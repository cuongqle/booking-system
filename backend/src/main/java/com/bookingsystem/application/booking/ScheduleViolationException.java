package com.bookingsystem.application.booking;

public class ScheduleViolationException extends RuntimeException {

	public ScheduleViolationException(String message) {
		super(message);
	}
}
