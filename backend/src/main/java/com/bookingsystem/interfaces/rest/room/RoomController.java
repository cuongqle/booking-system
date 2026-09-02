package com.bookingsystem.interfaces.rest.room;

import com.bookingsystem.application.room.RoomService;
import com.bookingsystem.domain.room.Room;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
public class RoomController {

	private final RoomService roomService;

	public RoomController(RoomService roomService) {
		this.roomService = roomService;
	}

	@GetMapping
	public List<Room> getRooms() {
		return roomService.getRooms();
	}
}
