package com.bookingsystem.domain.resource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public final class Pricing {

	private Pricing() {
	}

	public static BigDecimal totalAmount(
			LocalDateTime start,
			LocalDateTime end,
			BigDecimal pricePerHour) {
		long minutes = StayRules.durationMinutes(start, end);
		return pricePerHour
				.multiply(BigDecimal.valueOf(minutes))
				.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
	}
}
