package com.bookingsystem.interfaces.rest.platform;

import com.bookingsystem.application.organization.InvalidOrganizationRegistrationException;
import com.bookingsystem.application.organization.OrganizationNotFoundException;
import com.bookingsystem.application.organization.OrganizationSuspendedException;
import com.bookingsystem.application.user.InvalidUserManagementException;
import com.bookingsystem.application.user.UserNotFoundException;
import com.bookingsystem.interfaces.rest.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.bookingsystem.interfaces.rest.platform")
public class PlatformExceptionHandler {

	@ExceptionHandler(OrganizationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleOrgNotFound(OrganizationNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "ORGANIZATION_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "USER_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(InvalidOrganizationRegistrationException.class)
	public ResponseEntity<ErrorResponse> handleInvalidOrg(InvalidOrganizationRegistrationException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_ORGANIZATION", ex.getMessage()));
	}

	@ExceptionHandler(InvalidUserManagementException.class)
	public ResponseEntity<ErrorResponse> handleInvalidUser(InvalidUserManagementException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_USER_MANAGEMENT", ex.getMessage()));
	}

	@ExceptionHandler(OrganizationSuspendedException.class)
	public ResponseEntity<ErrorResponse> handleSuspended(OrganizationSuspendedException ex) {
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), "ORG_SUSPENDED", ex.getMessage()));
	}
}
