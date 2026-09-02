package com.bookingsystem.application.booking;

public class BookingConflictException extends RuntimeException {

	public BookingConflictException(String roomId) {
		super("Room %s is already booked for the selected time range".formatted(roomId));
	}
}
