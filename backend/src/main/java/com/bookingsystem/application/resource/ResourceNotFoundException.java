package com.bookingsystem.application.resource;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String id) {
		super("Resource %s not found".formatted(id));
	}
}
