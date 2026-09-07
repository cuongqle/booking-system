package com.bookingsystem.interfaces.rest.auth;

import com.bookingsystem.domain.user.UserRole;

public record AuthResponse(
		String accessToken,
		String tokenType,
		Long userId,
		Long organizationId,
		String organizationName,
		String organizationSlug,
		String email,
		String fullName,
		UserRole role) {

	public static AuthResponse bearer(
			String accessToken,
			Long userId,
			Long organizationId,
			String organizationName,
			String organizationSlug,
			String email,
			String fullName,
			UserRole role) {
		return new AuthResponse(
				accessToken,
				"Bearer",
				userId,
				organizationId,
				organizationName,
				organizationSlug,
				email,
				fullName,
				role);
	}
}
