/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;
import java.util.ArrayList;
import java.util.List;
/**
 * Domain model representing a physical room on the Smart Campus.
 *
 * <p>A Room is the parent entity in the core resource hierarchy. It owns a list of
 * sensor IDs rather than full {@link Sensor} objects to avoid circular references
 * during JSON serialisation and to keep the payload lightweight- clients that need
 * full sensor details can follow up with a dedicated sensor request.</p>
 *
 * @author aryanpaudel
 */
public class Room {
 
    private String id;           // Unique identifier, e.g. "LIB-301"
    private String name;         // Human-readable label, e.g. "Library Quiet Study"
    private int capacity;        // Maximum occupancy for safety regulations
    private List<String> sensorIds = new ArrayList<>(); // Child sensor references; initialised here to prevent NPEs on serialisation
 
    public Room() {}
 
    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }
 
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
 
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
 
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
 
    public List<String> getSensorIds() { return sensorIds; }
    public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }
}



