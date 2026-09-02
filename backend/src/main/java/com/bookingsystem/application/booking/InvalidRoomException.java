package com.bookingsystem.application.booking;

public class InvalidRoomException extends RuntimeException {

	public InvalidRoomException(String roomId) {
		super("Unknown room: %s".formatted(roomId));
	}
}
