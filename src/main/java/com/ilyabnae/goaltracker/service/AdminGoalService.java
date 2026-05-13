package com.ilyabnae.goaltracker.service;

import com.ilyabnae.goaltracker.api.dto.GoalResponse;
import com.ilyabnae.goaltracker.domain.Goal;
import com.ilyabnae.goaltracker.domain.GoalApprovalStatus;
import com.ilyabnae.goaltracker.error.ResourceNotFoundException;
import com.ilyabnae.goaltracker.repository.GoalRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminGoalService {

	private final GoalRepository goalRepository;

	@Transactional(readOnly = true)
	public List<GoalResponse> listPendingApprovals() {
		return goalRepository.findAllByApprovalStatusOrderByCreatedAtDesc(GoalApprovalStatus.PENDING).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public GoalResponse reviewGoal(UUID id, boolean approved) {
		Goal goal = goalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
		if (goal.getApprovalStatus() != GoalApprovalStatus.PENDING) {
			throw new IllegalStateException("Goal is not pending approval");
		}
		goal.setApprovalStatus(approved ? GoalApprovalStatus.APPROVED : GoalApprovalStatus.REJECTED);
		return toResponse(goalRepository.save(goal));
	}

	private GoalResponse toResponse(Goal goal) {
		return new GoalResponse(
				goal.getId(),
				goal.getTitle(),
				goal.getDescription(),
				goal.getDueDate(),
				goal.getStatus(),
				goal.getApprovalStatus(),
				goal.getCreatedAt(),
				goal.getUpdatedAt());
	}

}
