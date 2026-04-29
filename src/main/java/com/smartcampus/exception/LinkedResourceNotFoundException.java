/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 *
 * @author aryanpaudel
 */
public class LinkedResourceNotFoundException extends RuntimeException {
 
    private final String fieldName;
    private final String fieldValue;
 
    public LinkedResourceNotFoundException(String fieldName, String fieldValue) {
        super("Referenced resource not found: field='" + fieldName
                + "', value='" + fieldValue + "'");
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
 
    public String getFieldName() {
        return fieldName;
    }
 
    public String getFieldValue() {
        return fieldValue;
    }
}
 
