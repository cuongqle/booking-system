package com.bookingsystem.application.invoice;

public class InvalidInvoiceStateException extends RuntimeException {

	public InvalidInvoiceStateException(String message) {
		super(message);
	}
}
