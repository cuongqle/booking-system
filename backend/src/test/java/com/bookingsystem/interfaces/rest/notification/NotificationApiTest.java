package com.bookingsystem.interfaces.rest.notification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookingsystem.support.AuthTestSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationApiTest {

	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final AtomicInteger SLOT = new AtomicInteger();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createBooking_createsInAppNotification() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		String[] window = nextWindow();

		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "resourceId": "A101",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(window[0], window[1])))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/v1/notifications/unread-count")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1));

		mockMvc.perform(get("/api/v1/notifications")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].type").value("BOOKING_CREATED"))
				.andExpect(jsonPath("$[0].title").value("Booking created"))
				.andExpect(jsonPath("$[0].read").value(false))
				.andExpect(jsonPath("$[0].link").value(org.hamcrest.Matchers.startsWith("/bookings/")));
	}

	@Test
	void markRead_andMarkAllRead_clearUnread() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		String[] window = nextWindow();

		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "resourceId": "B201",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(window[0], window[1])))
				.andExpect(status().isCreated());

		String body = mockMvc.perform(get("/api/v1/notifications")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Number id = com.jayway.jsonpath.JsonPath.read(body, "$[0].id");

		mockMvc.perform(post("/api/v1/notifications/{id}/read", id.longValue())
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.read").value(true));

		mockMvc.perform(get("/api/v1/notifications/unread-count")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(0));

		String[] second = nextWindow();
		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "resourceId": "B202",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(second[0], second[1])))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/v1/notifications/read-all")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.updated").value(1));

		mockMvc.perform(get("/api/v1/notifications/unread-count")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(0));
	}

	private String[] nextWindow() {
		LocalDateTime start = LocalDateTime.of(2028, 3, 1, 9, 0).plusHours(SLOT.getAndIncrement() * 3L);
		return new String[] { start.format(DATE_TIME), start.plusHours(2).format(DATE_TIME) };
	}
}
