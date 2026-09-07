package com.bookingsystem.application.booking;

import com.bookingsystem.application.invoice.InvoiceService;
import com.bookingsystem.application.notification.NotificationService;
import com.bookingsystem.application.resource.ResourceService;
import com.bookingsystem.domain.booking.Booking;
import com.bookingsystem.domain.booking.BookingStatus;
import com.bookingsystem.domain.resource.OperatingHours;
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
	private final InvoiceService invoiceService;

	public BookingService(
			BookingRepository bookingRepository,
			BookingMapper bookingMapper,
			ResourceService resourceService,
			NotificationService notificationService,
			InvoiceService invoiceService) {
		this.bookingRepository = bookingRepository;
		this.bookingMapper = bookingMapper;
		this.resourceService = resourceService;
		this.notificationService = notificationService;
		this.invoiceService = invoiceService;
	}

	public List<Booking> getBookings(Long userId) {
		return getBookings(userId, null, null);
	}

	public List<Booking> getBookings(Long userId, BookingStatus status, String resourceId) {
		String resourceFilter = resourceId == null || resourceId.isBlank() ? null : resourceId.trim();
		return bookingRepository.findForUser(userId, status, resourceFilter).stream()
				.map(bookingMapper::toDomain)
				.toList();
	}

	public List<Booking> listAll(Long organizationId, BookingStatus status, String resourceId, Long userId) {
		String resourceFilter = resourceId == null || resourceId.isBlank() ? null : resourceId.trim();
		return bookingRepository.search(organizationId, status, resourceFilter, userId).stream()
				.map(bookingMapper::toDomain)
				.toList();
	}

	public Booking getBooking(Long id, Long userId) {
		return bookingRepository.findByIdAndUserId(id, userId)
				.map(bookingMapper::toDomain)
				.orElseThrow(() -> new BookingNotFoundException(id));
	}

	public Booking getBooking(Long id, Long userId, Long organizationId, boolean admin) {
		if (admin) {
			return bookingRepository.findByIdAndOrganizationId(id, organizationId)
					.map(bookingMapper::toDomain)
					.orElseThrow(() -> new BookingNotFoundException(id));
		}
		return getBooking(id, userId);
	}

	@Transactional
	public Booking createBooking(Long userId, Long organizationId, CreateBookingCommand command) {
		Resource resource = requireActiveResource(command.resourceId(), organizationId);
		validateDateRange(command.startDate(), command.endDate());
		validateStayRules(resource, command.startDate(), command.endDate());
		validateSchedule(resource, command.startDate(), command.endDate());
		ensureResourceAvailable(resource, command.startDate(), command.endDate(), null);
		ensureNoBlackout(resource, command.startDate(), command.endDate());

		BigDecimal totalAmount = Pricing.totalAmount(
				command.startDate(),
				command.endDate(),
				resource.getPricePerHour());
		Instant now = Instant.now();
		Booking booking = new Booking(
				null,
				organizationId,
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
		invoiceService.createForBooking(saved);
		notificationService.notifyBookingCreated(saved);
		return saved;
	}

	@Transactional
	public Booking updateBooking(Long id, Long userId, Long organizationId, UpdateBookingCommand command) {
		Resource resource = requireActiveResource(command.resourceId(), organizationId);
		validateDateRange(command.startDate(), command.endDate());
		validateStayRules(resource, command.startDate(), command.endDate());
		validateSchedule(resource, command.startDate(), command.endDate());

		BookingEntity existing = bookingRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new BookingNotFoundException(id));

		BookingStatus previousStatus = existing.getStatus();
		invoiceService.requirePaidForConfirm(id, command.status());

		ensureResourceAvailable(resource, command.startDate(), command.endDate(), id);
		ensureNoBlackout(resource, command.startDate(), command.endDate());

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
		invoiceService.syncOnBookingUpdate(saved, previousStatus);
		notificationService.notifyBookingUpdated(saved);
		return saved;
	}

	private Resource requireActiveResource(String resourceId, Long organizationId) {
		if (!resourceService.isActiveResource(resourceId, organizationId)) {
			throw new InvalidResourceException(resourceId);
		}
		return resourceService.getResource(resourceId, organizationId);
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

	private void validateSchedule(Resource resource, LocalDateTime startDate, LocalDateTime endDate) {
		try {
			OperatingHours.validateBooking(
					startDate,
					endDate,
					resource.getOpenTime(),
					resource.getCloseTime());
		} catch (IllegalArgumentException ex) {
			throw new ScheduleViolationException(ex.getMessage());
		}
	}

	private void ensureNoBlackout(Resource resource, LocalDateTime startDate, LocalDateTime endDate) {
		if (resourceService.hasBlackoutOverlap(resource.getId(), startDate, endDate)) {
			throw new BlackoutConflictException(resource.getId());
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
