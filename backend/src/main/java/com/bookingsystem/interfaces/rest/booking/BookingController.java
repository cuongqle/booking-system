package com.bookingsystem.interfaces.rest.booking;

import com.bookingsystem.application.booking.BookingService;
import com.bookingsystem.domain.booking.Booking;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@GetMapping
	public List<Booking> getBookings(@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return bookingService.getBookings(currentUser.getId());
	}

	@GetMapping("/{id}")
	public Booking getBooking(
			@PathVariable Long id,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return bookingService.getBooking(id, currentUser.getId());
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
}
