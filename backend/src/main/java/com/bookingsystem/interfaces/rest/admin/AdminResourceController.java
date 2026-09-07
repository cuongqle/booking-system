package com.bookingsystem.interfaces.rest.admin;

import com.bookingsystem.application.resource.ResourceService;
import com.bookingsystem.domain.resource.Resource;
import com.bookingsystem.interfaces.rest.resource.CreateResourceRequest;
import com.bookingsystem.interfaces.rest.resource.UpdateResourceRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
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
	public List<Resource> getResources() {
		return resourceService.getAllResources();
	}

	@GetMapping("/{id}")
	public Resource getResource(@PathVariable String id) {
		return resourceService.getResource(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Resource createResource(@Valid @RequestBody CreateResourceRequest request) {
		return resourceService.createResource(request.toCommand());
	}

	@PutMapping("/{id}")
	public Resource updateResource(
			@PathVariable String id,
			@Valid @RequestBody UpdateResourceRequest request) {
		return resourceService.updateResource(id, request.toCommand());
	}
}
