package com.bookingsystem.interfaces.rest.booking;

import com.bookingsystem.application.booking.CreateBookingCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateBookingRequest(
		@NotBlank String roomId,
		@NotNull LocalDateTime startDate,
		@NotNull LocalDateTime endDate) {

	public CreateBookingCommand toCommand() {
		return new CreateBookingCommand(roomId, startDate, endDate);
	}
}
