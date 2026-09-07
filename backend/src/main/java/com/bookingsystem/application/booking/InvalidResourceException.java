package com.bookingsystem.application.booking;

public class InvalidResourceException extends RuntimeException {

	public InvalidResourceException(String resourceId) {
		super("Unknown or inactive resource: %s".formatted(resourceId));
	}
}
