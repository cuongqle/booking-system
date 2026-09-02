package com.bookingsystem.interfaces.rest.booking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class BookingApiTest {

	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final AtomicInteger SLOT = new AtomicInteger();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getBookings_withoutToken_returnsUnauthorized() throws Exception {
		mockMvc.perform(get("/api/v1/bookings"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
	}

	@Test
	void createBooking_withToken_returnsCreatedAndAssociatesUser() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		String[] window = nextWindow();

		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "roomId": "A101",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(window[0], window[1])))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.roomId").value("A101"))
				.andExpect(jsonPath("$.status").value("PENDING"))
				.andExpect(jsonPath("$.userId").isNumber());
	}

	@Test
	void createBooking_withInvalidDates_returnsBadRequest() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);

		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "roomId": "A101",
								  "startDate": "2026-09-12T14:00:00",
								  "endDate": "2026-09-10T10:00:00"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_BOOKING_DATES"));
	}

	@Test
	void createBooking_withUnknownRoom_returnsBadRequest() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		String[] window = nextWindow();

		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "roomId": "Z999",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(window[0], window[1])))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_ROOM"));
	}

	@Test
	void getBooking_returnsOwnBookingDetails() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		Long bookingId = createBooking(token, "B202");

		mockMvc.perform(get("/api/v1/bookings/{id}", bookingId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookingId))
				.andExpect(jsonPath("$.roomId").value("B202"));
	}

	@Test
	void getBooking_forOtherUser_returnsNotFound() throws Exception {
		String ownerToken = AuthTestSupport.registerAndGetToken(mockMvc);
		Long bookingId = createBooking(ownerToken, "C301");

		String otherToken = AuthTestSupport.registerAndGetToken(mockMvc);

		mockMvc.perform(get("/api/v1/bookings/{id}", bookingId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + otherToken))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("BOOKING_NOT_FOUND"));
	}

	@Test
	void getBookings_returnsOnlyCurrentUserBookings() throws Exception {
		String aliceToken = AuthTestSupport.registerAndGetToken(mockMvc);
		String bobToken = AuthTestSupport.registerAndGetToken(mockMvc);

		createBooking(aliceToken, "A101");
		createBooking(bobToken, "A102");

		mockMvc.perform(get("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + aliceToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].roomId").value("A101"));
	}

	@Test
	void createBooking_withOverlappingTimes_returnsConflict() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		String[] window = nextWindow();
		createBooking(token, "B201", window[0], window[1]);

		LocalDateTime start = LocalDateTime.parse(window[0]);
		String overlapStart = start.plusMinutes(30).format(DATE_TIME);
		String overlapEnd = start.plusHours(3).format(DATE_TIME);

		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "roomId": "B201",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(overlapStart, overlapEnd)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("BOOKING_CONFLICT"));
	}

	@Test
	void createBooking_adjacentTimes_doesNotConflict() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		String[] window = nextWindow();
		createBooking(token, "C302", window[0], window[1]);

		mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "roomId": "C302",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(window[1], LocalDateTime.parse(window[1]).plusHours(2).format(DATE_TIME))))
				.andExpect(status().isCreated());
	}

	@Test
	void updateBooking_withToken_updatesOwnBookingAndStatus() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		Long bookingId = createBooking(token, "A101");
		String[] window = nextWindow();

		mockMvc.perform(put("/api/v1/bookings/{id}", bookingId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "roomId": "A101",
								  "startDate": "%s",
								  "endDate": "%s",
								  "status": "CONFIRMED"
								}
								""".formatted(window[0], window[1])))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookingId))
				.andExpect(jsonPath("$.startDate").value(window[0]))
				.andExpect(jsonPath("$.endDate").value(window[1]))
				.andExpect(jsonPath("$.status").value("CONFIRMED"));
	}

	@Test
	void updateBooking_withOverlappingTimes_returnsConflict() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		String[] first = nextWindow();
		createBooking(token, "B202", first[0], first[1]);
		Long bookingId = createBooking(token, "B202");

		LocalDateTime start = LocalDateTime.parse(first[0]);
		String overlapStart = start.plusMinutes(30).format(DATE_TIME);
		String overlapEnd = start.plusHours(3).format(DATE_TIME);

		mockMvc.perform(put("/api/v1/bookings/{id}", bookingId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "roomId": "B202",
								  "startDate": "%s",
								  "endDate": "%s",
								  "status": "PENDING"
								}
								""".formatted(overlapStart, overlapEnd)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("BOOKING_CONFLICT"));
	}

	@Test
	void updateBooking_forOtherUser_returnsNotFound() throws Exception {
		String ownerToken = AuthTestSupport.registerAndGetToken(mockMvc);
		Long bookingId = createBooking(ownerToken, "C301");
		String otherToken = AuthTestSupport.registerAndGetToken(mockMvc);
		String[] window = nextWindow();

		mockMvc.perform(put("/api/v1/bookings/{id}", bookingId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + otherToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "roomId": "C301",
								  "startDate": "%s",
								  "endDate": "%s",
								  "status": "CONFIRMED"
								}
								""".formatted(window[0], window[1])))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("BOOKING_NOT_FOUND"));
	}

	private Long createBooking(String token, String roomId) throws Exception {
		String[] window = nextWindow();
		return createBooking(token, roomId, window[0], window[1]);
	}

	private Long createBooking(String token, String roomId, String startDate, String endDate) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/bookings")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "roomId": "%s",
								  "startDate": "%s",
								  "endDate": "%s"
								}
								""".formatted(roomId, startDate, endDate)))
				.andExpect(status().isCreated())
				.andReturn();

		Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		return id.longValue();
	}

	private String[] nextWindow() {
		LocalDateTime start = LocalDateTime.of(2027, 1, 1, 8, 0).plusHours(SLOT.getAndIncrement() * 3L);
		return new String[] { start.format(DATE_TIME), start.plusHours(2).format(DATE_TIME) };
	}
}
