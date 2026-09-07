package com.bookingsystem.application.organization;

public class OrganizationNotFoundException extends RuntimeException {

	public OrganizationNotFoundException(String slug) {
		super("Organization not found: " + slug);
	}
}
