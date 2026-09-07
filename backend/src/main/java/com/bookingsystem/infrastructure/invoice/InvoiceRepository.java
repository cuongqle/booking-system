package com.bookingsystem.infrastructure.invoice;

import com.bookingsystem.domain.invoice.InvoiceStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

	Optional<InvoiceEntity> findByBookingIdAndUserId(Long bookingId, Long userId);

	Optional<InvoiceEntity> findByBookingId(Long bookingId);

	@Query("""
			SELECT i FROM InvoiceEntity i
			WHERE i.userId = :userId
			  AND (:status IS NULL OR i.status = :status)
			ORDER BY i.createdAt DESC
			""")
	List<InvoiceEntity> findForUser(
			@Param("userId") Long userId,
			@Param("status") InvoiceStatus status);

	@Query("""
			SELECT i FROM InvoiceEntity i
			WHERE (:status IS NULL OR i.status = :status)
			  AND (:userId IS NULL OR i.userId = :userId)
			  AND (:bookingId IS NULL OR i.bookingId = :bookingId)
			ORDER BY i.createdAt DESC
			""")
	List<InvoiceEntity> search(
			@Param("status") InvoiceStatus status,
			@Param("userId") Long userId,
			@Param("bookingId") Long bookingId);
}
