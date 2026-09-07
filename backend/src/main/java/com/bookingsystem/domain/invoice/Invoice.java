package com.bookingsystem.domain.invoice;

import java.math.BigDecimal;
import java.time.Instant;

public class Invoice {

	private Long id;
	private Long organizationId;
	private Long bookingId;
	private Long userId;
	private BigDecimal amount;
	private String currency;
	private InvoiceStatus status;
	private String method;
	private Instant paidAt;
	private Instant createdAt;
	private Instant updatedAt;

	public Invoice() {
	}

	public Invoice(
			Long id,
			Long organizationId,
			Long bookingId,
			Long userId,
			BigDecimal amount,
			String currency,
			InvoiceStatus status,
			String method,
			Instant paidAt,
			Instant createdAt,
			Instant updatedAt) {
		this.id = id;
		this.organizationId = organizationId;
		this.bookingId = bookingId;
		this.userId = userId;
		this.amount = amount;
		this.currency = currency;
		this.status = status;
		this.method = method;
		this.paidAt = paidAt;
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

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public InvoiceStatus getStatus() {
		return status;
	}

	public void setStatus(InvoiceStatus status) {
		this.status = status;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Instant getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(Instant paidAt) {
		this.paidAt = paidAt;
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
