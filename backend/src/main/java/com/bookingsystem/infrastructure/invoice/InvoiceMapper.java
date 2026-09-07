package com.bookingsystem.infrastructure.invoice;

import com.bookingsystem.domain.invoice.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

	public Invoice toDomain(InvoiceEntity entity) {
		return new Invoice(
				entity.getId(),
				entity.getBookingId(),
				entity.getUserId(),
				entity.getAmount(),
				entity.getCurrency(),
				entity.getStatus(),
				entity.getMethod(),
				entity.getPaidAt(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	public InvoiceEntity toEntity(Invoice invoice) {
		InvoiceEntity entity = new InvoiceEntity();
		entity.setId(invoice.getId());
		entity.setBookingId(invoice.getBookingId());
		entity.setUserId(invoice.getUserId());
		entity.setAmount(invoice.getAmount());
		entity.setCurrency(invoice.getCurrency());
		entity.setStatus(invoice.getStatus());
		entity.setMethod(invoice.getMethod());
		entity.setPaidAt(invoice.getPaidAt());
		entity.setCreatedAt(invoice.getCreatedAt());
		entity.setUpdatedAt(invoice.getUpdatedAt());
		return entity;
	}
}
