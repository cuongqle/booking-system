package com.bookingsystem.infrastructure.security;

import com.bookingsystem.domain.user.User;
import com.bookingsystem.domain.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final SecretKey secretKey;
	private final long expirationMs;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms}") long expirationMs) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	public String generateToken(User user) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMs);

		return Jwts.builder()
				.subject(user.getEmail())
				.claim("userId", user.getId())
				.claim("fullName", user.getFullName())
				.claim("role", user.getRole().name())
				.issuedAt(now)
				.expiration(expiry)
				.signWith(secretKey)
				.compact();
	}

	public boolean isValid(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public String extractEmail(String token) {
		return parseClaims(token).getSubject();
	}

	public Long extractUserId(String token) {
		Object userId = parseClaims(token).get("userId");
		if (userId instanceof Number number) {
			return number.longValue();
		}
		throw new IllegalArgumentException("JWT is missing userId claim");
	}

	public String extractFullName(String token) {
		Object fullName = parseClaims(token).get("fullName");
		return fullName == null ? null : fullName.toString();
	}

	public UserRole extractRole(String token) {
		Object role = parseClaims(token).get("role");
		if (role == null) {
			return UserRole.USER;
		}
		return UserRole.valueOf(role.toString());
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
