package com.bookingsystem.infrastructure.organization;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {

	Optional<OrganizationEntity> findBySlug(String slug);

	boolean existsBySlug(String slug);
}
