package com.bookingsystem.application.resource;

import java.time.LocalDateTime;

public record CreateBlackoutCommand(
		LocalDateTime startAt,
		LocalDateTime endAt,
		String reason) {
}
