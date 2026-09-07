package com.bookingsystem.interfaces.rest.auth;

import com.bookingsystem.application.organization.InvalidOrganizationRegistrationException;
import com.bookingsystem.application.organization.OrganizationNotFoundException;
import com.bookingsystem.application.organization.OrganizationSuspendedException;
import com.bookingsystem.application.user.InvalidCredentialsException;
import com.bookingsystem.application.user.UserAlreadyExistsException;
import com.bookingsystem.application.user.UserInactiveException;
import com.bookingsystem.application.user.UserNotFoundException;
import com.bookingsystem.interfaces.rest.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler {

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyExists(UserAlreadyExistsException ex) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(HttpStatus.CONFLICT.value(), "USER_ALREADY_EXISTS", ex.getMessage()));
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "INVALID_CREDENTIALS", ex.getMessage()));
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(UserNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "USER_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(OrganizationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleOrganizationNotFound(OrganizationNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "ORGANIZATION_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(InvalidOrganizationRegistrationException.class)
	public ResponseEntity<ErrorResponse> handleInvalidOrganization(InvalidOrganizationRegistrationException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(
						HttpStatus.BAD_REQUEST.value(),
						"INVALID_ORGANIZATION",
						ex.getMessage()));
	}

	@ExceptionHandler(OrganizationSuspendedException.class)
	public ResponseEntity<ErrorResponse> handleOrganizationSuspended(OrganizationSuspendedException ex) {
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), "ORG_SUSPENDED", ex.getMessage()));
	}

	@ExceptionHandler(UserInactiveException.class)
	public ResponseEntity<ErrorResponse> handleUserInactive(UserInactiveException ex) {
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), "USER_INACTIVE", ex.getMessage()));
	}
}
