package com.ilyabnae.goaltracker.api.dto;

import com.ilyabnae.goaltracker.domain.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Goal returned by the API")
public record GoalResponse(
		UUID id,
		String title,
		String description,
		LocalDate dueDate,
		GoalStatus status,
		Instant createdAt,
		Instant updatedAt) {
}
