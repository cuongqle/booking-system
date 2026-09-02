package com.bookingsystem.interfaces.rest.booking;

import com.bookingsystem.application.booking.BookingConflictException;
import com.bookingsystem.application.booking.BookingNotFoundException;
import com.bookingsystem.application.booking.InvalidBookingDatesException;
import com.bookingsystem.application.booking.InvalidRoomException;
import com.bookingsystem.interfaces.rest.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = BookingController.class)
public class BookingExceptionHandler {

	@ExceptionHandler(InvalidBookingDatesException.class)
	public ResponseEntity<ErrorResponse> handleInvalidDates(InvalidBookingDatesException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_BOOKING_DATES", ex.getMessage()));
	}

	@ExceptionHandler(InvalidRoomException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRoom(InvalidRoomException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_ROOM", ex.getMessage()));
	}

	@ExceptionHandler(BookingConflictException.class)
	public ResponseEntity<ErrorResponse> handleConflict(BookingConflictException ex) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(HttpStatus.CONFLICT.value(), "BOOKING_CONFLICT", ex.getMessage()));
	}

	@ExceptionHandler(BookingNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(BookingNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "BOOKING_NOT_FOUND", ex.getMessage()));
	}
}
