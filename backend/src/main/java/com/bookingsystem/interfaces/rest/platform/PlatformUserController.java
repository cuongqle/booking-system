package com.bookingsystem.interfaces.rest.platform;

import com.bookingsystem.application.user.UserService;
import com.bookingsystem.domain.user.UserRole;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform")
public class PlatformUserController {

	private final UserService userService;

	public PlatformUserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/organizations/{organizationId}/users")
	public List<PlatformUserResponse> list(@PathVariable Long organizationId) {
		return userService.listByOrganization(organizationId).stream()
				.map(PlatformUserResponse::from)
				.toList();
	}

	@PatchMapping("/users/{userId}")
	public PlatformUserResponse update(
			@PathVariable Long userId,
			@Valid @RequestBody UpdatePlatformUserRequest request) {
		return PlatformUserResponse.from(
				userService.updatePlatformUser(userId, request.role(), request.active()));
	}

	public record UpdatePlatformUserRequest(UserRole role, Boolean active) {
	}
}
