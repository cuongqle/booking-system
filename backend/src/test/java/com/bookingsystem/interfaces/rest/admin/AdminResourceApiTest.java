package com.bookingsystem.interfaces.rest.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookingsystem.infrastructure.user.UserRepository;
import com.bookingsystem.support.AuthTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminResourceApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Test
	void adminCanCreateAndUpdateResource() throws Exception {
		String adminToken = AuthTestSupport.registerAdminAndGetToken(mockMvc, userRepository);
		String resourceId = "T-" + System.nanoTime() % 100000;

		mockMvc.perform(post("/api/v1/admin/resources")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "id": "%s",
								  "name": "Test Desk",
								  "description": "Temporary desk",
								  "type": "DESK",
								  "active": true,
								  "pricePerHour": 15.00,
								  "currency": "USD",
								  "minDurationMinutes": 30,
								  "maxDurationMinutes": 240,
								  "bufferMinutes": 0,
								  "openTime": "08:00:00",
								  "closeTime": "18:00:00"
								}
								""".formatted(resourceId)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(resourceId))
				.andExpect(jsonPath("$.type").value("DESK"))
				.andExpect(jsonPath("$.pricePerHour").value(15.00))
				.andExpect(jsonPath("$.minDurationMinutes").value(30))
				.andExpect(jsonPath("$.openTime").value("08:00:00"))
				.andExpect(jsonPath("$.closeTime").value("18:00:00"));

		mockMvc.perform(put("/api/v1/admin/resources/{id}", resourceId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "Test Desk Updated",
								  "description": "Updated desk",
								  "type": "DESK",
								  "active": false,
								  "pricePerHour": 18.50,
								  "currency": "USD",
								  "minDurationMinutes": 60,
								  "maxDurationMinutes": 180,
								  "bufferMinutes": 10,
								  "openTime": "09:00:00",
								  "closeTime": "17:00:00"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Test Desk Updated"))
				.andExpect(jsonPath("$.active").value(false))
				.andExpect(jsonPath("$.pricePerHour").value(18.50))
				.andExpect(jsonPath("$.bufferMinutes").value(10))
				.andExpect(jsonPath("$.openTime").value("09:00:00"));

		mockMvc.perform(post("/api/v1/admin/resources/{id}/blackouts", resourceId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "startAt": "2032-01-01T00:00:00",
								  "endAt": "2032-01-02T00:00:00",
								  "reason": "Holiday"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.reason").value("Holiday"));

		mockMvc.perform(get("/api/v1/admin/resources/{id}/blackouts", resourceId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));

		mockMvc.perform(get("/api/v1/admin/resources")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id == '%s')]".formatted(resourceId)).exists());
	}

	@Test
	void nonAdminCannotManageResources() throws Exception {
		String userToken = AuthTestSupport.registerAndGetToken(mockMvc);

		mockMvc.perform(get("/api/v1/admin/resources")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));

		mockMvc.perform(post("/api/v1/admin/resources")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "id": "X999",
								  "name": "Blocked",
								  "description": null,
								  "type": "OTHER",
								  "active": true,
								  "pricePerHour": 10.00,
								  "currency": "USD",
								  "minDurationMinutes": 30,
								  "maxDurationMinutes": null,
								  "bufferMinutes": 0
								}
								"""))
				.andExpect(status().isForbidden());
	}
}
