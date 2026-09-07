package com.bookingsystem.infrastructure.resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourceBlackoutRepository extends JpaRepository<ResourceBlackoutEntity, Long> {

	List<ResourceBlackoutEntity> findByResourceIdOrderByStartAtAsc(String resourceId);

	Optional<ResourceBlackoutEntity> findByIdAndResourceId(Long id, String resourceId);

	@Query("""
			SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END
			FROM ResourceBlackoutEntity b
			WHERE b.resourceId = :resourceId
			  AND b.startAt < :endAt
			  AND b.endAt > :startAt
			""")
	boolean existsOverlapping(
			@Param("resourceId") String resourceId,
			@Param("startAt") LocalDateTime startAt,
			@Param("endAt") LocalDateTime endAt);
}
