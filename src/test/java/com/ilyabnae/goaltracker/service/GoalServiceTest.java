package com.ilyabnae.goaltracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ilyabnae.goaltracker.api.dto.CreateGoalRequest;
import com.ilyabnae.goaltracker.api.dto.UpdateGoalRequest;
import com.ilyabnae.goaltracker.domain.AppUser;
import com.ilyabnae.goaltracker.domain.Goal;
import com.ilyabnae.goaltracker.domain.GoalApprovalStatus;
import com.ilyabnae.goaltracker.domain.GoalStatus;
import com.ilyabnae.goaltracker.error.ResourceNotFoundException;
import com.ilyabnae.goaltracker.repository.GoalRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

	@Mock
	private GoalRepository goalRepository;

	@Mock
	private UserAccountService userAccountService;

	@InjectMocks
	private GoalService goalService;

	private AppUser alice;
	private UUID goalId;

	@BeforeEach
	void setUp() {
		alice = new AppUser("sub-alice", "Alice");
		alice.setId(UUID.randomUUID());
		goalId = UUID.randomUUID();
	}

	@Test
	void createGoal_defaultsStatusToPending() {
		when(userAccountService.getOrCreateCurrentUser()).thenReturn(alice);
		when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> {
			Goal g = inv.getArgument(0);
			if (g.getId() == null) {
				g.setId(goalId);
			}
			return g;
		});

		var res = goalService.createGoal(new CreateGoalRequest("Title", "Desc", LocalDate.of(2026, 6, 1), null));

		assertThat(res.title()).isEqualTo("Title");
		assertThat(res.status()).isEqualTo(GoalStatus.PENDING);
		assertThat(res.approvalStatus()).isEqualTo(GoalApprovalStatus.PENDING);
		verify(goalRepository).save(any(Goal.class));
	}

	@Test
	void getGoal_notFound_throws() {
		when(userAccountService.getOrCreateCurrentUser()).thenReturn(alice);
		when(goalRepository.findByIdAndOwner_Id(goalId, alice.getId())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> goalService.getGoal(goalId)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void listMyGoals_empty() {
		when(userAccountService.getOrCreateCurrentUser()).thenReturn(alice);
		when(goalRepository.findAllByOwner_IdOrderByCreatedAtDesc(alice.getId())).thenReturn(List.of());

		assertThat(goalService.listMyGoals()).isEmpty();
	}

	@Test
	void updateGoal_updatesFields() {
		var goal = new Goal(alice, "Old", null, null, GoalStatus.PENDING);
		goal.setId(goalId);
		goal.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
		goal.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));

		when(userAccountService.getOrCreateCurrentUser()).thenReturn(alice);
		when(goalRepository.findByIdAndOwner_Id(goalId, alice.getId())).thenReturn(Optional.of(goal));
		when(goalRepository.save(goal)).thenReturn(goal);

		var res = goalService.updateGoal(
				goalId, new UpdateGoalRequest("New", "D", LocalDate.of(2026, 2, 2), GoalStatus.IN_PROGRESS));

		assertThat(res.title()).isEqualTo("New");
		assertThat(res.description()).isEqualTo("D");
		assertThat(res.dueDate()).isEqualTo(LocalDate.of(2026, 2, 2));
		assertThat(res.status()).isEqualTo(GoalStatus.IN_PROGRESS);
	}

}
