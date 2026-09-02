package com.bookingsystem.application.room;

import com.bookingsystem.domain.room.Room;
import com.bookingsystem.domain.room.RoomCatalog;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

	public List<Room> getRooms() {
		return RoomCatalog.all();
	}
}
