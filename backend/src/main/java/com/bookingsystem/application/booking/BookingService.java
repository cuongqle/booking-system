package com.bookingsystem.application.booking;

import com.bookingsystem.application.notification.NotificationService;
import com.bookingsystem.application.resource.ResourceService;
import com.bookingsystem.domain.booking.Booking;
import com.bookingsystem.domain.booking.BookingStatus;
import com.bookingsystem.domain.resource.Pricing;
import com.bookingsystem.domain.resource.Resource;
import com.bookingsystem.domain.resource.StayRules;
import com.bookingsystem.infrastructure.booking.BookingEntity;
import com.bookingsystem.infrastructure.booking.BookingMapper;
import com.bookingsystem.infrastructure.booking.BookingRepository;
import java.math.BigDecimal;
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
	private final ResourceService resourceService;
	private final NotificationService notificationService;

	public BookingService(
			BookingRepository bookingRepository,
			BookingMapper bookingMapper,
			ResourceService resourceService,
			NotificationService notificationService) {
		this.bookingRepository = bookingRepository;
		this.bookingMapper = bookingMapper;
		this.resourceService = resourceService;
		this.notificationService = notificationService;
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
		Resource resource = requireActiveResource(command.resourceId());
		validateDateRange(command.startDate(), command.endDate());
		validateStayRules(resource, command.startDate(), command.endDate());
		ensureResourceAvailable(resource, command.startDate(), command.endDate(), null);

		BigDecimal totalAmount = Pricing.totalAmount(
				command.startDate(),
				command.endDate(),
				resource.getPricePerHour());
		Instant now = Instant.now();
		Booking booking = new Booking(
				null,
				userId,
				command.resourceId(),
				command.startDate(),
				command.endDate(),
				BookingStatus.PENDING,
				totalAmount,
				resource.getCurrency(),
				now,
				now);

		Booking saved = bookingMapper.toDomain(bookingRepository.save(bookingMapper.toEntity(booking)));
		notificationService.notifyBookingCreated(saved);
		return saved;
	}

	@Transactional
	public Booking updateBooking(Long id, Long userId, UpdateBookingCommand command) {
		Resource resource = requireActiveResource(command.resourceId());
		validateDateRange(command.startDate(), command.endDate());
		validateStayRules(resource, command.startDate(), command.endDate());

		BookingEntity existing = bookingRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new BookingNotFoundException(id));

		ensureResourceAvailable(resource, command.startDate(), command.endDate(), id);

		BigDecimal totalAmount = Pricing.totalAmount(
				command.startDate(),
				command.endDate(),
				resource.getPricePerHour());

		existing.setResourceId(command.resourceId());
		existing.setStartDate(command.startDate());
		existing.setEndDate(command.endDate());
		existing.setStatus(command.status());
		existing.setTotalAmount(totalAmount);
		existing.setCurrency(resource.getCurrency());
		existing.setUpdatedAt(Instant.now());

		Booking saved = bookingMapper.toDomain(bookingRepository.save(existing));
		notificationService.notifyBookingUpdated(saved);
		return saved;
	}

	private Resource requireActiveResource(String resourceId) {
		if (!resourceService.isActiveResource(resourceId)) {
			throw new InvalidResourceException(resourceId);
		}
		return resourceService.getResource(resourceId);
	}

	private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
		if (!startDate.isBefore(endDate)) {
			throw new InvalidBookingDatesException("startDate must be before endDate");
		}
	}

	private void validateStayRules(Resource resource, LocalDateTime startDate, LocalDateTime endDate) {
		try {
			StayRules.validate(
					startDate,
					endDate,
					resource.getMinDurationMinutes(),
					resource.getMaxDurationMinutes());
		} catch (IllegalArgumentException ex) {
			throw new StayRuleViolationException(ex.getMessage());
		}
	}

	private void ensureResourceAvailable(
			Resource resource,
			LocalDateTime startDate,
			LocalDateTime endDate,
			Long excludeId) {
		int buffer = resource.getBufferMinutes();
		LocalDateTime bufferedStart = startDate.minusMinutes(buffer);
		LocalDateTime bufferedEnd = endDate.plusMinutes(buffer);
		boolean conflict = bookingRepository.existsOverlapping(
				resource.getId(),
				bufferedStart,
				bufferedEnd,
				excludeId,
				BLOCKING_STATUSES);
		if (conflict) {
			throw new BookingConflictException(resource.getId());
		}
	}
}
