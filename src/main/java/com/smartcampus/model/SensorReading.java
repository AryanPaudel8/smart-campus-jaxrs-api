/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;

import java.util.UUID;
/**
 * Domain model representing a single timestamped data point from a sensor.
 *
 * <p>Readings are immutable records of what a sensor measured at a point in time.
 * The {@code id} and {@code timestamp} fields are auto-generated server-side when
 * not provided by the client, which keeps the POST contract simple for hardware
 * devices that may lack reliable clocks or UUID generation capabilities.</p>
 *
 * @author aryanpaudel
 */
public class SensorReading {
 
    private String id;        // UUID uniquely identifying this reading event
    private long timestamp;   // Epoch milliseconds — chosen over ISO strings for sorting efficiency
    private double value;     // The raw metric captured by the sensor hardware
 
    public SensorReading() {}
 
    /** Convenience constructor that auto-generates the id and timestamp. Used for seed data. */
    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }
 
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }
 
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
 
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
 
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}

