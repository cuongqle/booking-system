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

	public String generateToken(User user, String organizationName, String organizationSlug) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMs);

		return Jwts.builder()
				.subject(user.getEmail())
				.claim("userId", user.getId())
				.claim("organizationId", user.getOrganizationId())
				.claim("organizationName", organizationName)
				.claim("organizationSlug", organizationSlug)
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
		return extractLongClaim(token, "userId");
	}

	public Long extractOrganizationId(String token) {
		return extractLongClaim(token, "organizationId");
	}

	public String extractOrganizationName(String token) {
		Object value = parseClaims(token).get("organizationName");
		return value == null ? null : value.toString();
	}

	public String extractOrganizationSlug(String token) {
		Object value = parseClaims(token).get("organizationSlug");
		return value == null ? null : value.toString();
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

	private Long extractLongClaim(String token, String claim) {
		Object value = parseClaims(token).get(claim);
		if (value instanceof Number number) {
			return number.longValue();
		}
		throw new IllegalArgumentException("JWT is missing " + claim + " claim");
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
