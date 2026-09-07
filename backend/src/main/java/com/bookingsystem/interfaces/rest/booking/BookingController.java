package com.bookingsystem.interfaces.rest.booking;

import com.bookingsystem.application.booking.BookingService;
import com.bookingsystem.application.invoice.InvoiceService;
import com.bookingsystem.domain.booking.Booking;
import com.bookingsystem.domain.booking.BookingStatus;
import com.bookingsystem.domain.invoice.Invoice;
import com.bookingsystem.domain.user.UserRole;
import com.bookingsystem.infrastructure.security.AuthenticatedUser;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final BookingService bookingService;
	private final InvoiceService invoiceService;

	public BookingController(BookingService bookingService, InvoiceService invoiceService) {
		this.bookingService = bookingService;
		this.invoiceService = invoiceService;
	}

	@GetMapping
	public List<Booking> getBookings(
			@AuthenticationPrincipal AuthenticatedUser currentUser,
			@RequestParam(required = false) BookingStatus status,
			@RequestParam(required = false) String resourceId) {
		return bookingService.getBookings(currentUser.getId(), status, resourceId);
	}

	@GetMapping("/{id}")
	public Booking getBooking(
			@PathVariable Long id,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return bookingService.getBooking(id, currentUser.getId(), isAdmin(currentUser));
	}

	@GetMapping("/{id}/invoice")
	public Invoice getInvoice(
			@PathVariable Long id,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		if (isAdmin(currentUser)) {
			bookingService.getBooking(id, currentUser.getId(), true);
			return invoiceService.getInvoiceForBooking(id);
		}
		bookingService.getBooking(id, currentUser.getId());
		return invoiceService.getInvoiceForBooking(id, currentUser.getId());
	}

	@PostMapping("/{id}/pay")
	public Invoice payBooking(
			@PathVariable Long id,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return invoiceService.payStub(id, currentUser.getId());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Booking createBooking(
			@Valid @RequestBody CreateBookingRequest request,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return bookingService.createBooking(currentUser.getId(), request.toCommand());
	}

	@PutMapping("/{id}")
	public Booking updateBooking(
			@PathVariable Long id,
			@Valid @RequestBody UpdateBookingRequest request,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return bookingService.updateBooking(id, currentUser.getId(), request.toCommand());
	}

	private static boolean isAdmin(AuthenticatedUser user) {
		return user.getRole() == UserRole.ADMIN;
	}
}
