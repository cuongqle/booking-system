package com.bookingsystem.domain.resource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class StayRules {

	private StayRules() {
	}

	public static long durationMinutes(LocalDateTime start, LocalDateTime end) {
		return ChronoUnit.MINUTES.between(start, end);
	}

	public static void validate(
			LocalDateTime start,
			LocalDateTime end,
			int minDurationMinutes,
			Integer maxDurationMinutes) {
		long minutes = durationMinutes(start, end);
		if (minutes < minDurationMinutes) {
			throw new IllegalArgumentException(
					"Booking must be at least %d minutes".formatted(minDurationMinutes));
		}
		if (maxDurationMinutes != null && minutes > maxDurationMinutes) {
			throw new IllegalArgumentException(
					"Booking must be at most %d minutes".formatted(maxDurationMinutes));
		}
	}
}
