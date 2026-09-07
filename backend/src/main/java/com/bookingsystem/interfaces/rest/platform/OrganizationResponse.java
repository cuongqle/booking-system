package com.bookingsystem.interfaces.rest.platform;

import com.bookingsystem.application.organization.OrganizationSummary;
import com.bookingsystem.domain.organization.OrganizationStatus;
import java.time.Instant;

public record OrganizationResponse(
		Long id,
		String name,
		String slug,
		OrganizationStatus status,
		Instant suspendedAt,
		String suspendedReason,
		long userCount,
		Instant createdAt) {

	public static OrganizationResponse from(OrganizationSummary summary) {
		return new OrganizationResponse(
				summary.id(),
				summary.name(),
				summary.slug(),
				summary.status(),
				summary.suspendedAt(),
				summary.suspendedReason(),
				summary.userCount(),
				summary.createdAt());
	}
}
