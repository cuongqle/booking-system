package com.bookingsystem.application.booking;

public class BookingNotFoundException extends RuntimeException {

	public BookingNotFoundException(Long id) {
		super("Booking %s not found".formatted(id));
	}
}
