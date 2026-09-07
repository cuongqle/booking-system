package com.bookingsystem.application.booking;

public class BookingConflictException extends RuntimeException {

	public BookingConflictException(String resourceId) {
		super("Resource %s is already booked for the selected time range".formatted(resourceId));
	}
}
