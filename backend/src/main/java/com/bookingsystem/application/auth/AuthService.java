package com.bookingsystem.application.auth;

import com.bookingsystem.application.organization.OrganizationService;
import com.bookingsystem.application.user.LoginCommand;
import com.bookingsystem.application.user.RegisterUserCommand;
import com.bookingsystem.application.user.UserService;
import com.bookingsystem.domain.organization.Organization;
import com.bookingsystem.domain.user.User;
import com.bookingsystem.domain.user.UserRole;
import com.bookingsystem.infrastructure.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserService userService;
	private final OrganizationService organizationService;
	private final JwtService jwtService;

	public AuthService(
			UserService userService,
			OrganizationService organizationService,
			JwtService jwtService) {
		this.userService = userService;
		this.organizationService = organizationService;
		this.jwtService = jwtService;
	}

	public AuthResult register(RegisterUserCommand command) {
		User user = userService.register(command);
		return toResult(user);
	}

	public AuthResult login(LoginCommand command) {
		User user = userService.authenticate(command);
		return toResult(user);
	}

	private AuthResult toResult(User user) {
		if (user.getRole() == UserRole.SUPER_ADMIN || user.getOrganizationId() == null) {
			return new AuthResult(
					jwtService.generateToken(user, null, null),
					user.getId(),
					null,
					null,
					null,
					user.getEmail(),
					user.getFullName(),
					user.getRole());
		}
		Organization organization = organizationService.getById(user.getOrganizationId());
		return new AuthResult(
				jwtService.generateToken(user, organization.getName(), organization.getSlug()),
				user.getId(),
				organization.getId(),
				organization.getName(),
				organization.getSlug(),
				user.getEmail(),
				user.getFullName(),
				user.getRole());
	}
}
