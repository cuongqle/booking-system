package com.bookingsystem.interfaces.rest.admin;

import com.bookingsystem.application.resource.ResourceService;
import com.bookingsystem.domain.resource.Resource;
import com.bookingsystem.domain.resource.ResourceBlackout;
import com.bookingsystem.infrastructure.security.AuthenticatedUser;
import com.bookingsystem.interfaces.rest.resource.CreateBlackoutRequest;
import com.bookingsystem.interfaces.rest.resource.CreateResourceRequest;
import com.bookingsystem.interfaces.rest.resource.UpdateResourceRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/resources")
public class AdminResourceController {

	private final ResourceService resourceService;

	public AdminResourceController(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@GetMapping
	public List<Resource> getResources(@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return resourceService.getAllResources(currentUser.getOrganizationId());
	}

	@GetMapping("/{id}")
	public Resource getResource(
			@PathVariable String id,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return resourceService.getResource(id, currentUser.getOrganizationId());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Resource createResource(
			@Valid @RequestBody CreateResourceRequest request,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return resourceService.createResource(currentUser.getOrganizationId(), request.toCommand());
	}

	@PutMapping("/{id}")
	public Resource updateResource(
			@PathVariable String id,
			@Valid @RequestBody UpdateResourceRequest request,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return resourceService.updateResource(id, currentUser.getOrganizationId(), request.toCommand());
	}

	@GetMapping("/{id}/blackouts")
	public List<ResourceBlackout> listBlackouts(
			@PathVariable String id,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return resourceService.listBlackouts(id, currentUser.getOrganizationId());
	}

	@PostMapping("/{id}/blackouts")
	@ResponseStatus(HttpStatus.CREATED)
	public ResourceBlackout createBlackout(
			@PathVariable String id,
			@Valid @RequestBody CreateBlackoutRequest request,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return resourceService.createBlackout(id, currentUser.getOrganizationId(), request.toCommand());
	}

	@DeleteMapping("/{id}/blackouts/{blackoutId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBlackout(
			@PathVariable String id,
			@PathVariable Long blackoutId,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		resourceService.deleteBlackout(id, currentUser.getOrganizationId(), blackoutId);
	}
}
