package com.ilyabnae.goaltracker.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Access token response (JWT, HS256)")
public record TokenResponse(
		@Schema(description = "JWT access token") String accessToken,
		@Schema(description = "Token type", example = "Bearer") String tokenType,
		@Schema(description = "Lifetime in seconds", example = "3600") long expiresIn) {
}
