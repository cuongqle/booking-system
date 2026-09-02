package com.bookingsystem.application.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookingsystem.domain.booking.Booking;
import com.bookingsystem.domain.booking.BookingStatus;
import com.bookingsystem.infrastructure.booking.BookingEntity;
import com.bookingsystem.infrastructure.booking.BookingMapper;
import com.bookingsystem.infrastructure.booking.BookingRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

	private static final Collection<BookingStatus> BLOCKING = EnumSet.of(
			BookingStatus.PENDING,
			BookingStatus.CONFIRMED);

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private BookingMapper bookingMapper;

	@InjectMocks
	private BookingService bookingService;

	@Test
	void createBooking_rejectsInvalidDateRange() {
		CreateBookingCommand command = new CreateBookingCommand(
				"A101",
				LocalDateTime.of(2026, 9, 12, 14, 0),
				LocalDateTime.of(2026, 9, 10, 10, 0));

		assertThatThrownBy(() -> bookingService.createBooking(1L, command))
				.isInstanceOf(InvalidBookingDatesException.class);
	}

	@Test
	void createBooking_rejectsUnknownRoom() {
		CreateBookingCommand command = new CreateBookingCommand(
				"Z999",
				LocalDateTime.of(2026, 9, 10, 10, 0),
				LocalDateTime.of(2026, 9, 10, 12, 0));

		assertThatThrownBy(() -> bookingService.createBooking(1L, command))
				.isInstanceOf(InvalidRoomException.class)
				.hasMessageContaining("Z999");
	}

	@Test
	void createBooking_rejectsOverlappingRoom() {
		CreateBookingCommand command = new CreateBookingCommand(
				"A101",
				LocalDateTime.of(2026, 9, 10, 10, 0),
				LocalDateTime.of(2026, 9, 10, 12, 0));

		when(bookingRepository.existsOverlapping(
						eq("A101"),
						eq(command.startDate()),
						eq(command.endDate()),
						isNull(),
						ArgumentMatchers.eq(BLOCKING)))
				.thenReturn(true);

		assertThatThrownBy(() -> bookingService.createBooking(1L, command))
				.isInstanceOf(BookingConflictException.class)
				.hasMessageContaining("A101");

		verify(bookingRepository, never()).save(any());
	}

	@Test
	void getBooking_throwsWhenMissing() {
		when(bookingRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookingService.getBooking(99L, 1L))
				.isInstanceOf(BookingNotFoundException.class)
				.hasMessage("Booking 99 not found");
	}

	@Test
	void getBookings_mapsEntitiesToDomain() {
		BookingEntity entity = org.mockito.Mockito.mock(BookingEntity.class);
		Booking booking = new Booking(
				1L,
				7L,
				"A101",
				LocalDateTime.of(2026, 9, 10, 10, 0),
				LocalDateTime.of(2026, 9, 10, 12, 0),
				BookingStatus.PENDING,
				Instant.now(),
				Instant.now());

		when(bookingRepository.findByUserId(7L)).thenReturn(List.of(entity));
		when(bookingMapper.toDomain(entity)).thenReturn(booking);

		List<Booking> result = bookingService.getBookings(7L);

		assertThat(result).containsExactly(booking);
		verify(bookingRepository).findByUserId(7L);
	}

	@Test
	void createBooking_persistsPendingBooking() {
		CreateBookingCommand command = new CreateBookingCommand(
				"A101",
				LocalDateTime.of(2026, 9, 10, 10, 0),
				LocalDateTime.of(2026, 9, 10, 12, 0));

		BookingEntity entity = org.mockito.Mockito.mock(BookingEntity.class);
		Booking saved = new Booking(
				10L,
				7L,
				"A101",
				command.startDate(),
				command.endDate(),
				BookingStatus.PENDING,
				Instant.now(),
				Instant.now());

		when(bookingRepository.existsOverlapping(
						eq("A101"),
						eq(command.startDate()),
						eq(command.endDate()),
						isNull(),
						ArgumentMatchers.eq(BLOCKING)))
				.thenReturn(false);
		when(bookingMapper.toEntity(any(Booking.class))).thenReturn(entity);
		when(bookingRepository.save(entity)).thenReturn(entity);
		when(bookingMapper.toDomain(entity)).thenReturn(saved);

		Booking result = bookingService.createBooking(7L, command);

		assertThat(result.getId()).isEqualTo(10L);
		assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);
		verify(bookingRepository).save(entity);
	}

	@Test
	void updateBooking_updatesFieldsIncludingStatus() {
		UpdateBookingCommand command = new UpdateBookingCommand(
				"B202",
				LocalDateTime.of(2026, 10, 1, 9, 0),
				LocalDateTime.of(2026, 10, 1, 11, 0),
				BookingStatus.CONFIRMED);

		BookingEntity existing = org.mockito.Mockito.mock(BookingEntity.class);
		Booking updated = new Booking(
				5L,
				7L,
				"B202",
				command.startDate(),
				command.endDate(),
				BookingStatus.CONFIRMED,
				Instant.parse("2026-01-01T00:00:00Z"),
				Instant.now());

		when(bookingRepository.findByIdAndUserId(5L, 7L)).thenReturn(Optional.of(existing));
		when(bookingRepository.existsOverlapping(
						eq("B202"),
						eq(command.startDate()),
						eq(command.endDate()),
						eq(5L),
						ArgumentMatchers.eq(BLOCKING)))
				.thenReturn(false);
		when(bookingRepository.save(existing)).thenReturn(existing);
		when(bookingMapper.toDomain(existing)).thenReturn(updated);

		Booking result = bookingService.updateBooking(5L, 7L, command);

		assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
		verify(existing).setRoomId("B202");
		verify(existing).setStartDate(command.startDate());
		verify(existing).setEndDate(command.endDate());
		verify(existing).setStatus(BookingStatus.CONFIRMED);
		verify(bookingRepository).save(existing);
	}

	@Test
	void updateBooking_rejectsOverlappingRoom() {
		UpdateBookingCommand command = new UpdateBookingCommand(
				"A101",
				LocalDateTime.of(2026, 9, 11, 10, 0),
				LocalDateTime.of(2026, 9, 11, 12, 0),
				BookingStatus.PENDING);

		BookingEntity existing = org.mockito.Mockito.mock(BookingEntity.class);

		when(bookingRepository.findByIdAndUserId(5L, 7L)).thenReturn(Optional.of(existing));
		when(bookingRepository.existsOverlapping(
						eq("A101"),
						eq(command.startDate()),
						eq(command.endDate()),
						eq(5L),
						ArgumentMatchers.eq(BLOCKING)))
				.thenReturn(true);

		assertThatThrownBy(() -> bookingService.updateBooking(5L, 7L, command))
				.isInstanceOf(BookingConflictException.class);

		verify(bookingRepository, never()).save(any());
	}
}
