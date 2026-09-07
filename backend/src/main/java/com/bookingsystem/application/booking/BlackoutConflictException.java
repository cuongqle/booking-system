package com.bookingsystem.application.booking;

public class BlackoutConflictException extends RuntimeException {

	public BlackoutConflictException(String resourceId) {
		super("Booking overlaps a blackout period for resource " + resourceId);
	}
}
