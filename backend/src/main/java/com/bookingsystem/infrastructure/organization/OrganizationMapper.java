package com.bookingsystem.infrastructure.organization;

import com.bookingsystem.domain.organization.Organization;
import com.bookingsystem.domain.organization.OrganizationStatus;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

	public Organization toDomain(OrganizationEntity entity) {
		return new Organization(
				entity.getId(),
				entity.getName(),
				entity.getSlug(),
				entity.getStatus() == null ? OrganizationStatus.ACTIVE : entity.getStatus(),
				entity.getSuspendedAt(),
				entity.getSuspendedReason(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	public OrganizationEntity toEntity(Organization organization) {
		OrganizationEntity entity = new OrganizationEntity();
		entity.setId(organization.getId());
		entity.setName(organization.getName());
		entity.setSlug(organization.getSlug());
		entity.setStatus(
				organization.getStatus() == null ? OrganizationStatus.ACTIVE : organization.getStatus());
		entity.setSuspendedAt(organization.getSuspendedAt());
		entity.setSuspendedReason(organization.getSuspendedReason());
		entity.setCreatedAt(organization.getCreatedAt());
		entity.setUpdatedAt(organization.getUpdatedAt());
		return entity;
	}
}
