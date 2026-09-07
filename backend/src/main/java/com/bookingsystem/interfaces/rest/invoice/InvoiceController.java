package com.bookingsystem.interfaces.rest.invoice;

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
@RequestMapping("/invoices")
public class InvoiceController {

	private final InvoiceService invoiceService;

	public InvoiceController(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	@GetMapping
	public List<Invoice> listInvoices(
			@AuthenticationPrincipal AuthenticatedUser currentUser,
			@RequestParam(required = false) InvoiceStatus status) {
		return invoiceService.listForUser(currentUser.getId(), status);
	}
}
