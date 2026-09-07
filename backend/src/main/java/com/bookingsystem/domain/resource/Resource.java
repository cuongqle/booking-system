package com.bookingsystem.domain.resource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

public class Resource {

	private String id;
	private Long organizationId;
	private String name;
	private String description;
	private ResourceType type;
	private boolean active;
	private BigDecimal pricePerHour;
	private String currency;
	private int minDurationMinutes;
	private Integer maxDurationMinutes;
	private int bufferMinutes;
	private LocalTime openTime;
	private LocalTime closeTime;
	private Instant createdAt;
	private Instant updatedAt;

	public Resource() {
	}

	public Resource(
			String id,
			Long organizationId,
			String name,
			String description,
			ResourceType type,
			boolean active,
			BigDecimal pricePerHour,
			String currency,
			int minDurationMinutes,
			Integer maxDurationMinutes,
			int bufferMinutes,
			LocalTime openTime,
			LocalTime closeTime,
			Instant createdAt,
			Instant updatedAt) {
		this.id = id;
		this.organizationId = organizationId;
		this.name = name;
		this.description = description;
		this.type = type;
		this.active = active;
		this.pricePerHour = pricePerHour;
		this.currency = currency;
		this.minDurationMinutes = minDurationMinutes;
		this.maxDurationMinutes = maxDurationMinutes;
		this.bufferMinutes = bufferMinutes;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ResourceType getType() {
		return type;
	}

	public void setType(ResourceType type) {
		this.type = type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public BigDecimal getPricePerHour() {
		return pricePerHour;
	}

	public void setPricePerHour(BigDecimal pricePerHour) {
		this.pricePerHour = pricePerHour;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getMinDurationMinutes() {
		return minDurationMinutes;
	}

	public void setMinDurationMinutes(int minDurationMinutes) {
		this.minDurationMinutes = minDurationMinutes;
	}

	public Integer getMaxDurationMinutes() {
		return maxDurationMinutes;
	}

	public void setMaxDurationMinutes(Integer maxDurationMinutes) {
		this.maxDurationMinutes = maxDurationMinutes;
	}

	public int getBufferMinutes() {
		return bufferMinutes;
	}

	public void setBufferMinutes(int bufferMinutes) {
		this.bufferMinutes = bufferMinutes;
	}

	public LocalTime getOpenTime() {
		return openTime;
	}

	public void setOpenTime(LocalTime openTime) {
		this.openTime = openTime;
	}

	public LocalTime getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(LocalTime closeTime) {
		this.closeTime = closeTime;
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
