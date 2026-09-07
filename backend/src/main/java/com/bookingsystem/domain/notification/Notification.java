package com.bookingsystem.domain.notification;

import java.time.Instant;

public class Notification {

	private Long id;
	private Long userId;
	private NotificationType type;
	private String title;
	private String message;
	private String link;
	private Instant readAt;
	private Instant createdAt;

	public Notification() {
	}

	public Notification(
			Long id,
			Long userId,
			NotificationType type,
			String title,
			String message,
			String link,
			Instant readAt,
			Instant createdAt) {
		this.id = id;
		this.userId = userId;
		this.type = type;
		this.title = title;
		this.message = message;
		this.link = link;
		this.readAt = readAt;
		this.createdAt = createdAt;
	}

	public boolean isRead() {
		return readAt != null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Instant getReadAt() {
		return readAt;
	}

	public void setReadAt(Instant readAt) {
		this.readAt = readAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
