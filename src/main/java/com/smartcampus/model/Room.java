/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aryanpaudel
 */
/**
 * Represents a physical room on the Smart Campus.
 * Encapsulated POJO with full getters and setters.
 */
public class Room {
    private String id;           // Unique identifier, e.g., "LIB-301"
    private String name;         // Human-readable name, e.g., "Library Quiet Study"
    private int capacity;        // Maximum occupancy for safety regulations
    private List<String> sensorIds = new ArrayList<>(); // IDs of sensors deployed in this room
 
    // ── Constructors ──────────────────────────────────────────────────────────
 
    public Room() {}
 
    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }
 
    // ── Getters & Setters ─────────────────────────────────────────────────────
 
    public String getId() {
        return id;
    }
 
    public void setId(String id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public int getCapacity() {
        return capacity;
    }
 
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
 
    public List<String> getSensorIds() {
        return sensorIds;
    }
 
    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }
}


