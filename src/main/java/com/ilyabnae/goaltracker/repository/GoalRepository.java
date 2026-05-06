package com.ilyabnae.goaltracker.repository;

import com.ilyabnae.goaltracker.domain.Goal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

	List<Goal> findAllByOwner_IdOrderByCreatedAtDesc(UUID ownerId);

	Optional<Goal> findByIdAndOwner_Id(UUID id, UUID ownerId);

}
