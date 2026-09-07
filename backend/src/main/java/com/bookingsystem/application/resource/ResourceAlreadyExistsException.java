package com.bookingsystem.application.resource;

public class ResourceAlreadyExistsException extends RuntimeException {

	public ResourceAlreadyExistsException(String id) {
		super("Resource %s already exists".formatted(id));
	}
}
