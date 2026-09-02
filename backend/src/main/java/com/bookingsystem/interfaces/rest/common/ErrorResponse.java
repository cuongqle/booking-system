package com.bookingsystem.interfaces.rest.common;

public record ErrorResponse(int status, String code, String message) {
}
