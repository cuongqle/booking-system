package com.bookingsystem.interfaces.rest.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookingsystem.infrastructure.user.UserRepository;
import com.bookingsystem.support.AuthTestSupport;
import com.jayway.jsonpath.JsonPath;
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
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AdminBookingApiTest {

	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final AtomicInteger SLOT = new AtomicInteger();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Test
	void adminCanListAllBookingsWithFilters() throws Exception {
		String userToken = AuthTestSupport.registerAndGetToken(mockMvc);
		Long bookingId = createBooking(userToken, "A101");
		Number userId = JsonPath.read(
				mockMvc.perform(get("/api/v1/bookings/{id}", bookingId)
								.header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
						.andExpect(status().isOk())
						.andReturn()
						.getResponse()
						.getContentAsString(),
				"$.userId");

		String adminToken = AuthTestSupport.registerAdminAndGetToken(mockMvc, userRepository);

		mockMvc.perform(get("/api/v1/admin/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id == " + bookingId + ")]").exists());

		mockMvc.perform(get("/api/v1/admin/bookings")
						.param("status", "PENDING")
						.param("resourceId", "A101")
						.param("userId", String.valueOf(userId.longValue()))
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(bookingId))
				.andExpect(jsonPath("$[0].userId").value(userId.intValue()));

		mockMvc.perform(get("/api/v1/admin/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
				.andExpect(status().isForbidden());
	}

	private Long createBooking(String token, String resourceId) throws Exception {
		String[] window = nextWindow();
		MvcResult result = mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "resourceId": "%s",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(resourceId, window[0], window[1])))
				.andExpect(status().isCreated())
				.andReturn();
		Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		return id.longValue();
	}

	private String[] nextWindow() {
		LocalDateTime start = LocalDateTime.of(2031, 6, 1, 10, 0).plusHours(SLOT.getAndIncrement() * 3L);
		return new String[] { start.format(DATE_TIME), start.plusHours(2).format(DATE_TIME) };
	}
}
