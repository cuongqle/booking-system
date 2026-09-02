package com.bookingsystem.interfaces.rest.auth;

import com.bookingsystem.application.user.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Email String email,
		@NotBlank @Size(min = 8, max = 100) String password,
		@NotBlank @Size(max = 255) String fullName) {

	public RegisterUserCommand toCommand() {
		return new RegisterUserCommand(email, password, fullName);
	}
}
