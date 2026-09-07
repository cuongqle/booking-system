package com.bookingsystem.application.resource;

import com.bookingsystem.domain.resource.Resource;
import com.bookingsystem.infrastructure.resource.ResourceEntity;
import com.bookingsystem.infrastructure.resource.ResourceMapper;
import com.bookingsystem.infrastructure.resource.ResourceRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceService {

	private final ResourceRepository resourceRepository;
	private final ResourceMapper resourceMapper;

	public ResourceService(ResourceRepository resourceRepository, ResourceMapper resourceMapper) {
		this.resourceRepository = resourceRepository;
		this.resourceMapper = resourceMapper;
	}

	public List<Resource> getActiveResources() {
		return resourceRepository.findByActiveTrueOrderByIdAsc().stream()
				.map(resourceMapper::toDomain)
				.toList();
	}

	public List<Resource> getAllResources() {
		return resourceRepository.findAllByOrderByIdAsc().stream()
				.map(resourceMapper::toDomain)
				.toList();
	}

	public Resource getResource(String id) {
		return resourceRepository.findById(id)
				.map(resourceMapper::toDomain)
				.orElseThrow(() -> new ResourceNotFoundException(id));
	}

	public boolean isActiveResource(String resourceId) {
		return resourceRepository.existsByIdAndActiveTrue(resourceId);
	}

	@Transactional
	public Resource createResource(CreateResourceCommand command) {
		String id = command.id().trim().toUpperCase();
		if (resourceRepository.existsById(id)) {
			throw new ResourceAlreadyExistsException(id);
		}
		validatePricingAndStayRules(command);

		Instant now = Instant.now();
		Resource resource = new Resource(
				id,
				command.name().trim(),
				normalizeDescription(command.description()),
				command.type(),
				command.active(),
				normalizeMoney(command.pricePerHour()),
				command.currency().trim().toUpperCase(),
				command.minDurationMinutes(),
				command.maxDurationMinutes(),
				command.bufferMinutes(),
				now,
				now);

		return resourceMapper.toDomain(resourceRepository.save(resourceMapper.toEntity(resource)));
	}

	@Transactional
	public Resource updateResource(String id, UpdateResourceCommand command) {
		ResourceEntity existing = resourceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(id));
		validatePricingAndStayRules(command);

		existing.setName(command.name().trim());
		existing.setDescription(normalizeDescription(command.description()));
		existing.setType(command.type());
		existing.setActive(command.active());
		existing.setPricePerHour(normalizeMoney(command.pricePerHour()));
		existing.setCurrency(command.currency().trim().toUpperCase());
		existing.setMinDurationMinutes(command.minDurationMinutes());
		existing.setMaxDurationMinutes(command.maxDurationMinutes());
		existing.setBufferMinutes(command.bufferMinutes());
		existing.setUpdatedAt(Instant.now());

		return resourceMapper.toDomain(resourceRepository.save(existing));
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
