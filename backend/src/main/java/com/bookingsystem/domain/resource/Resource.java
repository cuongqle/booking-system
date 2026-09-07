package com.bookingsystem.domain.resource;

import java.math.BigDecimal;
import java.time.Instant;

public class Resource {

	private String id;
	private String name;
	private String description;
	private ResourceType type;
	private boolean active;
	private BigDecimal pricePerHour;
	private String currency;
	private int minDurationMinutes;
	private Integer maxDurationMinutes;
	private int bufferMinutes;
	private Instant createdAt;
	private Instant updatedAt;

	public Resource() {
	}

	public Resource(
			String id,
			String name,
			String description,
			ResourceType type,
			boolean active,
			BigDecimal pricePerHour,
			String currency,
			int minDurationMinutes,
			Integer maxDurationMinutes,
			int bufferMinutes,
			Instant createdAt,
			Instant updatedAt) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.active = active;
		this.pricePerHour = pricePerHour;
		this.currency = currency;
		this.minDurationMinutes = minDurationMinutes;
		this.maxDurationMinutes = maxDurationMinutes;
		this.bufferMinutes = bufferMinutes;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
