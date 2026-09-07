package com.bookingsystem.interfaces.rest.invoice;

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
class InvoiceListApiTest {

	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final AtomicInteger SLOT = new AtomicInteger();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Test
	void userSeesOnlyOwnInvoices_andCanFilterByStatus() throws Exception {
		String ownerToken = AuthTestSupport.registerAndGetToken(mockMvc);
		String otherToken = AuthTestSupport.registerAndGetToken(mockMvc);
		Long bookingId = createBooking(ownerToken, "A101");
		createBooking(otherToken, "B201");

		mockMvc.perform(get("/api/v1/invoices")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + ownerToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].bookingId").value(bookingId))
				.andExpect(jsonPath("$[0].status").value("UNPAID"));

		mockMvc.perform(post("/api/v1/bookings/{id}/pay", bookingId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + ownerToken))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/v1/invoices")
						.param("status", "PAID")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + ownerToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].status").value("PAID"));

		mockMvc.perform(get("/api/v1/invoices")
						.param("status", "UNPAID")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + ownerToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void adminCanListAllInvoicesWithFilters() throws Exception {
		String userToken = AuthTestSupport.registerAndGetToken(mockMvc);
		Long bookingId = createBooking(userToken, "C301");
		Number userId = JsonPath.read(
				mockMvc.perform(get("/api/v1/bookings/{id}", bookingId)
								.header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
						.andExpect(status().isOk())
						.andReturn()
						.getResponse()
						.getContentAsString(),
				"$.userId");

		String adminToken = AuthTestSupport.registerAdminAndGetToken(mockMvc, userRepository);

		mockMvc.perform(get("/api/v1/admin/invoices")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.bookingId == " + bookingId + ")]").exists());

		mockMvc.perform(get("/api/v1/admin/invoices")
						.param("status", "UNPAID")
						.param("userId", String.valueOf(userId.longValue()))
						.param("bookingId", String.valueOf(bookingId))
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].bookingId").value(bookingId))
				.andExpect(jsonPath("$[0].userId").value(userId.intValue()));

		mockMvc.perform(get("/api/v1/admin/invoices")
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
		LocalDateTime start = LocalDateTime.of(2030, 5, 1, 10, 0).plusHours(SLOT.getAndIncrement() * 3L);
		return new String[] { start.format(DATE_TIME), start.plusHours(2).format(DATE_TIME) };
	}
}
