/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;
import com.smartcampus.exception.ErrorResponse;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.exception.RoomNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.service.DataStore;
 
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JAX-RS resource managing the {@code /api/v1/rooms} collection.
 *
 * <p>Handles the full Room lifecycle: listing, creation, retrieval, and safe deletion.
 * All state is held in the {@link DataStore} singleton- this class is instantiated
 * fresh per request by the JAX-RS runtime so it must never hold mutable instance fields.</p>
 *
 * @author aryanpaudel
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
 
    private final DataStore store = DataStore.getInstance();
 
    /**
     * Returns all registered rooms.
     * Full Room objects are returned rather than ID-only summaries to avoid the N+1
     * request problem- forcing clients to make a separate call per room just to get
     * basic details would be a poor API experience.
     */
    @GET
    public Response getAllRooms() {
        Collection<Room> rooms = store.getAllRooms();
        return Response.ok(new ArrayList<>(rooms)).build();
    }
 
    /**
     * Creates a new room. Returns 201 Created with a Location header pointing to the
     * new resource, following the REST convention for successful POST operations.
     * Returns 409 if a room with the same ID already exists to prevent silent overwrites.
     */
    @POST
    public Response createRoom(Room room) {
        if (room == null || isBlank(room.getId()) || isBlank(room.getName())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(400, "Bad Request",
                            "Room body must include non-empty 'id' and 'name' fields."))
                    .build();
        }
        if (store.roomExists(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(409, "Conflict",
                            "A room with id '" + room.getId() + "' already exists."))
                    .build();
        }
 
        store.addRoom(room);
        URI location = UriBuilder.fromResource(RoomResource.class).path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }
 
    /** Returns full metadata for a single room, or 404 if it does not exist. */
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) throw new RoomNotFoundException(roomId);
        return Response.ok(room).build();
    }
 
    /**
     * Decommissions a room. Enforces the business constraint that a room cannot be
     * deleted while sensors are still assigned- doing so would leave orphaned sensor
     * records with a broken roomId reference. The exception carries the sensor list so
     * the client knows exactly what must be resolved before retrying.
     *
     * <p>This operation is not strictly idempotent: the first DELETE returns 204,
     * but a repeat call returns 404 because the resource no longer exists.
     * This is the correct REST behaviour- the server state is consistent either way.</p>
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) throw new RoomNotFoundException(roomId);
 
        List<String> sensorIds = room.getSensorIds();
        if (!sensorIds.isEmpty()) {
            // Block deletion to prevent orphaned sensor records
            throw new RoomNotEmptyException(roomId, new ArrayList<>(sensorIds));
        }
 
        store.deleteRoom(roomId);
        return Response.noContent().build();
    }
 
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
 
