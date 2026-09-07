package com.bookingsystem.infrastructure.organization;

import com.bookingsystem.domain.organization.Organization;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

	public Organization toDomain(OrganizationEntity entity) {
		return new Organization(
				entity.getId(),
				entity.getName(),
				entity.getSlug(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	public OrganizationEntity toEntity(Organization organization) {
		OrganizationEntity entity = new OrganizationEntity();
		entity.setId(organization.getId());
		entity.setName(organization.getName());
		entity.setSlug(organization.getSlug());
		entity.setCreatedAt(organization.getCreatedAt());
		entity.setUpdatedAt(organization.getUpdatedAt());
		return entity;
	}
}
