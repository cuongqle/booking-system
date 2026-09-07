package com.bookingsystem.application.notification;

import com.bookingsystem.domain.booking.Booking;
import com.bookingsystem.domain.notification.Notification;
import com.bookingsystem.domain.notification.NotificationType;
import com.bookingsystem.infrastructure.notification.NotificationEntity;
import com.bookingsystem.infrastructure.notification.NotificationMapper;
import com.bookingsystem.infrastructure.notification.NotificationRepository;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

	private static final DateTimeFormatter RANGE = DateTimeFormatter.ofPattern("MMM d, HH:mm");

	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;

	public NotificationService(
			NotificationRepository notificationRepository,
			NotificationMapper notificationMapper) {
		this.notificationRepository = notificationRepository;
		this.notificationMapper = notificationMapper;
	}

	public List<Notification> getNotifications(Long userId) {
		return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
				.limit(50)
				.map(notificationMapper::toDomain)
				.toList();
	}

	public long getUnreadCount(Long userId) {
		return notificationRepository.countByUserIdAndReadAtIsNull(userId);
	}

	@Transactional
	public Notification markRead(Long id, Long userId) {
		NotificationEntity entity = notificationRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new NotificationNotFoundException(id));
		if (entity.getReadAt() == null) {
			entity.setReadAt(Instant.now());
			entity = notificationRepository.save(entity);
		}
		return notificationMapper.toDomain(entity);
	}

	@Transactional
	public int markAllRead(Long userId) {
		return notificationRepository.markAllReadForUser(userId, Instant.now());
	}

	@Transactional
	public void notifyBookingCreated(Booking booking) {
		create(
				booking.getUserId(),
				NotificationType.BOOKING_CREATED,
				"Booking created",
				"Reservation for %s is pending (%s → %s)."
						.formatted(
								booking.getResourceId(),
								RANGE.format(booking.getStartDate()),
								RANGE.format(booking.getEndDate())),
				"/bookings/" + booking.getId());
	}

	@Transactional
	public void notifyBookingUpdated(Booking booking) {
		create(
				booking.getUserId(),
				NotificationType.BOOKING_UPDATED,
				"Booking updated",
				"Reservation for %s is now %s (%s → %s)."
						.formatted(
								booking.getResourceId(),
								booking.getStatus().name().toLowerCase().replace('_', ' '),
								RANGE.format(booking.getStartDate()),
								RANGE.format(booking.getEndDate())),
				"/bookings/" + booking.getId());
	}

	private void create(
			Long userId,
			NotificationType type,
			String title,
			String message,
			String link) {
		Notification notification = new Notification(
				null,
				userId,
				type,
				title,
				message,
				link,
				null,
				Instant.now());
		notificationRepository.save(notificationMapper.toEntity(notification));
	}
}
