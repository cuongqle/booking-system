package com.bookingsystem.application.user;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(Long id) {
		super("User %s not found".formatted(id));
	}
}
