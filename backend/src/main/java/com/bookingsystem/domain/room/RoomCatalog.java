package com.bookingsystem.domain.room;

import java.util.List;
import java.util.Optional;

public final class RoomCatalog {

	private static final List<Room> ROOMS = List.of(
			new Room("A101", "Harbor Suite A101", "Quiet suite with harbor view"),
			new Room("A102", "Harbor Suite A102", "Corner suite near the lobby"),
			new Room("B201", "Garden Room B201", "Garden-facing twin room"),
			new Room("B202", "Garden Room B202", "Garden-facing king room"),
			new Room("C301", "Sky Loft C301", "Top-floor loft with desk space"),
			new Room("C302", "Sky Loft C302", "Top-floor loft for longer stays"));

	private RoomCatalog() {
	}

	public static List<Room> all() {
		return ROOMS;
	}

	public static boolean exists(String roomId) {
		return find(roomId).isPresent();
	}

	public static Optional<Room> find(String roomId) {
		return ROOMS.stream().filter(room -> room.id().equals(roomId)).findFirst();
	}
}
