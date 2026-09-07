package com.bookingsystem.application.organization;

import com.bookingsystem.domain.organization.Organization;
import com.bookingsystem.infrastructure.organization.OrganizationMapper;
import com.bookingsystem.infrastructure.organization.OrganizationRepository;
import com.bookingsystem.infrastructure.user.UserRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

	private final OrganizationRepository organizationRepository;
	private final OrganizationMapper organizationMapper;
	private final UserRepository userRepository;

	public OrganizationService(
			OrganizationRepository organizationRepository,
			OrganizationMapper organizationMapper,
			UserRepository userRepository) {
		this.organizationRepository = organizationRepository;
		this.organizationMapper = organizationMapper;
		this.userRepository = userRepository;
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

	public List<OrganizationSummary> listSummaries() {
		return organizationRepository.findAll().stream()
				.map(organizationMapper::toDomain)
				.sorted(Comparator.comparing(Organization::getName, String.CASE_INSENSITIVE_ORDER))
				.map(org -> new OrganizationSummary(
						org.getId(),
						org.getName(),
						org.getSlug(),
						userRepository.countByOrganizationId(org.getId()),
						org.getCreatedAt()))
				.toList();
	}

	public OrganizationSummary getSummary(Long id) {
		Organization org = getById(id);
		return new OrganizationSummary(
				org.getId(),
				org.getName(),
				org.getSlug(),
				userRepository.countByOrganizationId(org.getId()),
				org.getCreatedAt());
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
