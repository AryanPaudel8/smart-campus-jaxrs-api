/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 * Signals that a room with the requested ID does not exist in the data store.
 * Carrying the room ID here lets the mapper produce a precise error message
 * without needing to re-parse the exception message string.
 * Mapped to HTTP 404 by {@link RoomNotFoundExceptionMapper}.
 *
 * @author aryanpaudel
 */
public class RoomNotFoundException extends RuntimeException {
 
    private final String roomId;
 
    public RoomNotFoundException(String roomId) {
        super("Room not found with ID: " + roomId);
        this.roomId = roomId;
    }
 
    public String getRoomId() { return roomId; }
}

