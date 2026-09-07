package com.bookingsystem.application.organization;

import com.bookingsystem.domain.organization.Organization;
import com.bookingsystem.domain.organization.OrganizationStatus;
import java.time.Instant;

public record OrganizationSummary(
		Long id,
		String name,
		String slug,
		OrganizationStatus status,
		Instant suspendedAt,
		String suspendedReason,
		long userCount,
		Instant createdAt) {

	public static OrganizationSummary from(Organization org, long userCount) {
		return new OrganizationSummary(
				org.getId(),
				org.getName(),
				org.getSlug(),
				org.getStatus(),
				org.getSuspendedAt(),
				org.getSuspendedReason(),
				userCount,
				org.getCreatedAt());
	}
}
