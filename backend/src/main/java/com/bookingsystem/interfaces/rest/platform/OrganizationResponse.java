package com.bookingsystem.interfaces.rest.platform;

import com.bookingsystem.application.organization.OrganizationSummary;
import java.time.Instant;

public record OrganizationResponse(
		Long id,
		String name,
		String slug,
		long userCount,
		Instant createdAt) {

	public static OrganizationResponse from(OrganizationSummary summary) {
		return new OrganizationResponse(
				summary.id(),
				summary.name(),
				summary.slug(),
				summary.userCount(),
				summary.createdAt());
	}
}
