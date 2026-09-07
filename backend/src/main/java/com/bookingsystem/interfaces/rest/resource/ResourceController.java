package com.bookingsystem.interfaces.rest.resource;

import com.bookingsystem.application.resource.ResourceService;
import com.bookingsystem.domain.resource.Resource;
import com.bookingsystem.infrastructure.security.AuthenticatedUser;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
public class ResourceController {

	private final ResourceService resourceService;

	public ResourceController(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@GetMapping
	public List<Resource> getActiveResources(@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return resourceService.getActiveResources(currentUser.getOrganizationId());
	}
}
