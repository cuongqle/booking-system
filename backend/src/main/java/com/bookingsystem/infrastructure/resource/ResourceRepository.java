package com.bookingsystem.infrastructure.resource;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<ResourceEntity, String> {

	List<ResourceEntity> findByActiveTrueOrderByIdAsc();

	List<ResourceEntity> findAllByOrderByIdAsc();

	boolean existsByIdAndActiveTrue(String id);
}
