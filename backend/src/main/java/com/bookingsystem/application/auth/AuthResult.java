package com.bookingsystem.application.auth;

import com.bookingsystem.domain.user.UserRole;

public record AuthResult(
		String accessToken,
		Long userId,
		Long organizationId,
		String organizationName,
		String organizationSlug,
		String email,
		String fullName,
		UserRole role) {
}
