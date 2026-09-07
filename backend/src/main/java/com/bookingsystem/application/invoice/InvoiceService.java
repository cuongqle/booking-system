package com.bookingsystem.application.invoice;

import com.bookingsystem.application.booking.BookingNotFoundException;
import com.bookingsystem.application.notification.NotificationService;
import com.bookingsystem.domain.booking.Booking;
import com.bookingsystem.domain.booking.BookingStatus;
import com.bookingsystem.domain.invoice.Invoice;
import com.bookingsystem.domain.invoice.InvoiceStatus;
import com.bookingsystem.infrastructure.booking.BookingEntity;
import com.bookingsystem.infrastructure.booking.BookingMapper;
import com.bookingsystem.infrastructure.booking.BookingRepository;
import com.bookingsystem.infrastructure.invoice.InvoiceEntity;
import com.bookingsystem.infrastructure.invoice.InvoiceMapper;
import com.bookingsystem.infrastructure.invoice.InvoiceRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {

	private static final String STUB_METHOD = "STUB";

	private final InvoiceRepository invoiceRepository;
	private final InvoiceMapper invoiceMapper;
	private final BookingRepository bookingRepository;
	private final BookingMapper bookingMapper;
	private final NotificationService notificationService;

	public InvoiceService(
			InvoiceRepository invoiceRepository,
			InvoiceMapper invoiceMapper,
			BookingRepository bookingRepository,
			BookingMapper bookingMapper,
			NotificationService notificationService) {
		this.invoiceRepository = invoiceRepository;
		this.invoiceMapper = invoiceMapper;
		this.bookingRepository = bookingRepository;
		this.bookingMapper = bookingMapper;
		this.notificationService = notificationService;
	}

	public List<Invoice> listForUser(Long userId, InvoiceStatus status) {
		return invoiceRepository.findForUser(userId, status).stream()
				.map(invoiceMapper::toDomain)
				.toList();
	}

	public List<Invoice> listAll(InvoiceStatus status, Long userId, Long bookingId) {
		return invoiceRepository.search(status, userId, bookingId).stream()
				.map(invoiceMapper::toDomain)
				.toList();
	}

	public Invoice getInvoiceForBooking(Long bookingId, Long userId) {
		return invoiceRepository.findByBookingIdAndUserId(bookingId, userId)
				.map(invoiceMapper::toDomain)
				.orElseThrow(() -> new InvoiceNotFoundException(bookingId));
	}

	public Invoice getInvoiceForBooking(Long bookingId) {
		return invoiceRepository.findByBookingId(bookingId)
				.map(invoiceMapper::toDomain)
				.orElseThrow(() -> new InvoiceNotFoundException(bookingId));
	}

	@Transactional
	public Invoice createForBooking(Booking booking) {
		Instant now = Instant.now();
		Invoice invoice = new Invoice(
				null,
				booking.getId(),
				booking.getUserId(),
				booking.getTotalAmount(),
				booking.getCurrency(),
				InvoiceStatus.UNPAID,
				STUB_METHOD,
				null,
				now,
				now);
		return invoiceMapper.toDomain(invoiceRepository.save(invoiceMapper.toEntity(invoice)));
	}

	@Transactional
	public Invoice payStub(Long bookingId, Long userId) {
		BookingEntity booking = bookingRepository.findByIdAndUserId(bookingId, userId)
				.orElseThrow(() -> new BookingNotFoundException(bookingId));
		InvoiceEntity invoice = invoiceRepository.findByBookingIdAndUserId(bookingId, userId)
				.orElseThrow(() -> new InvoiceNotFoundException(bookingId));

		if (invoice.getStatus() == InvoiceStatus.PAID) {
			return invoiceMapper.toDomain(invoice);
		}
		if (invoice.getStatus() != InvoiceStatus.UNPAID) {
			throw new InvalidInvoiceStateException(
					"Invoice cannot be paid when status is " + invoice.getStatus());
		}
		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new InvalidInvoiceStateException("Cannot pay a cancelled booking");
		}

		Instant now = Instant.now();
		invoice.setStatus(InvoiceStatus.PAID);
		invoice.setMethod(STUB_METHOD);
		invoice.setPaidAt(now);
		invoice.setUpdatedAt(now);
		invoiceRepository.save(invoice);

		booking.setStatus(BookingStatus.CONFIRMED);
		booking.setUpdatedAt(now);
		Booking confirmed = bookingMapper.toDomain(bookingRepository.save(booking));

		notificationService.notifyPaymentReceived(confirmed, invoiceMapper.toDomain(invoice));
		return invoiceMapper.toDomain(invoice);
	}

	@Transactional
	public void syncOnBookingUpdate(Booking booking, BookingStatus previousStatus) {
		InvoiceEntity invoice = invoiceRepository.findByBookingId(booking.getId())
				.orElse(null);
		if (invoice == null) {
			return;
		}

		Instant now = Instant.now();
		if (booking.getStatus() == BookingStatus.CANCELLED
				&& previousStatus != BookingStatus.CANCELLED) {
			if (invoice.getStatus() == InvoiceStatus.UNPAID) {
				invoice.setStatus(InvoiceStatus.CANCELLED);
			} else if (invoice.getStatus() == InvoiceStatus.PAID) {
				invoice.setStatus(InvoiceStatus.REFUNDED);
			}
			invoice.setUpdatedAt(now);
			invoiceRepository.save(invoice);
			return;
		}

		if (invoice.getStatus() == InvoiceStatus.UNPAID) {
			invoice.setAmount(booking.getTotalAmount());
			invoice.setCurrency(booking.getCurrency());
			invoice.setUpdatedAt(now);
			invoiceRepository.save(invoice);
		}
	}

	public void requirePaidForConfirm(Long bookingId, BookingStatus requestedStatus) {
		if (requestedStatus != BookingStatus.CONFIRMED) {
			return;
		}
		InvoiceEntity invoice = invoiceRepository.findByBookingId(bookingId).orElse(null);
		if (invoice == null || invoice.getStatus() != InvoiceStatus.PAID) {
			throw new InvalidInvoiceStateException(
					"Confirm booking by completing payment first");
		}
	}

	public boolean isPaid(Long bookingId) {
		return invoiceRepository.findByBookingId(bookingId)
				.map(invoice -> invoice.getStatus() == InvoiceStatus.PAID)
				.orElse(false);
	}
}
