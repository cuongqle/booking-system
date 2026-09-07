package com.bookingsystem.interfaces.rest.platform;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
				.andExpect(jsonPath("$[?(@.slug == 'hold')].slug").isNotEmpty());

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
				.andExpect(jsonPath("$.userCount").value(0));
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
