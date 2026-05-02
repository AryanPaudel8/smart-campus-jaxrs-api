/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
/**
 * Translates {@link SensorUnavailableException} into HTTP 403 Forbidden.
 *
 * <p>403 is chosen because the sensor resource exists and the request is valid-
 * the server is refusing the action due to the sensor's current operational state.
 * This is a state-constraint rejection, not an authentication failure, making
 * 403 more semantically accurate than 401 or 503.</p>
 *
 * @author aryanpaudel
 */
@Provider
public class SensorUnavailableExceptionMapper
        implements ExceptionMapper<SensorUnavailableException> {
 
    @Override
    public Response toResponse(SensorUnavailableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status",        403);
        body.put("error",         "Forbidden");
        body.put("message",       "Cannot post a reading to sensor '" + ex.getSensorId()
                + "'. The sensor is currently in '" + ex.getStatus()
                + "' state and is not accepting new data.");
        body.put("sensorId",      ex.getSensorId());
        body.put("currentStatus", ex.getStatus());
 
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
