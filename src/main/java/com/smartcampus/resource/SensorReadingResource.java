/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.service.DataStore;
 
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;
/**
 *
 * @author aryanpaudel
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    private final Sensor sensor;
    private final DataStore store = DataStore.getInstance();
 
    /**
     * Constructor called by the sub-resource locator in SensorResource.
     * The parent sensor is validated and injected here.
     */
    public SensorReadingResource(Sensor sensor) {
        this.sensor = sensor;
    }
 
    // ── GET /sensors/{sensorId}/readings ──────────────────────────────────────
    /**
     * Returns the full historical reading list for this sensor.
     */
    @GET
    public Response getReadings() {
        List<SensorReading> history = store.getReadings(sensor.getId());
        return Response.ok(history).build();
    }
 
    // ── POST /sensors/{sensorId}/readings ─────────────────────────────────────
    /**
     * Appends a new reading for this sensor.
     *
     * State Constraint (Part 5.3):
     * If the sensor's status is "MAINTENANCE", the request is blocked and
     * SensorUnavailableException is thrown (→ 403 Forbidden).
     *
     * Side Effect (Part 4.2):
     * A successful POST updates the parent sensor's currentValue to the newly
     * recorded value, ensuring data consistency across the API.
     */
    @POST
    public Response addReading(SensorReading reading) {
        // State constraint guard (Part 5.3)
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensor.getId(), sensor.getStatus());
        }
 
        // Also guard OFFLINE sensors — they cannot record readings either
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensor.getId(), sensor.getStatus());
        }
 
        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error(400, "Bad Request", "Reading body cannot be null."))
                    .build();
        }
 
        // Auto-populate id and timestamp if not provided by client
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }
 
        // Persist reading AND update parent sensor's currentValue (side effect)
        store.addReading(sensor.getId(), reading);
 
        // Build Location header pointing to the new reading
        URI location = UriBuilder.fromResource(SensorResource.class)
                .path(sensor.getId())
                .path("readings")
                .path(reading.getId())
                .build();
 
        return Response.created(location).entity(reading).build();
    }
 
    // ── Private helpers ───────────────────────────────────────────────────────
    private java.util.Map<String, Object> error(int status, String error, String message) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("status", status);
        map.put("error", error);
        map.put("message", message);
        return map;
    }
}
