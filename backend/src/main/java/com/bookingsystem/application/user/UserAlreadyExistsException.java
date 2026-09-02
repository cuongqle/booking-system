package com.bookingsystem.application.user;

public class UserAlreadyExistsException extends RuntimeException {

	public UserAlreadyExistsException(String email) {
		super("User already exists: %s".formatted(email));
	}
}
