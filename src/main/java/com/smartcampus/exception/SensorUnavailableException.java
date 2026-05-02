/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 * Signals that a reading was posted to a sensor that is not in an ACTIVE state.
 *
 * <p>A sensor in MAINTENANCE or OFFLINE state is physically disconnected or under
 * service- recording readings against it would produce misleading historical data.
 * The status is carried through so the mapper can tell the client exactly why the
 * request was rejected and what state the sensor is currently in.</p>
 * Mapped to HTTP 403 by {@link SensorUnavailableExceptionMapper}.
 *
 * @author aryanpaudel
 */
public class SensorUnavailableException extends RuntimeException {
 
    private final String sensorId;
    private final String status; // The blocking status, e.g. "MAINTENANCE" or "OFFLINE"
 
    public SensorUnavailableException(String sensorId, String status) {
        super("Sensor '" + sensorId + "' is unavailable. Current status: " + status);
        this.sensorId = sensorId;
        this.status   = status;
    }
 
    public String getSensorId() { return sensorId; }
    public String getStatus()   { return status; }
}
 
