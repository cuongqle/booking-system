package com.bookingsystem.interfaces.rest.notification;

import com.bookingsystem.application.notification.NotificationNotFoundException;
import com.bookingsystem.interfaces.rest.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = NotificationController.class)
public class NotificationExceptionHandler {

	@ExceptionHandler(NotificationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(NotificationNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(
						HttpStatus.NOT_FOUND.value(),
						"NOTIFICATION_NOT_FOUND",
						ex.getMessage()));
	}
}
