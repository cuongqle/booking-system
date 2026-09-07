package com.bookingsystem.interfaces.rest.resource;

import com.bookingsystem.application.resource.CreateBlackoutCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateBlackoutRequest(
		@NotNull LocalDateTime startAt,
		@NotNull LocalDateTime endAt,
		@Size(max = 255) String reason) {

	public CreateBlackoutCommand toCommand() {
		return new CreateBlackoutCommand(startAt, endAt, reason);
	}
}
