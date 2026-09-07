package com.bookingsystem.application.user;

import com.bookingsystem.domain.user.UserRole;
import java.time.Instant;

public record UserSummary(
		Long id,
		Long organizationId,
		String email,
		String fullName,
		UserRole role,
		boolean active,
		Instant createdAt) {
}
