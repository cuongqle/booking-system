package com.bookingsystem.interfaces.rest.platform;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookingsystem.infrastructure.user.UserRepository;
import com.bookingsystem.support.AuthTestSupport;
import com.jayway.jsonpath.JsonPath;
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
class PlatformOrganizationApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Test
	void superAdminCanListAndCreateOrganizations() throws Exception {
		String token = AuthTestSupport.loginSuperAdminAndGetToken(mockMvc);

		mockMvc.perform(get("/api/v1/platform/organizations")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
				.andExpect(jsonPath("$[?(@.slug == 'hold')].slug").isNotEmpty())
				.andExpect(jsonPath("$[?(@.slug == 'hold')].status").value(org.hamcrest.Matchers.hasItem("ACTIVE")));

		String name = "Platform Org " + System.nanoTime();
		mockMvc.perform(post("/api/v1/platform/organizations")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "name": "%s" }
								""".formatted(name)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value(name))
				.andExpect(jsonPath("$.slug").isNotEmpty())
				.andExpect(jsonPath("$.status").value("ACTIVE"))
				.andExpect(jsonPath("$.userCount").value(0));
	}

	@Test
	void superAdminCanSuspendAndUnsuspendOrganization() throws Exception {
		String superToken = AuthTestSupport.loginSuperAdminAndGetToken(mockMvc);
		String memberEmail = AuthTestSupport.uniqueEmail();
		AuthTestSupport.registerAndGetToken(mockMvc, memberEmail, "password1", "Hold Member");

		MvcResult listResult = mockMvc.perform(get("/api/v1/platform/organizations")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + superToken))
				.andExpect(status().isOk())
				.andReturn();
		@SuppressWarnings("unchecked")
		java.util.List<Number> holdIds = JsonPath.read(
				listResult.getResponse().getContentAsString(),
				"$[?(@.slug == 'hold')].id");
		long holdId = holdIds.getFirst().longValue();

		mockMvc.perform(post("/api/v1/platform/organizations/{id}/suspend", holdId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + superToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "reason": "Policy review" }
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("SUSPENDED"))
				.andExpect(jsonPath("$.suspendedReason").value("Policy review"))
				.andExpect(jsonPath("$.suspendedAt").isNotEmpty());

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "password1"
								}
								""".formatted(memberEmail)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("ORG_SUSPENDED"));

		mockMvc.perform(post("/api/v1/platform/organizations/{id}/unsuspend", holdId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + superToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("ACTIVE"));

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "password1"
								}
								""".formatted(memberEmail)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty());
	}

	@Test
	void orgAdminCannotAccessPlatformOrganizations() throws Exception {
		String adminToken = AuthTestSupport.registerAdminAndGetToken(mockMvc, userRepository);

		mockMvc.perform(get("/api/v1/platform/organizations")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
				.andExpect(status().isForbidden());
	}

	@Test
	void superAdminCannotAccessTenantAdminApis() throws Exception {
		String token = AuthTestSupport.loginSuperAdminAndGetToken(mockMvc);

		mockMvc.perform(get("/api/v1/admin/resources")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isForbidden());
	}
}
