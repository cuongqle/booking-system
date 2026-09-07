package com.bookingsystem.infrastructure.booking;

import com.bookingsystem.domain.booking.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

	public Booking toDomain(BookingEntity entity) {
		return new Booking(
				entity.getId(),
				entity.getUserId(),
				entity.getResourceId(),
				entity.getStartDate(),
				entity.getEndDate(),
				entity.getStatus(),
				entity.getTotalAmount(),
				entity.getCurrency(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	public BookingEntity toEntity(Booking booking) {
		BookingEntity entity = new BookingEntity();
		entity.setId(booking.getId());
		entity.setUserId(booking.getUserId());
		entity.setResourceId(booking.getResourceId());
		entity.setStartDate(booking.getStartDate());
		entity.setEndDate(booking.getEndDate());
		entity.setStatus(booking.getStatus());
		entity.setTotalAmount(booking.getTotalAmount());
		entity.setCurrency(booking.getCurrency());
		entity.setCreatedAt(booking.getCreatedAt());
		entity.setUpdatedAt(booking.getUpdatedAt());
		return entity;
	}
}
