package com.bookingsystem.infrastructure.resource;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<ResourceEntity, String> {

	List<ResourceEntity> findByOrganizationIdAndActiveTrueOrderByIdAsc(Long organizationId);

	List<ResourceEntity> findByOrganizationIdOrderByIdAsc(Long organizationId);

	Optional<ResourceEntity> findByIdAndOrganizationId(String id, Long organizationId);

	boolean existsByIdAndOrganizationIdAndActiveTrue(String id, Long organizationId);

	boolean existsByIdAndOrganizationId(String id, Long organizationId);
}
