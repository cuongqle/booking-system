package com.bookingsystem.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookingsystem.domain.user.User;
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
		User user = new User(42L, "alice@example.com", "hash", "Alice", Instant.now(), Instant.now());

		String token = jwtService.generateToken(user);

		assertThat(jwtService.isValid(token)).isTrue();
		assertThat(jwtService.extractEmail(token)).isEqualTo("alice@example.com");
		assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
	}

	@Test
	void isValid_rejectsTamperedToken() {
		User user = new User(1L, "bob@example.com", "hash", "Bob", Instant.now(), Instant.now());
		String token = jwtService.generateToken(user);
		String[] parts = token.split("\\.");
		String tampered = parts[0] + "." + parts[1] + ".invalid-signature";

		assertThat(jwtService.isValid(tampered)).isFalse();
		assertThat(jwtService.isValid("not-a-jwt")).isFalse();
	}
}
