/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 * Signals that a sensor with the requested ID does not exist in the data store.
 * Also thrown by the sub-resource locator in {@link com.smartcampus.resource.SensorResource}
 * to guard access to the readings sub-resource before it is even constructed.
 * Mapped to HTTP 404 by {@link SensorNotFoundExceptionMapper}.
 *
 * @author aryanpaudel
 */
public class SensorNotFoundException extends RuntimeException {
 
    private final String sensorId;
 
    public SensorNotFoundException(String sensorId) {
        super("Sensor not found with ID: " + sensorId);
        this.sensorId = sensorId;
    }
 
    public String getSensorId() { return sensorId; }
}
 
