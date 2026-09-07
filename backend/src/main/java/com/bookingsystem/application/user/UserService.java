package com.bookingsystem.application.user;

import com.bookingsystem.application.organization.InvalidOrganizationRegistrationException;
import com.bookingsystem.application.organization.OrganizationService;
import com.bookingsystem.domain.organization.Organization;
import com.bookingsystem.domain.user.User;
import com.bookingsystem.domain.user.UserRole;
import com.bookingsystem.infrastructure.user.UserEntity;
import com.bookingsystem.infrastructure.user.UserMapper;
import com.bookingsystem.infrastructure.user.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final OrganizationService organizationService;

	public UserService(
			UserRepository userRepository,
			UserMapper userMapper,
			PasswordEncoder passwordEncoder,
			OrganizationService organizationService) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
		this.organizationService = organizationService;
	}

	@Transactional
	public User register(RegisterUserCommand command) {
		if (userRepository.existsByEmail(command.email())) {
			throw new UserAlreadyExistsException(command.email());
		}

		boolean createOrg = hasText(command.organizationName());
		boolean joinOrg = hasText(command.organizationSlug());
		if (createOrg == joinOrg) {
			throw new InvalidOrganizationRegistrationException(
					"Provide either organizationName (create) or organizationSlug (join)");
		}

		Organization organization;
		UserRole role;
		if (createOrg) {
			organization = organizationService.create(command.organizationName());
			role = UserRole.ADMIN;
		} else {
			organization = organizationService.getBySlug(command.organizationSlug());
			organizationService.requireActive(organization);
			role = UserRole.USER;
		}

		Instant now = Instant.now();
		User user = new User(
				null,
				organization.getId(),
				command.email().toLowerCase().trim(),
				passwordEncoder.encode(command.password()),
				command.fullName().trim(),
				role,
				true,
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

		if (!user.isActive()) {
			throw new UserInactiveException();
		}

		if (user.getOrganizationId() != null) {
			organizationService.requireActiveById(user.getOrganizationId());
		}

		return user;
	}

	public User getById(Long id) {
		return userRepository.findById(id)
				.map(userMapper::toDomain)
				.orElseThrow(() -> new UserNotFoundException(id));
	}

	public void requireActive(Long userId) {
		User user = getById(userId);
		if (!user.isActive()) {
			throw new UserInactiveException();
		}
	}

	public List<UserSummary> listByOrganization(Long organizationId) {
		organizationService.getById(organizationId);
		return userRepository.findByOrganizationIdOrderByFullNameAscEmailAsc(organizationId).stream()
				.map(this::toSummary)
				.toList();
	}

	@Transactional
	public UserSummary updatePlatformUser(Long userId, UserRole role, Boolean active) {
		if (role == null && active == null) {
			throw new InvalidUserManagementException("Provide role and/or active");
		}

		UserEntity entity = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userId));

		if (entity.getOrganizationId() == null || entity.getRole() == UserRole.SUPER_ADMIN) {
			throw new InvalidUserManagementException("Platform users cannot be managed here");
		}

		organizationService.getById(entity.getOrganizationId());

		if (role != null) {
			if (role != UserRole.USER && role != UserRole.ADMIN) {
				throw new InvalidUserManagementException("Role must be USER or ADMIN");
			}
			entity.setRole(role);
		}
		if (active != null) {
			entity.setActive(active);
		}
		entity.setUpdatedAt(Instant.now());

		return toSummary(userRepository.save(entity));
	}

	private UserSummary toSummary(UserEntity entity) {
		return new UserSummary(
				entity.getId(),
				entity.getOrganizationId(),
				entity.getEmail(),
				entity.getFullName(),
				entity.getRole(),
				entity.isActive(),
				entity.getCreatedAt());
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
