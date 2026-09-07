package com.bookingsystem.interfaces.rest.admin;

import com.bookingsystem.application.booking.BookingService;
import com.bookingsystem.domain.booking.Booking;
import com.bookingsystem.domain.booking.BookingStatus;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/bookings")
public class AdminBookingController {

	private final BookingService bookingService;

	public AdminBookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@GetMapping
	public List<Booking> listBookings(
			@RequestParam(required = false) BookingStatus status,
			@RequestParam(required = false) String resourceId,
			@RequestParam(required = false) Long userId) {
		return bookingService.listAll(status, resourceId, userId);
	}
}
