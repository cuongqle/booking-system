package com.bookingsystem.application.auth;

import com.bookingsystem.application.user.LoginCommand;
import com.bookingsystem.application.user.RegisterUserCommand;
import com.bookingsystem.application.user.UserService;
import com.bookingsystem.domain.user.User;
import com.bookingsystem.infrastructure.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserService userService;
	private final JwtService jwtService;

	public AuthService(UserService userService, JwtService jwtService) {
		this.userService = userService;
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
		return new AuthResult(
				jwtService.generateToken(user),
				user.getId(),
				user.getEmail(),
				user.getFullName());
	}
}
