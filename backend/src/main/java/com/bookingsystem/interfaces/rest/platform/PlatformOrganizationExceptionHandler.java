package com.bookingsystem.interfaces.rest.platform;

import com.bookingsystem.application.organization.InvalidOrganizationRegistrationException;
import com.bookingsystem.application.organization.OrganizationNotFoundException;
import com.bookingsystem.interfaces.rest.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = PlatformOrganizationController.class)
public class PlatformOrganizationExceptionHandler {

	@ExceptionHandler(OrganizationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(OrganizationNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "ORGANIZATION_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(InvalidOrganizationRegistrationException.class)
	public ResponseEntity<ErrorResponse> handleInvalid(InvalidOrganizationRegistrationException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_ORGANIZATION", ex.getMessage()));
	}
}
