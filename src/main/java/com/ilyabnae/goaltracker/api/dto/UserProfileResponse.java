package com.ilyabnae.goaltracker.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Current user profile resolved from JWT and local database")
public record UserProfileResponse(UUID id, String externalSubject, String displayName) {
}
