package com.bookingsystem.infrastructure.notification;

import com.bookingsystem.domain.notification.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

	public Notification toDomain(NotificationEntity entity) {
		return new Notification(
				entity.getId(),
				entity.getUserId(),
				entity.getType(),
				entity.getTitle(),
				entity.getMessage(),
				entity.getLink(),
				entity.getReadAt(),
				entity.getCreatedAt());
	}

	public NotificationEntity toEntity(Notification notification) {
		NotificationEntity entity = new NotificationEntity();
		entity.setId(notification.getId());
		entity.setUserId(notification.getUserId());
		entity.setType(notification.getType());
		entity.setTitle(notification.getTitle());
		entity.setMessage(notification.getMessage());
		entity.setLink(notification.getLink());
		entity.setReadAt(notification.getReadAt());
		entity.setCreatedAt(notification.getCreatedAt());
		return entity;
	}
}
