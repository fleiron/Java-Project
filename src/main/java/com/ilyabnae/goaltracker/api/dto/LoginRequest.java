package com.ilyabnae.goaltracker.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Demo login payload (username/password)")
public record LoginRequest(
		@NotBlank @Schema(example = "ilya") String username,
		@NotBlank @Schema(example = "ilya123") String password) {
}
