package com.bookingsystem.application.resource;

import com.bookingsystem.domain.resource.OperatingHours;
import com.bookingsystem.domain.resource.Resource;
import com.bookingsystem.domain.resource.ResourceBlackout;
import com.bookingsystem.infrastructure.resource.ResourceBlackoutEntity;
import com.bookingsystem.infrastructure.resource.ResourceBlackoutMapper;
import com.bookingsystem.infrastructure.resource.ResourceBlackoutRepository;
import com.bookingsystem.infrastructure.resource.ResourceEntity;
import com.bookingsystem.infrastructure.resource.ResourceMapper;
import com.bookingsystem.infrastructure.resource.ResourceRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceService {

	private final ResourceRepository resourceRepository;
	private final ResourceMapper resourceMapper;
	private final ResourceBlackoutRepository blackoutRepository;
	private final ResourceBlackoutMapper blackoutMapper;

	public ResourceService(
			ResourceRepository resourceRepository,
			ResourceMapper resourceMapper,
			ResourceBlackoutRepository blackoutRepository,
			ResourceBlackoutMapper blackoutMapper) {
		this.resourceRepository = resourceRepository;
		this.resourceMapper = resourceMapper;
		this.blackoutRepository = blackoutRepository;
		this.blackoutMapper = blackoutMapper;
	}

	public List<Resource> getActiveResources(Long organizationId) {
		return resourceRepository.findByOrganizationIdAndActiveTrueOrderByIdAsc(organizationId).stream()
				.map(resourceMapper::toDomain)
				.toList();
	}

	public List<Resource> getAllResources(Long organizationId) {
		return resourceRepository.findByOrganizationIdOrderByIdAsc(organizationId).stream()
				.map(resourceMapper::toDomain)
				.toList();
	}

	public Resource getResource(String id, Long organizationId) {
		return resourceRepository.findByIdAndOrganizationId(id, organizationId)
				.map(resourceMapper::toDomain)
				.orElseThrow(() -> new ResourceNotFoundException(id));
	}

	public boolean isActiveResource(String resourceId, Long organizationId) {
		return resourceRepository.existsByIdAndOrganizationIdAndActiveTrue(resourceId, organizationId);
	}

	public boolean hasBlackoutOverlap(String resourceId, LocalDateTime start, LocalDateTime end) {
		return blackoutRepository.existsOverlapping(resourceId, start, end);
	}

	public List<ResourceBlackout> listBlackouts(String resourceId, Long organizationId) {
		getResource(resourceId, organizationId);
		return blackoutRepository.findByResourceIdOrderByStartAtAsc(resourceId).stream()
				.map(blackoutMapper::toDomain)
				.toList();
	}

	@Transactional
	public ResourceBlackout createBlackout(String resourceId, Long organizationId, CreateBlackoutCommand command) {
		getResource(resourceId, organizationId);
		if (!command.startAt().isBefore(command.endAt())) {
			throw new InvalidResourcePolicyException("Blackout startAt must be before endAt");
		}
		Instant now = Instant.now();
		ResourceBlackout blackout = new ResourceBlackout(
				null,
				resourceId,
				command.startAt(),
				command.endAt(),
				normalizeDescription(command.reason()),
				now);
		return blackoutMapper.toDomain(blackoutRepository.save(blackoutMapper.toEntity(blackout)));
	}

	@Transactional
	public void deleteBlackout(String resourceId, Long organizationId, Long blackoutId) {
		getResource(resourceId, organizationId);
		ResourceBlackoutEntity existing = blackoutRepository.findByIdAndResourceId(blackoutId, resourceId)
				.orElseThrow(() -> new BlackoutNotFoundException(blackoutId));
		blackoutRepository.delete(existing);
	}

	@Transactional
	public Resource createResource(Long organizationId, CreateResourceCommand command) {
		String id = command.id().trim().toUpperCase();
		if (resourceRepository.existsById(id)) {
			throw new ResourceAlreadyExistsException(id);
		}
		validatePricingAndStayRules(command);
		validateOperatingHours(command.openTime(), command.closeTime());

		Instant now = Instant.now();
		Resource resource = new Resource(
				id,
				organizationId,
				command.name().trim(),
				normalizeDescription(command.description()),
				command.type(),
				command.active(),
				normalizeMoney(command.pricePerHour()),
				command.currency().trim().toUpperCase(),
				command.minDurationMinutes(),
				command.maxDurationMinutes(),
				command.bufferMinutes(),
				command.openTime(),
				command.closeTime(),
				now,
				now);

		return resourceMapper.toDomain(resourceRepository.save(resourceMapper.toEntity(resource)));
	}

	@Transactional
	public Resource updateResource(String id, Long organizationId, UpdateResourceCommand command) {
		ResourceEntity existing = resourceRepository.findByIdAndOrganizationId(id, organizationId)
				.orElseThrow(() -> new ResourceNotFoundException(id));
		validatePricingAndStayRules(command);
		validateOperatingHours(command.openTime(), command.closeTime());

		existing.setName(command.name().trim());
		existing.setDescription(normalizeDescription(command.description()));
		existing.setType(command.type());
		existing.setActive(command.active());
		existing.setPricePerHour(normalizeMoney(command.pricePerHour()));
		existing.setCurrency(command.currency().trim().toUpperCase());
		existing.setMinDurationMinutes(command.minDurationMinutes());
		existing.setMaxDurationMinutes(command.maxDurationMinutes());
		existing.setBufferMinutes(command.bufferMinutes());
		existing.setOpenTime(command.openTime());
		existing.setCloseTime(command.closeTime());
		existing.setUpdatedAt(Instant.now());

		return resourceMapper.toDomain(resourceRepository.save(existing));
	}

	private void validateOperatingHours(LocalTime openTime, LocalTime closeTime) {
		try {
			OperatingHours.validateConfig(openTime, closeTime);
		} catch (IllegalArgumentException ex) {
			throw new InvalidResourcePolicyException(ex.getMessage());
		}
	}

	private void validatePricingAndStayRules(CreateResourceCommand command) {
		validatePricingAndStayRules(
				command.pricePerHour(),
				command.minDurationMinutes(),
				command.maxDurationMinutes(),
				command.bufferMinutes());
	}

	private void validatePricingAndStayRules(UpdateResourceCommand command) {
		validatePricingAndStayRules(
				command.pricePerHour(),
				command.minDurationMinutes(),
				command.maxDurationMinutes(),
				command.bufferMinutes());
	}

	private void validatePricingAndStayRules(
			BigDecimal pricePerHour,
			int minDurationMinutes,
			Integer maxDurationMinutes,
			int bufferMinutes) {
		if (pricePerHour.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidResourcePolicyException("pricePerHour must be >= 0");
		}
		if (minDurationMinutes < 1) {
			throw new InvalidResourcePolicyException("minDurationMinutes must be >= 1");
		}
		if (maxDurationMinutes != null && maxDurationMinutes < minDurationMinutes) {
			throw new InvalidResourcePolicyException(
					"maxDurationMinutes must be >= minDurationMinutes");
		}
		if (bufferMinutes < 0) {
			throw new InvalidResourcePolicyException("bufferMinutes must be >= 0");
		}
	}

	private BigDecimal normalizeMoney(BigDecimal amount) {
		return amount.setScale(2, RoundingMode.HALF_UP);
	}

	private String normalizeDescription(String description) {
		if (description == null || description.isBlank()) {
			return null;
		}
		return description.trim();
	}
}
