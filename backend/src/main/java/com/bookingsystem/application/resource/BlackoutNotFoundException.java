package com.bookingsystem.application.resource;

public class BlackoutNotFoundException extends RuntimeException {

	public BlackoutNotFoundException(Long id) {
		super("Blackout %d not found".formatted(id));
	}
}
