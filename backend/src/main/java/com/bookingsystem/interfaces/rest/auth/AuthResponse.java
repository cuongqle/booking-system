package com.bookingsystem.interfaces.rest.auth;

public record AuthResponse(String accessToken, String tokenType, Long userId, String email, String fullName) {

	public static AuthResponse bearer(String accessToken, Long userId, String email, String fullName) {
		return new AuthResponse(accessToken, "Bearer", userId, email, fullName);
	}
}
