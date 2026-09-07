package com.bookingsystem.application.organization;

import com.bookingsystem.domain.organization.Organization;
import com.bookingsystem.domain.organization.OrganizationStatus;
import com.bookingsystem.infrastructure.organization.OrganizationEntity;
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

	public void requireActive(Organization organization) {
		if (organization.isSuspended()) {
			throw new OrganizationSuspendedException(organization.getSlug());
		}
	}

	public void requireActiveById(Long organizationId) {
		if (organizationId == null) {
			return;
		}
		requireActive(getById(organizationId));
	}

	public List<OrganizationSummary> listSummaries() {
		return organizationRepository.findAll().stream()
				.map(organizationMapper::toDomain)
				.sorted(Comparator.comparing(Organization::getName, String.CASE_INSENSITIVE_ORDER))
				.map(org -> OrganizationSummary.from(org, userRepository.countByOrganizationId(org.getId())))
				.toList();
	}

	public OrganizationSummary getSummary(Long id) {
		Organization org = getById(id);
		return OrganizationSummary.from(org, userRepository.countByOrganizationId(org.getId()));
	}

	@Transactional
	public Organization create(String name) {
		String trimmed = name == null ? "" : name.trim();
		if (trimmed.isBlank()) {
			throw new InvalidOrganizationRegistrationException("organizationName is required");
		}
		Instant now = Instant.now();
		Organization organization = new Organization(
				null,
				trimmed,
				uniqueSlug(trimmed),
				OrganizationStatus.ACTIVE,
				null,
				null,
				now,
				now);
		return organizationMapper.toDomain(organizationRepository.save(organizationMapper.toEntity(organization)));
	}

	@Transactional
	public OrganizationSummary suspend(Long id, String reason) {
		OrganizationEntity entity = organizationRepository.findById(id)
				.orElseThrow(() -> new OrganizationNotFoundException(String.valueOf(id)));
		if (entity.getStatus() == OrganizationStatus.SUSPENDED) {
			return OrganizationSummary.from(
					organizationMapper.toDomain(entity),
					userRepository.countByOrganizationId(entity.getId()));
		}
		Instant now = Instant.now();
		entity.setStatus(OrganizationStatus.SUSPENDED);
		entity.setSuspendedAt(now);
		entity.setSuspendedReason(normalizeReason(reason));
		entity.setUpdatedAt(now);
		OrganizationEntity saved = organizationRepository.save(entity);
		return OrganizationSummary.from(
				organizationMapper.toDomain(saved),
				userRepository.countByOrganizationId(saved.getId()));
	}

	@Transactional
	public OrganizationSummary unsuspend(Long id) {
		OrganizationEntity entity = organizationRepository.findById(id)
				.orElseThrow(() -> new OrganizationNotFoundException(String.valueOf(id)));
		if (entity.getStatus() != OrganizationStatus.SUSPENDED) {
			return OrganizationSummary.from(
					organizationMapper.toDomain(entity),
					userRepository.countByOrganizationId(entity.getId()));
		}
		Instant now = Instant.now();
		entity.setStatus(OrganizationStatus.ACTIVE);
		entity.setSuspendedAt(null);
		entity.setSuspendedReason(null);
		entity.setUpdatedAt(now);
		OrganizationEntity saved = organizationRepository.save(entity);
		return OrganizationSummary.from(
				organizationMapper.toDomain(saved),
				userRepository.countByOrganizationId(saved.getId()));
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

	private static String normalizeReason(String reason) {
		if (reason == null || reason.isBlank()) {
			return "Suspended by platform admin";
		}
		String trimmed = reason.trim();
		return trimmed.length() > 500 ? trimmed.substring(0, 500) : trimmed;
	}
}
