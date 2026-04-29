/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;
import java.util.List;
/**
 *
 * @author aryanpaudel
 */
public class RoomNotEmptyException extends RuntimeException {
 
    private final String roomId;
    private final List<String> sensorIds;
 
    public RoomNotEmptyException(String roomId, List<String> sensorIds) {
        super("Cannot delete room '" + roomId + "': it still has "
                + sensorIds.size() + " sensor(s) assigned.");
        this.roomId = roomId;
        this.sensorIds = sensorIds;
    }
 
    public String getRoomId() {
        return roomId;
    }
 
    public List<String> getSensorIds() {
        return sensorIds;
    }
}
