package com.bookingsystem.application.user;

public class UserInactiveException extends RuntimeException {

	public UserInactiveException() {
		super("User account is deactivated");
	}
}
