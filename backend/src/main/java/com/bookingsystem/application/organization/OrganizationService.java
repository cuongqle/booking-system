package com.bookingsystem.application.organization;

import com.bookingsystem.domain.organization.Organization;
import com.bookingsystem.infrastructure.organization.OrganizationMapper;
import com.bookingsystem.infrastructure.organization.OrganizationRepository;
import java.time.Instant;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

	private final OrganizationRepository organizationRepository;
	private final OrganizationMapper organizationMapper;

	public OrganizationService(
			OrganizationRepository organizationRepository,
			OrganizationMapper organizationMapper) {
		this.organizationRepository = organizationRepository;
		this.organizationMapper = organizationMapper;
	}

	public Organization getById(Long id) {
		return organizationRepository.findById(id)
				.map(organizationMapper::toDomain)
				.orElseThrow(() -> new OrganizationNotFoundException(String.valueOf(id)));
	}

	public Organization getBySlug(String slug) {
		String normalized = normalizeSlug(slug);
		return organizationRepository.findBySlug(normalized)
				.map(organizationMapper::toDomain)
				.orElseThrow(() -> new OrganizationNotFoundException(normalized));
	}

	@Transactional
	public Organization create(String name) {
		String trimmed = name == null ? "" : name.trim();
		if (trimmed.isBlank()) {
			throw new InvalidOrganizationRegistrationException("organizationName is required");
		}
		Instant now = Instant.now();
		Organization organization = new Organization(null, trimmed, uniqueSlug(trimmed), now, now);
		return organizationMapper.toDomain(organizationRepository.save(organizationMapper.toEntity(organization)));
	}

	private String uniqueSlug(String name) {
		String base = toSlug(name);
		String candidate = base;
		int suffix = 2;
		while (organizationRepository.existsBySlug(candidate)) {
			candidate = base + "-" + suffix;
			suffix++;
		}
		return candidate;
	}

	private static String toSlug(String name) {
		String slug = name.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("^-+|-+$", "");
		if (slug.isBlank()) {
			slug = "org";
		}
		if (slug.length() > 80) {
			slug = slug.substring(0, 80).replaceAll("-+$", "");
		}
		return slug;
	}

	private static String normalizeSlug(String slug) {
		if (slug == null || slug.isBlank()) {
			throw new InvalidOrganizationRegistrationException("organizationSlug is required");
		}
		return slug.trim().toLowerCase(Locale.ROOT);
	}
}
