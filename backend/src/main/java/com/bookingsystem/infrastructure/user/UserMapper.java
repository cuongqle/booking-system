package com.bookingsystem.infrastructure.user;

import com.bookingsystem.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public User toDomain(UserEntity entity) {
		return new User(
				entity.getId(),
				entity.getOrganizationId(),
				entity.getEmail(),
				entity.getPasswordHash(),
				entity.getFullName(),
				entity.getRole(),
				entity.isActive(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	public UserEntity toEntity(User user) {
		UserEntity entity = new UserEntity();
		entity.setId(user.getId());
		entity.setOrganizationId(user.getOrganizationId());
		entity.setEmail(user.getEmail());
		entity.setPasswordHash(user.getPasswordHash());
		entity.setFullName(user.getFullName());
		entity.setRole(user.getRole());
		entity.setActive(user.isActive());
		entity.setCreatedAt(user.getCreatedAt());
		entity.setUpdatedAt(user.getUpdatedAt());
		return entity;
	}
}
