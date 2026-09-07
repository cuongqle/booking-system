package com.bookingsystem.interfaces.rest.auth;

import com.bookingsystem.application.auth.AuthResult;
import com.bookingsystem.application.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return toResponse(authService.register(request.toCommand()));
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return toResponse(authService.login(request.toCommand()));
	}

	private AuthResponse toResponse(AuthResult result) {
		return AuthResponse.bearer(
				result.accessToken(),
				result.userId(),
				result.organizationId(),
				result.organizationName(),
				result.organizationSlug(),
				result.email(),
				result.fullName(),
				result.role());
	}
}
