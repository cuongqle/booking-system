package com.bookingsystem.interfaces.rest.admin;

import com.bookingsystem.application.resource.BlackoutNotFoundException;
import com.bookingsystem.application.resource.InvalidResourcePolicyException;
import com.bookingsystem.application.resource.ResourceAlreadyExistsException;
import com.bookingsystem.application.resource.ResourceNotFoundException;
import com.bookingsystem.interfaces.rest.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AdminResourceController.class)
public class AdminResourceExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "RESOURCE_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(BlackoutNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleBlackoutNotFound(BlackoutNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "BLACKOUT_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleConflict(ResourceAlreadyExistsException ex) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(HttpStatus.CONFLICT.value(), "RESOURCE_ALREADY_EXISTS", ex.getMessage()));
	}

	@ExceptionHandler(InvalidResourcePolicyException.class)
	public ResponseEntity<ErrorResponse> handleInvalidPolicy(InvalidResourcePolicyException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(
						HttpStatus.BAD_REQUEST.value(),
						"INVALID_RESOURCE_POLICY",
						ex.getMessage()));
	}
}
