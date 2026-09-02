package com.bookingsystem.domain.booking;

import java.time.Instant;
import java.time.LocalDateTime;

public class Booking {

	private Long id;
	private Long userId;
	private String roomId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private BookingStatus status;
	private Instant createdAt;
	private Instant updatedAt;

	public Booking() {
	}

	public Booking(
			Long id,
			Long userId,
			String roomId,
			LocalDateTime startDate,
			LocalDateTime endDate,
			BookingStatus status,
			Instant createdAt,
			Instant updatedAt) {
		this.id = id;
		this.userId = userId;
		this.roomId = roomId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
