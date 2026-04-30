/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 *
 * @author aryanpaudel
 */
public class RoomNotFoundException extends RuntimeException {

    private final String roomId;

    public RoomNotFoundException(String roomId) {
        super("Room not found with ID: " + roomId);
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
