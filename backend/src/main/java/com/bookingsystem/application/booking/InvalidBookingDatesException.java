package com.bookingsystem.application.booking;

public class InvalidBookingDatesException extends RuntimeException {

	public InvalidBookingDatesException(String message) {
		super(message);
	}
}
