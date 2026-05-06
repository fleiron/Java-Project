package com.ilyabnae.goaltracker.api.dto;

import com.ilyabnae.goaltracker.domain.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "Payload to create a goal")
public record CreateGoalRequest(
		@NotBlank @Size(max = 200) @Schema(example = "Finish Java coursework") String title,
		@Size(max = 4000) @Schema(example = "Implement REST, tests, Docker") String description,
		@Schema(example = "2026-06-01") LocalDate dueDate,
		@Schema(description = "Defaults to PENDING if omitted") GoalStatus status) {
}
