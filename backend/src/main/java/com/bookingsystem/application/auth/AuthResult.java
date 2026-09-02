package com.bookingsystem.application.auth;

public record AuthResult(String accessToken, Long userId, String email, String fullName) {
}
