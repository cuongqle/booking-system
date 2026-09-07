package com.bookingsystem.domain.organization;

import java.time.Instant;

public class Organization {

	private Long id;
	private String name;
	private String slug;
	private Instant createdAt;
	private Instant updatedAt;

	public Organization() {
	}

	public Organization(Long id, String name, String slug, Instant createdAt, Instant updatedAt) {
		this.id = id;
		this.name = name;
		this.slug = slug;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
