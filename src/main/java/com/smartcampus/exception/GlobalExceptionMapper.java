/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author aryanpaudel
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
 
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());
 
    @Override
    public Response toResponse(Throwable ex) {
        // Log the full details server-side (never sent to the client)
        LOGGER.log(Level.SEVERE, "Unhandled exception caught by global mapper", ex);
 
        ErrorResponse body = new ErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred. Please contact the system administrator."
        );
 
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
