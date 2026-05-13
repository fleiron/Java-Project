package com.ilyabnae.goaltracker.api;

import com.ilyabnae.goaltracker.api.dto.AdminReviewRequest;
import com.ilyabnae.goaltracker.api.dto.GoalResponse;
import com.ilyabnae.goaltracker.service.AdminGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/goals")
@RequiredArgsConstructor
@Tag(name = "Admin goals", description = "Модерація цілей (тільки ROLE_ADMIN)")
public class AdminGoalController {

	private final AdminGoalService adminGoalService;

	@GetMapping("/pending")
	@Operation(summary = "Список цілей, що чекають на погодження")
	public List<GoalResponse> listPending() {
		return adminGoalService.listPendingApprovals();
	}

	@PostMapping("/{id}/review")
	@Operation(summary = "Схвалити або відхилити ціль")
	public GoalResponse review(@PathVariable UUID id, @Valid @RequestBody AdminReviewRequest request) {
		return adminGoalService.reviewGoal(id, Boolean.TRUE.equals(request.approved()));
	}

}
