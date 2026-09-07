package com.bookingsystem.infrastructure.resource;

import com.bookingsystem.domain.resource.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

	public Resource toDomain(ResourceEntity entity) {
		return new Resource(
				entity.getId(),
				entity.getName(),
				entity.getDescription(),
				entity.getType(),
				entity.isActive(),
				entity.getPricePerHour(),
				entity.getCurrency(),
				entity.getMinDurationMinutes(),
				entity.getMaxDurationMinutes(),
				entity.getBufferMinutes(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	public ResourceEntity toEntity(Resource resource) {
		ResourceEntity entity = new ResourceEntity();
		entity.setId(resource.getId());
		entity.setName(resource.getName());
		entity.setDescription(resource.getDescription());
		entity.setType(resource.getType());
		entity.setActive(resource.isActive());
		entity.setPricePerHour(resource.getPricePerHour());
		entity.setCurrency(resource.getCurrency());
		entity.setMinDurationMinutes(resource.getMinDurationMinutes());
		entity.setMaxDurationMinutes(resource.getMaxDurationMinutes());
		entity.setBufferMinutes(resource.getBufferMinutes());
		entity.setCreatedAt(resource.getCreatedAt());
		entity.setUpdatedAt(resource.getUpdatedAt());
		return entity;
	}
}
