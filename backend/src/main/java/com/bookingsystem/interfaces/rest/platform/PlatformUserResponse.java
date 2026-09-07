package com.bookingsystem.interfaces.rest.platform;

import com.bookingsystem.application.user.UserSummary;
import com.bookingsystem.domain.user.UserRole;
import java.time.Instant;

public record PlatformUserResponse(
		Long id,
		Long organizationId,
		String email,
		String fullName,
		UserRole role,
		boolean active,
		Instant createdAt) {

	public static PlatformUserResponse from(UserSummary summary) {
		return new PlatformUserResponse(
				summary.id(),
				summary.organizationId(),
				summary.email(),
				summary.fullName(),
				summary.role(),
				summary.active(),
				summary.createdAt());
	}
}
