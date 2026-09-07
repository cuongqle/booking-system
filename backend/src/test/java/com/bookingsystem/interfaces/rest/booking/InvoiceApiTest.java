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
class InvoiceApiTest {

	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final AtomicInteger SLOT = new AtomicInteger();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createBooking_createsUnpaidInvoice_andPayConfirms() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		Long bookingId = createBookingFull(token, "A101").id();

		mockMvc.perform(get("/api/v1/bookings/{id}/invoice", bookingId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.bookingId").value(bookingId))
				.andExpect(jsonPath("$.status").value("UNPAID"))
				.andExpect(jsonPath("$.method").value("STUB"))
				.andExpect(jsonPath("$.amount").value(80.00));

		mockMvc.perform(post("/api/v1/bookings/{id}/pay", bookingId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("PAID"))
				.andExpect(jsonPath("$.paidAt").isNotEmpty());

		mockMvc.perform(get("/api/v1/bookings/{id}", bookingId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CONFIRMED"));
	}

	@Test
	void confirmWithoutPayment_returnsBadRequest() throws Exception {
		String token = AuthTestSupport.registerAndGetToken(mockMvc);
		CreatedBooking created = createBookingFull(token, "B201");

		mockMvc.perform(put("/api/v1/bookings/{id}", created.id())
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "resourceId": "B201",
								  "startDate": "%s",
								  "endDate": "%s",
								  "status": "CONFIRMED"
								}
								""".formatted(created.startDate(), created.endDate())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_INVOICE_STATE"));
	}

	private CreatedBooking createBookingFull(String token, String resourceId) throws Exception {
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
		return new CreatedBooking(id.longValue(), window[0], window[1]);
	}

	private record CreatedBooking(Long id, String startDate, String endDate) {
	}

	private String[] nextWindow() {
		LocalDateTime start = LocalDateTime.of(2029, 4, 1, 10, 0).plusHours(SLOT.getAndIncrement() * 3L);
		return new String[] { start.format(DATE_TIME), start.plusHours(2).format(DATE_TIME) };
	}
}
