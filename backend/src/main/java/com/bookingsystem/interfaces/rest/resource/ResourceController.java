package com.bookingsystem.interfaces.rest.resource;

import com.bookingsystem.application.resource.ResourceService;
import com.bookingsystem.domain.resource.Resource;
import java.util.List;
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
	public List<Resource> getActiveResources() {
		return resourceService.getActiveResources();
	}
}
