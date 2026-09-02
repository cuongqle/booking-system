package com.bookingsystem.interfaces.rest.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookingsystem.support.AuthTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void register_returnsCreatedWithToken() throws Exception {
		String email = AuthTestSupport.uniqueEmail();

		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "password1",
								  "fullName": "Alice"
								}
								""".formatted(email)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.fullName").value("Alice"))
				.andExpect(jsonPath("$.userId").isNumber());
	}

	@Test
	void register_duplicateEmail_returnsConflict() throws Exception {
		String email = AuthTestSupport.uniqueEmail();
		AuthTestSupport.registerAndGetToken(mockMvc, email, "password1", "Alice");

		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "password1",
								  "fullName": "Alice Again"
								}
								""".formatted(email)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"));
	}

	@Test
	void register_invalidPayload_returnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "not-an-email",
								  "password": "short",
								  "fullName": ""
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
	}

	@Test
	void login_withValidCredentials_returnsToken() throws Exception {
		String email = AuthTestSupport.uniqueEmail();
		AuthTestSupport.registerAndGetToken(mockMvc, email, "password1", "Alice");

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "password1"
								}
								""".formatted(email)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.email").value(email));
	}

	@Test
	void login_withWrongPassword_returnsUnauthorized() throws Exception {
		String email = AuthTestSupport.uniqueEmail();
		AuthTestSupport.registerAndGetToken(mockMvc, email, "password1", "Alice");

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "wrong-password"
								}
								""".formatted(email)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
	}
}
