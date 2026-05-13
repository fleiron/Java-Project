package com.ilyabnae.goaltracker.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Рішення адміністратора щодо цілі")
public record AdminReviewRequest(
		@NotNull @Schema(description = "true = схвалити, false = відхилити", example = "true") Boolean approved) {
}
