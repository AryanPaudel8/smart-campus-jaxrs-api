/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;
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
 *
 * @author aryanpaudel
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
      private final DataStore store = DataStore.getInstance();
 
    // ── GET /sensors[?type=...] ───────────────────────────────────────────────
    /**
     * Returns all sensors, optionally filtered by type via query parameter.
     * Example: GET /api/v1/sensors?type=CO2
     */
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        Collection<Sensor> all = store.getAllSensors();
        List<Sensor> result;
 
        if (type != null && !type.trim().isEmpty()) {
            // Case-insensitive filter so ?type=co2 and ?type=CO2 both work
            final String typeLower = type.trim().toLowerCase();
            result = all.stream()
                    .filter(s -> s.getType() != null
                            && s.getType().toLowerCase().equals(typeLower))
                    .collect(Collectors.toList());
        } else {
            result = new ArrayList<>(all);
        }
 
        return Response.ok(result).build();
    }
 
    // ── GET /sensors/{sensorId} ───────────────────────────────────────────────
    /**
     * Returns the full details for a single sensor.
     */
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            throw new SensorNotFoundException(sensorId);
        }
        return Response.ok(sensor).build();
    }
 
    // ── POST /sensors ─────────────────────────────────────────────────────────
    /**
     * Registers a new sensor.
     * Validates that the referenced roomId actually exists (Part 3.1 integrity check).
     * If roomId is missing/unknown → throws LinkedResourceNotFoundException (→ 422).
     * Returns HTTP 201 Created with Location header.
     */
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || isBlank(sensor.getType())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error(400, "Bad Request",
                            "Sensor body must include a 'type' field."))
                    .build();
        }
 
        // Foreign-key integrity check: roomId must exist
        if (isBlank(sensor.getRoomId()) || !store.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("roomId",
                    sensor.getRoomId() == null ? "(null)" : sensor.getRoomId());
        }
 
        // Auto-generate ID if not supplied
        if (isBlank(sensor.getId())) {
            sensor.setId(sensor.getType().toUpperCase() + "-" +
                    UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
 
        // Default status to ACTIVE if not provided
        if (isBlank(sensor.getStatus())) {
            sensor.setStatus("ACTIVE");
        }
 
        // addSensor registers the sensor AND appends its id to the room's sensorIds list
        store.addSensor(sensor);
 
        URI location = UriBuilder.fromResource(SensorResource.class)
                .path(sensor.getId()).build();
        return Response.created(location).entity(sensor).build();
    }
 
    // ── Sub-Resource Locator: /sensors/{sensorId}/readings ───────────────────
    /**
     * Part 4.1 — Sub-Resource Locator Pattern
     *
     * This method does NOT handle the request itself; instead it returns an
     * instance of SensorReadingResource, which JAX-RS then uses to handle all
     * paths under /sensors/{sensorId}/readings.
     *
     * Why sub-resource locators?
     * They delegate responsibility to a dedicated class, keeping each class
     * focused and small. The alternative — defining every nested path in one
     * giant controller — violates the Single Responsibility Principle and makes
     * code hard to read, test, and maintain as the API grows.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            throw new SensorNotFoundException(sensorId);
        }
        // Inject the validated sensor into the sub-resource
        return new SensorReadingResource(sensor);
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
