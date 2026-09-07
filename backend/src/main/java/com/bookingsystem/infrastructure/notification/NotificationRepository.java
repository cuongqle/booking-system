package com.bookingsystem.infrastructure.notification;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

	List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

	Optional<NotificationEntity> findByIdAndUserId(Long id, Long userId);

	long countByUserIdAndReadAtIsNull(Long userId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			update NotificationEntity n
			set n.readAt = :readAt
			where n.userId = :userId
			  and n.readAt is null
			""")
	int markAllReadForUser(@Param("userId") Long userId, @Param("readAt") java.time.Instant readAt);
}
