package com.bookingsystem.infrastructure.user;

import com.bookingsystem.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public User toDomain(UserEntity entity) {
		return new User(
				entity.getId(),
				entity.getEmail(),
				entity.getPasswordHash(),
				entity.getFullName(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	public UserEntity toEntity(User user) {
		UserEntity entity = new UserEntity();
		entity.setId(user.getId());
		entity.setEmail(user.getEmail());
		entity.setPasswordHash(user.getPasswordHash());
		entity.setFullName(user.getFullName());
		entity.setCreatedAt(user.getCreatedAt());
		entity.setUpdatedAt(user.getUpdatedAt());
		return entity;
	}
}
