package com.bookingsystem.interfaces.rest.auth;

import com.bookingsystem.domain.user.UserRole;

public record AuthResponse(
		String accessToken,
		String tokenType,
		Long userId,
		String email,
		String fullName,
		UserRole role) {

	public static AuthResponse bearer(
			String accessToken,
			Long userId,
			String email,
			String fullName,
			UserRole role) {
		return new AuthResponse(accessToken, "Bearer", userId, email, fullName, role);
	}
}
