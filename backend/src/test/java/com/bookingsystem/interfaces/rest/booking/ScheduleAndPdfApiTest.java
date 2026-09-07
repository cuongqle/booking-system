package com.bookingsystem.interfaces.rest.booking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
class ScheduleAndPdfApiTest {

	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final AtomicInteger SLOT = new AtomicInteger();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Test
	void bookingOutsideOperatingHours_isRejected() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		LocalDateTime start = LocalDateTime.of(2033, 3, 15, 5, 0);
		String end = start.plusHours(1).format(DATE_TIME);

		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "resourceId": "A101",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(start.format(DATE_TIME), end)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("SCHEDULE_VIOLATION"));
	}

	@Test
	void bookingDuringBlackout_isRejected() throws Exception {
		String adminToken = AuthTestSupport.registerAdminAndGetToken(mockMvc, userRepository);
		String userToken = AuthTestSupport.registerAndGetToken(mockMvc);
		LocalDateTime start = LocalDateTime.of(2033, 4, 10, 10, 0).plusDays(SLOT.getAndIncrement());
		LocalDateTime end = start.plusHours(2);

		mockMvc.perform(post("/api/v1/admin/resources/A101/blackouts")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "startAt": "%s",
								  "endAt": "%s",
								  "reason": "Maintenance"
								}
								""".formatted(start.minusHours(1).format(DATE_TIME), end.plusHours(1).format(DATE_TIME))))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "resourceId": "A101",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(start.format(DATE_TIME), end.format(DATE_TIME))))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("BLACKOUT_CONFLICT"));
	}

	@Test
	void invoicePdf_downloadsForOwner() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		LocalDateTime start = LocalDateTime.of(2033, 5, 1, 10, 0).plusHours(SLOT.getAndIncrement() * 3L);
		MvcResult created = mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "resourceId": "B201",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(start.format(DATE_TIME), start.plusHours(2).format(DATE_TIME))))
				.andExpect(status().isCreated())
				.andReturn();
		Number bookingId = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

		mockMvc.perform(get("/api/v1/bookings/{id}/invoice/pdf", bookingId.longValue())
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
				.andExpect(header().string(
						HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"invoice-booking-" + bookingId.longValue() + ".pdf\""));
	}
}
