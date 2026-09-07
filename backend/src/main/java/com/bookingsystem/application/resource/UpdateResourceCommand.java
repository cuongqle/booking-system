package com.bookingsystem.application.resource;

import com.bookingsystem.domain.resource.ResourceType;
import java.math.BigDecimal;
import java.time.LocalTime;

public record UpdateResourceCommand(
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
		LocalTime closeTime) {
}
