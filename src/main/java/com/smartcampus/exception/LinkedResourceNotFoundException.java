/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 * Signals that a request payload references a resource by ID that does not exist.
 *
 * <p>The primary scenario is posting a new sensor with a {@code roomId} that has no
 * matching room. This is semantically different from a 404 (target endpoint missing) -
 * the request body is valid JSON but contains a broken foreign-key reference, making
 * HTTP 422 Unprocessable Entity the correct response code. Carrying the field name and
 * rejected value lets the mapper give the client actionable feedback without them having
 * to guess which field was the problem.</p>
 * Mapped to HTTP 422 by {@link LinkedResourceNotFoundExceptionMapper}.
 *
 * @author aryanpaudel
 */
public class LinkedResourceNotFoundException extends RuntimeException {
 
    private final String fieldName;  // The JSON field containing the broken reference, e.g. "roomId"
    private final String fieldValue; // The value that could not be resolved
 
    public LinkedResourceNotFoundException(String fieldName, String fieldValue) {
        super("Referenced resource not found: field='" + fieldName
                + "', value='" + fieldValue + "'");
        this.fieldName  = fieldName;
        this.fieldValue = fieldValue;
    }
 
    public String getFieldName()  { return fieldName; }
    public String getFieldValue() { return fieldValue; }
}

