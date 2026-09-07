package com.bookingsystem.domain.resource;

import java.time.Instant;
import java.time.LocalDateTime;

public class ResourceBlackout {

	private Long id;
	private String resourceId;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private String reason;
	private Instant createdAt;

	public ResourceBlackout() {
	}

	public ResourceBlackout(
			Long id,
			String resourceId,
			LocalDateTime startAt,
			LocalDateTime endAt,
			String reason,
			Instant createdAt) {
		this.id = id;
		this.resourceId = resourceId;
		this.startAt = startAt;
		this.endAt = endAt;
		this.reason = reason;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public LocalDateTime getStartAt() {
		return startAt;
	}

	public void setStartAt(LocalDateTime startAt) {
		this.startAt = startAt;
	}

	public LocalDateTime getEndAt() {
		return endAt;
	}

	public void setEndAt(LocalDateTime endAt) {
		this.endAt = endAt;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
