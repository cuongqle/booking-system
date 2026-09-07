package com.bookingsystem.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookingsystem.domain.user.User;
import com.bookingsystem.domain.user.UserRole;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService(
				"booking-system-test-secret-key-must-be-at-least-32-chars",
				3_600_000L);
	}

	@Test
	void generateAndParseToken_returnsClaims() {
		User user = new User(
				42L,
				7L,
				"alice@example.com",
				"hash",
				"Alice",
				UserRole.ADMIN,
				Instant.now(),
				Instant.now());

		String token = jwtService.generateToken(user, "Hold Demo", "hold");

		assertThat(jwtService.isValid(token)).isTrue();
		assertThat(jwtService.extractEmail(token)).isEqualTo("alice@example.com");
		assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
		assertThat(jwtService.extractOrganizationId(token)).isEqualTo(7L);
		assertThat(jwtService.extractOrganizationName(token)).isEqualTo("Hold Demo");
		assertThat(jwtService.extractOrganizationSlug(token)).isEqualTo("hold");
		assertThat(jwtService.extractRole(token)).isEqualTo(UserRole.ADMIN);
	}

	@Test
	void isValid_rejectsTamperedToken() {
		User user = new User(
				1L, 1L, "bob@example.com", "hash", "Bob", UserRole.USER, Instant.now(), Instant.now());
		String token = jwtService.generateToken(user, "Hold Demo", "hold");
		String[] parts = token.split("\\.");
		String tampered = parts[0] + "." + parts[1] + ".invalid-signature";

		assertThat(jwtService.isValid(tampered)).isFalse();
		assertThat(jwtService.isValid("not-a-jwt")).isFalse();
	}
}
