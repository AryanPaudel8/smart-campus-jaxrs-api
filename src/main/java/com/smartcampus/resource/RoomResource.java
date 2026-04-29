/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;
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
 *
 * @author aryanpaudel
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final DataStore store = DataStore.getInstance();
 
    // ── GET /rooms ────────────────────────────────────────────────────────────
    /**
     * Returns the full list of all rooms currently registered in the system.
     * HTTP 200 OK with JSON array body.
     */
    @GET
    public Response getAllRooms() {
        Collection<Room> rooms = store.getAllRooms();
        return Response.ok(new ArrayList<>(rooms)).build();
    }
 
    // ── POST /rooms ───────────────────────────────────────────────────────────
    /**
     * Creates a new room.
     * Validates that id and name are not blank.
     * Returns HTTP 201 Created with a Location header and the created room body.
     */
    @POST
    public Response createRoom(Room room) {
        if (room == null || isBlank(room.getId()) || isBlank(room.getName())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error(400, "Bad Request",
                            "Room body must include non-empty 'id' and 'name' fields."))
                    .build();
        }
        if (store.roomExists(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(error(409, "Conflict",
                            "A room with id '" + room.getId() + "' already exists."))
                    .build();
        }
        store.addRoom(room);
        URI location = UriBuilder.fromResource(RoomResource.class)
                .path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }
 
    // ── GET /rooms/{roomId} ───────────────────────────────────────────────────
    /**
     * Returns the full metadata for a single room.
     * Throws RoomNotFoundException (→ 404) if not found.
     */
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            throw new RoomNotFoundException(roomId);
        }
        return Response.ok(room).build();
    }
 
    // ── DELETE /rooms/{roomId} ────────────────────────────────────────────────
    /**
     * Decommissions a room.
     * Business constraint: room cannot be deleted if it has sensors assigned.
     * → Throws RoomNotEmptyException (→ 409 Conflict) in that case.
     * Returns 204 No Content on success.
     * Returns 404 if the room does not exist (idempotent: state is already "gone").
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            throw new RoomNotFoundException(roomId);
        }
        // Business logic guard: block deletion if sensors are still assigned
        List<String> sensorIds = room.getSensorIds();
        if (!sensorIds.isEmpty()) {
            throw new RoomNotEmptyException(roomId, new ArrayList<>(sensorIds));
        }
        store.deleteRoom(roomId);
        return Response.noContent().build();
    }
 
    // ── Private helpers ───────────────────────────────────────────────────────
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
 
    private java.util.Map<String, Object> error(int status, String error, String message) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("status", status);
        map.put("error", error);
        map.put("message", message);
        return map;
    }
}
