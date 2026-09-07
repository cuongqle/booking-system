package com.bookingsystem.interfaces.rest.notification;

import com.bookingsystem.application.notification.NotificationService;
import com.bookingsystem.domain.notification.Notification;
import com.bookingsystem.infrastructure.security.AuthenticatedUser;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping
	public List<Notification> getNotifications(@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return notificationService.getNotifications(currentUser.getId());
	}

	@GetMapping("/unread-count")
	public Map<String, Long> getUnreadCount(@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return Map.of("count", notificationService.getUnreadCount(currentUser.getId()));
	}

	@PostMapping("/{id}/read")
	public Notification markRead(
			@PathVariable Long id,
			@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return notificationService.markRead(id, currentUser.getId());
	}

	@PostMapping("/read-all")
	public Map<String, Integer> markAllRead(@AuthenticationPrincipal AuthenticatedUser currentUser) {
		return Map.of("updated", notificationService.markAllRead(currentUser.getId()));
	}
}
