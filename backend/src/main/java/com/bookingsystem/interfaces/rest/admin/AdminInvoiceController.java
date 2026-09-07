package com.bookingsystem.interfaces.rest.admin;

import com.bookingsystem.application.invoice.InvoiceService;
import com.bookingsystem.domain.invoice.Invoice;
import com.bookingsystem.domain.invoice.InvoiceStatus;
import com.bookingsystem.infrastructure.security.AuthenticatedUser;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/invoices")
public class AdminInvoiceController {

	private final InvoiceService invoiceService;

	public AdminInvoiceController(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	@GetMapping
	public List<Invoice> listInvoices(
			@AuthenticationPrincipal AuthenticatedUser currentUser,
			@RequestParam(required = false) InvoiceStatus status,
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) Long bookingId) {
		return invoiceService.listAll(currentUser.getOrganizationId(), status, userId, bookingId);
	}
}
