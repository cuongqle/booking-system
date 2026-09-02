package com.bookingsystem.application.booking;

import java.time.LocalDateTime;

public record CreateBookingCommand(
		String roomId,
		LocalDateTime startDate,
		LocalDateTime endDate) {
}
