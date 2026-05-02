/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;
import com.smartcampus.exception.ErrorResponse;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.exception.SensorNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.service.DataStore;
 
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JAX-RS resource managing the {@code /api/v1/sensors} collection and acting as the
 * gateway to the nested readings sub-resource.
 *
 * <p>Sensor creation enforces referential integrity by verifying the supplied
 * {@code roomId} maps to an existing room before persisting- without this check,
 * sensors could reference rooms that never existed or were already deleted.</p>
 *
 * @author aryanpaudel
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
 
    private final DataStore store = DataStore.getInstance();
 
    /**
     * Returns all sensors, optionally filtered by type via a query parameter.
     *
     * <p>A query parameter ({@code ?type=CO2}) is used for filtering rather than a path
     * segment ({@code /sensors/type/CO2}) because filters and searches are optional
     * modifiers on a collection, not distinct sub-resources. Path parameters imply a
     * fixed hierarchy; query parameters signal that the collection itself is being
     * narrowed- a cleaner and more conventional REST design.</p>
     */
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        Collection<Sensor> all = store.getAllSensors();
        List<Sensor> result;
 
        if (type != null && !type.trim().isEmpty()) {
            // Case-insensitive so ?type=co2, ?type=CO2, and ?type=Co2 all behave identically
            final String typeLower = type.trim().toLowerCase();
            result = all.stream()
                    .filter(s -> s.getType() != null && s.getType().toLowerCase().equals(typeLower))
                    .collect(Collectors.toList());
        } else {
            result = new ArrayList<>(all);
        }
 
        return Response.ok(result).build();
    }
 
    /** Returns full metadata for a single sensor, or 404 if it does not exist. */
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) throw new SensorNotFoundException(sensorId);
        return Response.ok(sensor).build();
    }
 
    /**
     * Registers a new sensor. Validates that the supplied {@code roomId} references an
     * existing room before persisting- a 422 is returned rather than 404 because the
     * endpoint was found; the problem is a broken foreign-key reference inside the body.
     *
     * <p>The {@code @Consumes(APPLICATION_JSON)} annotation means JAX-RS will reject any
     * request with a mismatched Content-Type (e.g. text/plain) with 415 Unsupported Media
     * Type before the method body is ever reached.</p>
     *
     * <p>If {@code id} or {@code status} are absent the API fills sensible defaults,
     * reducing friction for clients that only know what type of sensor they're registering.</p>
     */
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || isBlank(sensor.getType())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(400, "Bad Request",
                            "Sensor body must include a 'type' field."))
                    .build();
        }
 
        // Foreign-key integrity check — roomId must reference a real room
        if (isBlank(sensor.getRoomId()) || !store.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("roomId",
                    sensor.getRoomId() == null ? "(null)" : sensor.getRoomId());
        }
 
        if (isBlank(sensor.getId())) {
            sensor.setId(sensor.getType().toUpperCase() + "-"
                    + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (isBlank(sensor.getStatus())) {
            sensor.setStatus("ACTIVE"); // Sensible default: a newly installed sensor is operational
        }
 
        store.addSensor(sensor);
        URI location = UriBuilder.fromResource(SensorResource.class).path(sensor.getId()).build();
        return Response.created(location).entity(sensor).build();
    }
 
    /**
     * Sub-resource locator that delegates all {@code /sensors/{sensorId}/readings} requests
     * to {@link SensorReadingResource}.
     *
     * <p>This method intentionally has no HTTP verb annotation- JAX-RS treats it as a
     * locator rather than a handler, constructing and dispatching to the returned instance.
     * The pattern keeps reading logic in its own class (Single Responsibility) and allows
     * the parent to validate the sensor exists before the child resource is even created,
     * so {@link SensorReadingResource} can safely assume its sensor is valid.</p>
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) throw new SensorNotFoundException(sensorId);
        return new SensorReadingResource(sensor);
    }
 
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
