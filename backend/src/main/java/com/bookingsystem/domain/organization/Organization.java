package com.bookingsystem.domain.organization;

import java.time.Instant;

public class Organization {

	private Long id;
	private String name;
	private String slug;
	private OrganizationStatus status;
	private Instant suspendedAt;
	private String suspendedReason;
	private Instant createdAt;
	private Instant updatedAt;

	public Organization() {
	}

	public Organization(
			Long id,
			String name,
			String slug,
			OrganizationStatus status,
			Instant suspendedAt,
			String suspendedReason,
			Instant createdAt,
			Instant updatedAt) {
		this.id = id;
		this.name = name;
		this.slug = slug;
		this.status = status == null ? OrganizationStatus.ACTIVE : status;
		this.suspendedAt = suspendedAt;
		this.suspendedReason = suspendedReason;
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

	public OrganizationStatus getStatus() {
		return status;
	}

	public void setStatus(OrganizationStatus status) {
		this.status = status;
	}

	public Instant getSuspendedAt() {
		return suspendedAt;
	}

	public void setSuspendedAt(Instant suspendedAt) {
		this.suspendedAt = suspendedAt;
	}

	public String getSuspendedReason() {
		return suspendedReason;
	}

	public void setSuspendedReason(String suspendedReason) {
		this.suspendedReason = suspendedReason;
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

	public boolean isSuspended() {
		return status == OrganizationStatus.SUSPENDED;
	}
}
