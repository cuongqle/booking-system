package com.bookingsystem.interfaces.rest.resource;

import com.bookingsystem.application.resource.UpdateResourceCommand;
import com.bookingsystem.domain.resource.ResourceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateResourceRequest(
		@NotBlank @Size(max = 255) String name,
		@Size(max = 1000) String description,
		@NotNull ResourceType type,
		@NotNull Boolean active,
		@NotNull @DecimalMin("0.00") BigDecimal pricePerHour,
		@NotBlank @Size(min = 3, max = 3) @Pattern(regexp = "[A-Z]{3}") String currency,
		@NotNull @Min(1) Integer minDurationMinutes,
		@Min(1) Integer maxDurationMinutes,
		@NotNull @Min(0) Integer bufferMinutes) {

	public UpdateResourceCommand toCommand() {
		return new UpdateResourceCommand(
				name,
				description,
				type,
				Boolean.TRUE.equals(active),
				pricePerHour,
				currency,
				minDurationMinutes,
				maxDurationMinutes,
				bufferMinutes);
	}
}
