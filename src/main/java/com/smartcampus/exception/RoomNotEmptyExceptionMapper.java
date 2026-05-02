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
 * Translates {@link RoomNotEmptyException} into a structured HTTP 409 Conflict response.
 *
 * <p>409 is correct here because the request is well-formed but conflicts with the
 * current resource state: the room exists but has dependencies that block deletion.
 * The response body lists the blocking sensor IDs so clients know exactly what to
 * resolve before retrying- avoiding a frustrating trial-and-error loop.</p>
 *
 * @author aryanpaudel
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
 
    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status",        409);
        body.put("error",         "Conflict");
        body.put("message",       "Room '" + ex.getRoomId()
                + "' cannot be deleted because it is currently occupied by active hardware.");
        body.put("activeSensors", ex.getSensorIds());
        body.put("hint",          "Decommission or reassign all sensors before deleting the room.");
 
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
 
