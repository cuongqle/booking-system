package com.bookingsystem.domain.booking;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public class Booking {

	private Long id;
	private Long organizationId;
	private Long userId;
	private String resourceId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private BookingStatus status;
	private BigDecimal totalAmount;
	private String currency;
	private Instant createdAt;
	private Instant updatedAt;

	public Booking() {
	}

	public Booking(
			Long id,
			Long organizationId,
			Long userId,
			String resourceId,
			LocalDateTime startDate,
			LocalDateTime endDate,
			BookingStatus status,
			BigDecimal totalAmount,
			String currency,
			Instant createdAt,
			Instant updatedAt) {
		this.id = id;
		this.organizationId = organizationId;
		this.userId = userId;
		this.resourceId = resourceId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.totalAmount = totalAmount;
		this.currency = currency;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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
