package com.bookingsystem.application.resource;

import com.bookingsystem.domain.resource.ResourceType;
import java.math.BigDecimal;

public record CreateResourceCommand(
		String id,
		String name,
		String description,
		ResourceType type,
		boolean active,
		BigDecimal pricePerHour,
		String currency,
		int minDurationMinutes,
		Integer maxDurationMinutes,
		int bufferMinutes) {
}
