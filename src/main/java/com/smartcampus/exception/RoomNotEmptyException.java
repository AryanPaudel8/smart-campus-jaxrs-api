/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;
import java.util.List;
/**
 * Signals that a room cannot be deleted because it still has sensors assigned to it.
 *
 * <p>Deleting a room with active sensors would produce orphaned sensor records that
 * reference a non-existent parent- a referential integrity violation in the in-memory
 * store. The sensor ID list is included so the mapper can surface exactly which sensors
 * must be decommissioned before the deletion can proceed.</p>
 * Mapped to HTTP 409 by {@link RoomNotEmptyExceptionMapper}.
 *
 * @author aryanpaudel
 */
public class RoomNotEmptyException extends RuntimeException {
 
    private final String       roomId;
    private final List<String> sensorIds;
 
    public RoomNotEmptyException(String roomId, List<String> sensorIds) {
        super("Cannot delete room '" + roomId + "': it still has "
                + sensorIds.size() + " sensor(s) assigned.");
        this.roomId    = roomId;
        this.sensorIds = sensorIds;
    }
 
    public String       getRoomId()    { return roomId; }
    public List<String> getSensorIds() { return sensorIds; }
}

