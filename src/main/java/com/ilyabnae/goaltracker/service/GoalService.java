package com.ilyabnae.goaltracker.service;

import com.ilyabnae.goaltracker.api.dto.CreateGoalRequest;
import com.ilyabnae.goaltracker.api.dto.GoalResponse;
import com.ilyabnae.goaltracker.api.dto.UpdateGoalRequest;
import com.ilyabnae.goaltracker.domain.AppUser;
import com.ilyabnae.goaltracker.domain.Goal;
import com.ilyabnae.goaltracker.domain.GoalStatus;
import com.ilyabnae.goaltracker.error.ResourceNotFoundException;
import com.ilyabnae.goaltracker.repository.GoalRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoalService {

	private final GoalRepository goalRepository;
	private final UserAccountService userAccountService;

	@Transactional(readOnly = true)
	public List<GoalResponse> listMyGoals() {
		AppUser user = userAccountService.getOrCreateCurrentUser();
		return goalRepository.findAllByOwner_IdOrderByCreatedAtDesc(user.getId()).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public GoalResponse getGoal(UUID id) {
		AppUser user = userAccountService.getOrCreateCurrentUser();
		Goal goal = goalRepository
				.findByIdAndOwner_Id(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
		return toResponse(goal);
	}

	@Transactional
	public GoalResponse createGoal(CreateGoalRequest request) {
		AppUser user = userAccountService.getOrCreateCurrentUser();
		Goal goal = new Goal(
				user,
				request.title().trim(),
				blankToNull(request.description()),
				request.dueDate(),
				request.status() != null ? request.status() : GoalStatus.PENDING);
		return toResponse(goalRepository.save(goal));
	}

	@Transactional
	public GoalResponse updateGoal(UUID id, UpdateGoalRequest request) {
		AppUser user = userAccountService.getOrCreateCurrentUser();
		Goal goal = goalRepository
				.findByIdAndOwner_Id(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
		if (request.title() != null) {
			goal.setTitle(request.title().trim());
		}
		if (request.description() != null) {
			goal.setDescription(blankToNull(request.description()));
		}
		if (request.dueDate() != null) {
			goal.setDueDate(request.dueDate());
		}
		if (request.status() != null) {
			goal.setStatus(request.status());
		}
		return toResponse(goalRepository.save(goal));
	}

	@Transactional
	public void deleteGoal(UUID id) {
		AppUser user = userAccountService.getOrCreateCurrentUser();
		Goal goal = goalRepository
				.findByIdAndOwner_Id(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
		goalRepository.delete(goal);
	}

	private GoalResponse toResponse(Goal goal) {
		return new GoalResponse(
				goal.getId(),
				goal.getTitle(),
				goal.getDescription(),
				goal.getDueDate(),
				goal.getStatus(),
				goal.getCreatedAt(),
				goal.getUpdatedAt());
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value;
	}

}
