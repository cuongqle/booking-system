package com.bookingsystem.application.booking;

import com.bookingsystem.domain.booking.Booking;
import com.bookingsystem.domain.booking.BookingStatus;
import com.bookingsystem.domain.room.RoomCatalog;
import com.bookingsystem.infrastructure.booking.BookingEntity;
import com.bookingsystem.infrastructure.booking.BookingMapper;
import com.bookingsystem.infrastructure.booking.BookingRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

	private static final Set<BookingStatus> BLOCKING_STATUSES = EnumSet.of(
			BookingStatus.PENDING,
			BookingStatus.CONFIRMED);

	private final BookingRepository bookingRepository;
	private final BookingMapper bookingMapper;

	public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper) {
		this.bookingRepository = bookingRepository;
		this.bookingMapper = bookingMapper;
	}

	public List<Booking> getBookings(Long userId) {
		return bookingRepository.findByUserId(userId).stream()
				.map(bookingMapper::toDomain)
				.toList();
	}

	public Booking getBooking(Long id, Long userId) {
		return bookingRepository.findByIdAndUserId(id, userId)
				.map(bookingMapper::toDomain)
				.orElseThrow(() -> new BookingNotFoundException(id));
	}

	@Transactional
	public Booking createBooking(Long userId, CreateBookingCommand command) {
		validateRoom(command.roomId());
		validateDateRange(command.startDate(), command.endDate());
		ensureRoomAvailable(command.roomId(), command.startDate(), command.endDate(), null);

		Instant now = Instant.now();
		Booking booking = new Booking(
				null,
				userId,
				command.roomId(),
				command.startDate(),
				command.endDate(),
				BookingStatus.PENDING,
				now,
				now);

		return bookingMapper.toDomain(bookingRepository.save(bookingMapper.toEntity(booking)));
	}

	@Transactional
	public Booking updateBooking(Long id, Long userId, UpdateBookingCommand command) {
		validateRoom(command.roomId());
		validateDateRange(command.startDate(), command.endDate());

		BookingEntity existing = bookingRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new BookingNotFoundException(id));

		ensureRoomAvailable(command.roomId(), command.startDate(), command.endDate(), id);

		existing.setRoomId(command.roomId());
		existing.setStartDate(command.startDate());
		existing.setEndDate(command.endDate());
		existing.setStatus(command.status());
		existing.setUpdatedAt(Instant.now());

		return bookingMapper.toDomain(bookingRepository.save(existing));
	}

	private void validateRoom(String roomId) {
		if (!RoomCatalog.exists(roomId)) {
			throw new InvalidRoomException(roomId);
		}
	}

	private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
		if (!startDate.isBefore(endDate)) {
			throw new InvalidBookingDatesException("startDate must be before endDate");
		}
	}

	private void ensureRoomAvailable(
			String roomId,
			LocalDateTime startDate,
			LocalDateTime endDate,
			Long excludeId) {
		boolean conflict = bookingRepository.existsOverlapping(
				roomId,
				startDate,
				endDate,
				excludeId,
				BLOCKING_STATUSES);
		if (conflict) {
			throw new BookingConflictException(roomId);
		}
	}
}
