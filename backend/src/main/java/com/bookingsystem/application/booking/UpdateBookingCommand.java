package com.bookingsystem.application.booking;

import com.bookingsystem.domain.booking.BookingStatus;
import java.time.LocalDateTime;

public record UpdateBookingCommand(
		String roomId,
		LocalDateTime startDate,
		LocalDateTime endDate,
		BookingStatus status) {
}
