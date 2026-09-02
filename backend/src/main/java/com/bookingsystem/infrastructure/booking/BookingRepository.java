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
			select case when count(b) > 0 then true else false end
			from BookingEntity b
			where b.roomId = :roomId
			  and b.status in :blockingStatuses
			  and b.startDate < :endDate
			  and b.endDate > :startDate
			  and (:excludeId is null or b.id <> :excludeId)
			""")
	boolean existsOverlapping(
			@Param("roomId") String roomId,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			@Param("excludeId") Long excludeId,
			@Param("blockingStatuses") Collection<BookingStatus> blockingStatuses);
}
