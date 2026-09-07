package com.bookingsystem.infrastructure.booking;

import com.bookingsystem.domain.booking.BookingStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

	List<BookingEntity> findByUserId(Long userId);

	Optional<BookingEntity> findByIdAndUserId(Long id, Long userId);

	@Query("""
			SELECT b FROM BookingEntity b
			WHERE b.userId = :userId
			  AND (:status IS NULL OR b.status = :status)
			  AND (:resourceId IS NULL OR b.resourceId = :resourceId)
			ORDER BY b.startDate DESC
			""")
	List<BookingEntity> findForUser(
			@Param("userId") Long userId,
			@Param("status") BookingStatus status,
			@Param("resourceId") String resourceId);

	@Query("""
			SELECT b FROM BookingEntity b
			WHERE (:status IS NULL OR b.status = :status)
			  AND (:resourceId IS NULL OR b.resourceId = :resourceId)
			  AND (:userId IS NULL OR b.userId = :userId)
			ORDER BY b.startDate DESC
			""")
	List<BookingEntity> search(
			@Param("status") BookingStatus status,
			@Param("resourceId") String resourceId,
			@Param("userId") Long userId);

	@Query("""
			select case when count(b) > 0 then true else false end
			from BookingEntity b
			where b.resourceId = :resourceId
			  and b.status in :blockingStatuses
			  and b.startDate < :endDate
			  and b.endDate > :startDate
			  and (:excludeId is null or b.id <> :excludeId)
			""")
	boolean existsOverlapping(
			@Param("resourceId") String resourceId,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			@Param("excludeId") Long excludeId,
			@Param("blockingStatuses") Collection<BookingStatus> blockingStatuses);
}
