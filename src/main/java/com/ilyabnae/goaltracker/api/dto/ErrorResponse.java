package com.ilyabnae.goaltracker.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error payload")
public record ErrorResponse(String message) {
}
