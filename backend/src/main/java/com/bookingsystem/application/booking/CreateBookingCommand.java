package com.bookingsystem.application.booking;

import java.time.LocalDateTime;

public record CreateBookingCommand(
		String resourceId,
		LocalDateTime startDate,
		LocalDateTime endDate) {
}
