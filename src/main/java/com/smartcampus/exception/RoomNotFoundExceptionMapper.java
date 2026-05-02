/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
/**
 * Translates {@link RoomNotFoundException} into a structured HTTP 404 response.
 *
 * <p>Registering this as a {@code @Provider} means JAX-RS intercepts the exception
 * before it can propagate as a 500, giving clients a meaningful error rather than
 * an opaque server failure.</p>
 *
 * @author aryanpaudel
 */
@Provider
public class RoomNotFoundExceptionMapper implements ExceptionMapper<RoomNotFoundException> {
 
    @Override
    public Response toResponse(RoomNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(404, "Not Found", ex.getMessage());
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}

