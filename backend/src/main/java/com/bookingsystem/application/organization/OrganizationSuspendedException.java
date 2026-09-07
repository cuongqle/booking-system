package com.bookingsystem.application.organization;

public class OrganizationSuspendedException extends RuntimeException {

	public OrganizationSuspendedException(String organizationLabel) {
		super("Organization is suspended: " + organizationLabel);
	}
}
