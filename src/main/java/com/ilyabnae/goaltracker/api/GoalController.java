package com.ilyabnae.goaltracker.api;

import com.ilyabnae.goaltracker.api.dto.CreateGoalRequest;
import com.ilyabnae.goaltracker.api.dto.GoalResponse;
import com.ilyabnae.goaltracker.api.dto.UpdateGoalRequest;
import com.ilyabnae.goaltracker.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// REST-контролер цілей. Базовий шлях /api/v1/goals; усі ендпоінти вимагають Bearer JWT.
// @Tag/@Operation — це Swagger/OpenAPI-анотації, які показують опис у /swagger-ui.html.
@RestController
@RequestMapping("/api/v1/goals")
@Validated
@RequiredArgsConstructor
@Tag(name = "Goals", description = "CRUD for personal goals (scoped to JWT subject)")
public class GoalController {

	private final GoalService goalService;

	// GET /api/v1/goals — список цілей поточного користувача
	@GetMapping
	@Operation(summary = "List goals of the current user")
	public List<GoalResponse> listGoals() {
		return goalService.listMyGoals();
	}

	// POST /api/v1/goals — створення; @Valid вмикає Bean Validation для CreateGoalRequest
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create a goal")
	public GoalResponse createGoal(@Valid @RequestBody CreateGoalRequest request) {
		return goalService.createGoal(request);
	}

	// GET /api/v1/goals/{id} — деталі цілі (тільки своєї)
	@GetMapping("/{id}")
	@Operation(summary = "Get goal by id")
	public GoalResponse getGoal(@PathVariable UUID id) {
		return goalService.getGoal(id);
	}

	// PUT /api/v1/goals/{id} — часткове оновлення (null-поля у тілі не міняють збережене значення)
	@PutMapping("/{id}")
	@Operation(summary = "Update goal (partial — null fields stay unchanged)")
	public GoalResponse updateGoal(@PathVariable UUID id, @Valid @RequestBody UpdateGoalRequest request) {
		return goalService.updateGoal(id, request);
	}

	// DELETE /api/v1/goals/{id} — видалення; повертаємо 204 No Content
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete goal")
	public void deleteGoal(@PathVariable UUID id) {
		goalService.deleteGoal(id);
	}

}
