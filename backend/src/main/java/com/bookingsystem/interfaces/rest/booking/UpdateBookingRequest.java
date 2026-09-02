package com.bookingsystem.interfaces.rest.booking;

import com.bookingsystem.application.booking.UpdateBookingCommand;
import com.bookingsystem.domain.booking.BookingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UpdateBookingRequest(
		@NotBlank String roomId,
		@NotNull LocalDateTime startDate,
		@NotNull LocalDateTime endDate,
		@NotNull BookingStatus status) {

	public UpdateBookingCommand toCommand() {
		return new UpdateBookingCommand(roomId, startDate, endDate, status);
	}
}
