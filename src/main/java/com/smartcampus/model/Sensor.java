/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;

/**
 * Domain model representing an IoT sensor deployed within a campus room.
 *
 * <p>The {@code status} field drives business logic across the API: only sensors
 * in {@code "ACTIVE"} state can accept new readings. The {@code currentValue}
 * field acts as a denormalised snapshot- it is updated as a side effect of each
 * successful reading POST so clients can retrieve the latest measurement without
 * querying the full reading history.</p>
 *
 * @author aryanpaudel
 */
public class Sensor {
 
    private String id;            // Unique identifier, e.g. "TEMP-001"
    private String type;          // Measurement category: "Temperature", "CO2", "Occupancy"
    private String status;        // Operational state: "ACTIVE", "MAINTENANCE", "OFFLINE"
    private double currentValue;  // Denormalised snapshot of the most recent reading
    private String roomId;        // Foreign key to the parent Room — enforced on creation
 
    public Sensor() {}
 
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }
 
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
 
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
 
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
 
    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }
 
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}
