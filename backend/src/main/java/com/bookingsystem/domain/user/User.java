package com.bookingsystem.domain.user;

import java.time.Instant;

public class User {

	private Long id;
	private String email;
	private String passwordHash;
	private String fullName;
	private Instant createdAt;
	private Instant updatedAt;

	public User() {
	}

	public User(
			Long id,
			String email,
			String passwordHash,
			String fullName,
			Instant createdAt,
			Instant updatedAt) {
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.fullName = fullName;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
