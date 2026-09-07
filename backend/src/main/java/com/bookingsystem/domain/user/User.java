package com.bookingsystem.domain.user;

import java.time.Instant;

public class User {

	private Long id;
	private Long organizationId;
	private String email;
	private String passwordHash;
	private String fullName;
	private UserRole role;
	private boolean active;
	private Instant createdAt;
	private Instant updatedAt;

	public User() {
	}

	public User(
			Long id,
			Long organizationId,
			String email,
			String passwordHash,
			String fullName,
			UserRole role,
			boolean active,
			Instant createdAt,
			Instant updatedAt) {
		this.id = id;
		this.organizationId = organizationId;
		this.email = email;
		this.passwordHash = passwordHash;
		this.fullName = fullName;
		this.role = role;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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
