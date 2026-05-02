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
 * Translates {@link LinkedResourceNotFoundException} into HTTP 422 Unprocessable Entity.
 *
 * <p>422 is preferred over 404 because the endpoint itself was found and the JSON was
 * parseable - the issue is that a field inside the payload references something that
 * doesn't exist. Returning 404 would mislead the client into thinking they hit the wrong
 * URL. The response includes the offending field and its rejected value so the client
 * can correct the request immediately.</p>
 *
 * @author aryanpaudel
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> {
 
    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status",        422);
        body.put("error",         "Unprocessable Entity");
        body.put("message",       "The field '" + ex.getFieldName()
                + "' references a resource that does not exist: '"
                + ex.getFieldValue() + "'.");
        body.put("field",         ex.getFieldName());
        body.put("rejectedValue", ex.getFieldValue());
        body.put("hint",          "Ensure the referenced resource exists before linking to it.");
 
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
 

