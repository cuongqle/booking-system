package com.bookingsystem.application.user;

import com.bookingsystem.domain.user.User;
import com.bookingsystem.infrastructure.user.UserMapper;
import com.bookingsystem.infrastructure.user.UserRepository;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	public UserService(
			UserRepository userRepository,
			UserMapper userMapper,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User register(RegisterUserCommand command) {
		if (userRepository.existsByEmail(command.email())) {
			throw new UserAlreadyExistsException(command.email());
		}

		Instant now = Instant.now();
		User user = new User(
				null,
				command.email().toLowerCase().trim(),
				passwordEncoder.encode(command.password()),
				command.fullName().trim(),
				now,
				now);

		return userMapper.toDomain(userRepository.save(userMapper.toEntity(user)));
	}

	public User authenticate(LoginCommand command) {
		User user = userRepository.findByEmail(command.email().toLowerCase().trim())
				.map(userMapper::toDomain)
				.orElseThrow(InvalidCredentialsException::new);

		if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
			throw new InvalidCredentialsException();
		}

		return user;
	}

	public User getById(Long id) {
		return userRepository.findById(id)
				.map(userMapper::toDomain)
				.orElseThrow(() -> new UserNotFoundException(id));
	}
}
