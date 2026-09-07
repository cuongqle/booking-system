package com.bookingsystem.application.invoice;

public class InvoiceNotFoundException extends RuntimeException {

	public InvoiceNotFoundException(Long bookingId) {
		super("Invoice for booking %d not found".formatted(bookingId));
	}
}
