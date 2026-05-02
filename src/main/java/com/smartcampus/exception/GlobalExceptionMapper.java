/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Last-resort exception mapper that catches any {@link Throwable} not handled by a
 * more specific mapper, ensuring the API never leaks stack traces to external clients.
 *
 * <p>Exposing raw stack traces is a security risk: they reveal internal class names,
 * framework versions, and file paths that an attacker can use to identify vulnerabilities
 * or reconstruct the system's architecture. This mapper logs the full detail server-side
 * for debugging while returning only a safe, generic message to the caller.</p>
 *
 * <p>JAX-RS resolves mappers from most-specific to least-specific exception type, so this
 * only fires when no other registered mapper matched — acting as a genuine safety net
 * rather than overriding targeted handlers.</p>
 *
 * @author aryanpaudel
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {

        // If it's a JAX-RS built-in exception (404, 405, 415 etc.)
        // let it pass through with its real status code instead of
        // converting everything to 500
        if (ex instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) ex;
            int status = wae.getResponse().getStatus();

            // Only log if it's actually a server error (5xx)
            if (status >= 500) {
                LOGGER.log(Level.SEVERE, "Server error: ", ex);
            }

            // Return the real HTTP status with a clean JSON body
            String reason = wae.getResponse().getStatusInfo().getReasonPhrase();
            ErrorResponse body = new ErrorResponse(status, reason, ex.getMessage());
            return Response.status(status)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(body)
                    .build();
        }

        // Everything else is a genuine unexpected server error — log and hide internals
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

