/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;
import com.smartcampus.exception.ErrorResponse;
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
 * Sub-resource managing the reading history for a specific sensor.
 *
 * <p>This class is never registered directly with the JAX-RS runtime- it has no
 * class-level {@code @Path} annotation. Instead it is instantiated on demand by the
 * sub-resource locator in {@link SensorResource}, which validates the parent sensor
 * before constructing this object. As a result, every method here can safely assume
 * {@code sensor} is non-null and already confirmed to exist in the store.</p>
 *
 * @author aryanpaudel
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
 
    private final Sensor    sensor;
    private final DataStore store = DataStore.getInstance();
 
    /** Receives the validated parent sensor injected by the sub-resource locator. */
    public SensorReadingResource(Sensor sensor) {
        this.sensor = sensor;
    }
 
    /** Returns the full reading history for this sensor in insertion order. */
    @GET
    public Response getReadings() {
        List<SensorReading> history = store.getReadings(sensor.getId());
        return Response.ok(history).build();
    }
 
    /**
     * Appends a new reading to this sensor's history.
     *
     * <p>Status guards are evaluated before the null-check on the request body — if
     * both the sensor is OFFLINE and the body is null, the 403 is the correct response,
     * not a 400. Reversing this order would cause a NullPointerException on a null-body
     * request to an inactive sensor before the state guard is ever reached.</p>
     *
     * <p>A successful POST also updates the parent sensor's {@code currentValue} as a
     * side effect, keeping that denormalised field consistent without requiring a
     * separate PATCH call from the client.</p>
     */
    @POST
    public Response addReading(SensorReading reading) {
        // State guards first — a non-ACTIVE sensor rejects readings regardless of the body
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())
                || "OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensor.getId(), sensor.getStatus());
        }
 
        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(400, "Bad Request", "Reading body cannot be null."))
                    .build();
        }
 
        // Auto-populate fields the client may have omitted — reduces friction for hardware clients
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }
 
        store.addReading(sensor.getId(), reading); // also updates sensor.currentValue atomically
 
        URI location = URI.create("/api/v1/sensors/" + sensor.getId() 
        + "/readings/" + reading.getId());
 
        return Response.created(location).entity(reading).build();
    }
}

