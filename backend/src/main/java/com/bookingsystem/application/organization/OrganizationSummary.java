package com.bookingsystem.application.organization;

public record OrganizationSummary(
		Long id,
		String name,
		String slug,
		long userCount,
		java.time.Instant createdAt) {
}
