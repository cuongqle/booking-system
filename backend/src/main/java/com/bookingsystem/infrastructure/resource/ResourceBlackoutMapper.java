package com.bookingsystem.infrastructure.resource;

import com.bookingsystem.domain.resource.ResourceBlackout;
import org.springframework.stereotype.Component;

@Component
public class ResourceBlackoutMapper {

	public ResourceBlackout toDomain(ResourceBlackoutEntity entity) {
		return new ResourceBlackout(
				entity.getId(),
				entity.getResourceId(),
				entity.getStartAt(),
				entity.getEndAt(),
				entity.getReason(),
				entity.getCreatedAt());
	}

	public ResourceBlackoutEntity toEntity(ResourceBlackout blackout) {
		ResourceBlackoutEntity entity = new ResourceBlackoutEntity();
		entity.setId(blackout.getId());
		entity.setResourceId(blackout.getResourceId());
		entity.setStartAt(blackout.getStartAt());
		entity.setEndAt(blackout.getEndAt());
		entity.setReason(blackout.getReason());
		entity.setCreatedAt(blackout.getCreatedAt());
		return entity;
	}
}
