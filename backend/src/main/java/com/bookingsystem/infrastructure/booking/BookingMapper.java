package com.bookingsystem.infrastructure.booking;

import com.bookingsystem.domain.booking.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

	public Booking toDomain(BookingEntity entity) {
		return new Booking(
				entity.getId(),
				entity.getUserId(),
				entity.getRoomId(),
				entity.getStartDate(),
				entity.getEndDate(),
				entity.getStatus(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	public BookingEntity toEntity(Booking booking) {
		BookingEntity entity = new BookingEntity();
		entity.setId(booking.getId());
		entity.setUserId(booking.getUserId());
		entity.setRoomId(booking.getRoomId());
		entity.setStartDate(booking.getStartDate());
		entity.setEndDate(booking.getEndDate());
		entity.setStatus(booking.getStatus());
		entity.setCreatedAt(booking.getCreatedAt());
		entity.setUpdatedAt(booking.getUpdatedAt());
		return entity;
	}
}
