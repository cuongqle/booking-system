package com.bookingsystem.application.notification;

public class NotificationNotFoundException extends RuntimeException {

	public NotificationNotFoundException(Long id) {
		super("Notification %d not found".formatted(id));
	}
}
