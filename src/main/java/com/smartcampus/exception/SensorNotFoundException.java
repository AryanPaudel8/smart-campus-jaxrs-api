/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
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