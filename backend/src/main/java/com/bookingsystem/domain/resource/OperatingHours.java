package com.bookingsystem.domain.resource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class OperatingHours {

	private OperatingHours() {
	}

	public static void validateConfig(LocalTime openTime, LocalTime closeTime) {
		if (openTime == null && closeTime == null) {
			return;
		}
		if (openTime == null || closeTime == null) {
			throw new IllegalArgumentException("openTime and closeTime must both be set or both empty");
		}
		if (!openTime.isBefore(closeTime)) {
			throw new IllegalArgumentException("openTime must be before closeTime (overnight hours are not supported)");
		}
	}

	public static void validateBooking(
			LocalDateTime start,
			LocalDateTime end,
			LocalTime openTime,
			LocalTime closeTime) {
		if (openTime == null && closeTime == null) {
			return;
		}
		validateConfig(openTime, closeTime);

		LocalDateTime cursor = start;
		while (cursor.isBefore(end)) {
			LocalDate day = cursor.toLocalDate();
			LocalDateTime dayOpen = day.atTime(openTime);
			LocalDateTime dayClose = day.atTime(closeTime);
			LocalDateTime dayEnd = day.plusDays(1).atStartOfDay();
			LocalDateTime segmentEnd = end.isBefore(dayEnd) ? end : dayEnd;

			if (cursor.isBefore(dayOpen)) {
				throw new IllegalArgumentException(
						"Booking starts before operating hours (%s–%s)".formatted(openTime, closeTime));
			}
			if (segmentEnd.isAfter(dayClose)) {
				throw new IllegalArgumentException(
						"Booking extends past operating hours (%s–%s)".formatted(openTime, closeTime));
			}
			cursor = segmentEnd;
		}
	}
}
