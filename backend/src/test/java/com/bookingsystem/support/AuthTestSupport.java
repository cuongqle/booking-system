package com.bookingsystem.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public final class AuthTestSupport {

	private AuthTestSupport() {
	}

	public static String uniqueEmail() {
		return "user-%s@example.com".formatted(UUID.randomUUID());
	}

	public static String registerAndGetToken(MockMvc mockMvc, String email, String password, String fullName)
			throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "%s",
								  "fullName": "%s"
								}
								""".formatted(email, password, fullName)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andReturn();

		return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
	}

	public static String registerAndGetToken(MockMvc mockMvc) throws Exception {
		return registerAndGetToken(mockMvc, uniqueEmail(), "password1", "Test User");
	}
}
