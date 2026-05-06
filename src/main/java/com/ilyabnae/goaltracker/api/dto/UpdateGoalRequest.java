package com.ilyabnae.goaltracker.api.dto;

import com.ilyabnae.goaltracker.domain.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "Partial update; only non-null fields are applied")
public record UpdateGoalRequest(
		@Size(max = 200) @Schema(example = "Finish Java coursework") String title,
		@Size(max = 4000) String description,
		LocalDate dueDate,
		GoalStatus status) {
}
