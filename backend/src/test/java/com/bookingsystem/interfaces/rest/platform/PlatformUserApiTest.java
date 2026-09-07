package com.bookingsystem.interfaces.rest.platform;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookingsystem.support.AuthTestSupport;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
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
class PlatformUserApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void superAdminCanListAndUpdateOrgUsers() throws Exception {
		String superToken = AuthTestSupport.loginSuperAdminAndGetToken(mockMvc);
		String memberEmail = AuthTestSupport.uniqueEmail();
		AuthTestSupport.registerAndGetToken(mockMvc, memberEmail, "password1", "Hold Member");

		MvcResult orgs = mockMvc.perform(get("/api/v1/platform/organizations")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + superToken))
				.andExpect(status().isOk())
				.andReturn();
		@SuppressWarnings("unchecked")
		List<Number> holdIds = JsonPath.read(orgs.getResponse().getContentAsString(), "$[?(@.slug == 'hold')].id");
		long holdId = holdIds.getFirst().longValue();

		MvcResult users = mockMvc.perform(get("/api/v1/platform/organizations/{id}/users", holdId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + superToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
				.andReturn();

		@SuppressWarnings("unchecked")
		List<Number> memberIds = JsonPath.read(
				users.getResponse().getContentAsString(),
				"$[?(@.email == '%s')].id".formatted(memberEmail));
		long memberId = memberIds.getFirst().longValue();

		mockMvc.perform(patch("/api/v1/platform/users/{id}", memberId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + superToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "role": "ADMIN" }
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.role").value("ADMIN"))
				.andExpect(jsonPath("$.active").value(true));

		mockMvc.perform(patch("/api/v1/platform/users/{id}", memberId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + superToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "active": false }
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active").value(false));

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "password1"
								}
								""".formatted(memberEmail)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("USER_INACTIVE"));

		mockMvc.perform(patch("/api/v1/platform/users/{id}", memberId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + superToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "active": true, "role": "USER" }
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active").value(true))
				.andExpect(jsonPath("$.role").value("USER"));
	}
}
