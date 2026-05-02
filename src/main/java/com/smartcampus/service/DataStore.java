/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.service;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
 
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory persistence layer for the Smart Campus API.
 *
 * <p>Implements the Singleton pattern via eager initialisation- the single instance
 * is created at class-load time by the JVM, which is inherently thread-safe without
 * requiring synchronization on {@code getInstance()}. This is necessary because JAX-RS
 * creates a new resource class instance per HTTP request, so shared state must live
 * outside the resource layer.</p>
 *
 * <p>All collections use {@link ConcurrentHashMap} to allow safe concurrent reads.
 * Write operations that must span multiple collections atomically (e.g. adding a sensor
 * and updating its parent room's sensor list in the same step) are further guarded with
 * {@code synchronized} to prevent race conditions under concurrent requests.</p>
 *
 * <p>Business rule validation (e.g. checking whether a room is empty before deletion)
 * is intentionally kept in the resource layer, not here. This keeps the store's methods
 * single-purpose and prevents incorrect exception types from leaking out of the data tier.</p>
 *
 * @author aryanpaudel
 */
public class DataStore {
 
    private static final DataStore INSTANCE = new DataStore();
 
    public static DataStore getInstance() {
        return INSTANCE;
    }
 
    private final ConcurrentHashMap<String, Room>          rooms    = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Sensor>        sensors  = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<SensorReading>> readings = new ConcurrentHashMap<>();
 
    /**
     * Seeds representative data on startup so the API is immediately usable for
     * demonstration and testing without requiring any prior setup calls.
     * Includes one MAINTENANCE sensor to showcase the 403 state-constraint scenario.
     */
    private DataStore() {
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-101", "Computer Science Lab", 30);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
 
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE",      22.5,  "LIB-301");
        Sensor s2 = new Sensor("CO2-001",  "CO2",         "ACTIVE",      400.0, "LIB-301");
        Sensor s3 = new Sensor("OCC-001",  "Occupancy",   "MAINTENANCE", 0.0,   "LAB-101");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);
 
        // Keep the room's sensorIds list in sync with the sensors map from the start
        r1.getSensorIds().add(s1.getId());
        r1.getSensorIds().add(s2.getId());
        r2.getSensorIds().add(s3.getId());
 
        readings.put(s1.getId(), new ArrayList<>());
        readings.get(s1.getId()).add(new SensorReading(22.5));
        readings.put(s2.getId(), new ArrayList<>());
        readings.put(s3.getId(), new ArrayList<>());
    }
 
    // ── Room operations ───────────────────────────────────────────────────────
 
    public Collection<Room> getAllRooms()          { return rooms.values(); }
    public Room             getRoom(String id)     { return rooms.get(id); }
    public boolean          roomExists(String id)  { return rooms.containsKey(id); }
    public void             addRoom(Room room)     { rooms.put(room.getId(), room); }
 
    /**
     * Removes a room from the store. The caller (resource layer) is responsible for
     * verifying the room exists and has no sensors before invoking this method —
     * keeping business-rule validation out of the data tier.
     */
    public synchronized boolean deleteRoom(String id) {
        if (!rooms.containsKey(id)) return false;
        rooms.remove(id);
        return true;
    }
 
    // ── Sensor operations ─────────────────────────────────────────────────────
 
    public Collection<Sensor> getAllSensors()         { return sensors.values(); }
    public Sensor             getSensor(String id)    { return sensors.get(id); }
    public boolean            sensorExists(String id) { return sensors.containsKey(id); }
    public void               updateSensor(Sensor s)  { sensors.put(s.getId(), s); }
 
    /**
     * Registers a sensor and appends its ID to the parent room's list atomically.
     * Both writes are inside a single {@code synchronized} block to prevent a race
     * where two concurrent POSTs could each pass the room-exists check but only one
     * correctly updates the room's sensor list.
     */
    public synchronized void addSensor(Sensor sensor) {
        Room room = rooms.get(sensor.getRoomId());
        if (room == null) throw new IllegalArgumentException("LINKED_RESOURCE_NOT_FOUND");
        sensors.put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());
        readings.put(sensor.getId(), new ArrayList<>());
    }
 
    // ── Reading operations ────────────────────────────────────────────────────
 
    public List<SensorReading> getReadings(String sensorId) {
        return readings.computeIfAbsent(sensorId, k -> new ArrayList<>());
    }
 
    /**
     * Appends a reading and updates the parent sensor's {@code currentValue} in
     * one synchronised step, keeping the denormalised snapshot consistent with
     * history.
     */
    public synchronized void addReading(String sensorId, SensorReading reading) {
        readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) sensor.setCurrentValue(reading.getValue());
    }
}






