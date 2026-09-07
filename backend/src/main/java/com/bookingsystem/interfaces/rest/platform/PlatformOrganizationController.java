package com.bookingsystem.interfaces.rest.platform;

import com.bookingsystem.application.organization.OrganizationService;
import com.bookingsystem.application.organization.OrganizationSummary;
import com.bookingsystem.domain.organization.Organization;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform/organizations")
public class PlatformOrganizationController {

	private final OrganizationService organizationService;

	public PlatformOrganizationController(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@GetMapping
	public List<OrganizationResponse> list() {
		return organizationService.listSummaries().stream()
				.map(OrganizationResponse::from)
				.toList();
	}

	@GetMapping("/{id}")
	public OrganizationResponse get(@PathVariable Long id) {
		return OrganizationResponse.from(organizationService.getSummary(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrganizationResponse create(@Valid @RequestBody CreateOrganizationRequest request) {
		Organization created = organizationService.create(request.name());
		OrganizationSummary summary = organizationService.getSummary(created.getId());
		return OrganizationResponse.from(summary);
	}

	public record CreateOrganizationRequest(
			@NotBlank @Size(max = 255) String name) {
	}
}
