package com.ilyabnae.goaltracker.repository;

import com.ilyabnae.goaltracker.domain.Goal;
import com.ilyabnae.goaltracker.domain.GoalApprovalStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data JPA: реалізацію створить сам Spring за іменами методів.
// JpaRepository<Goal, UUID> вже дає save/findById/delete/findAll тощо.
public interface GoalRepository extends JpaRepository<Goal, UUID> {

	// "знайти всі цілі певного власника, відсортовані за датою створення спадно".
	// Owner_Id означає "поле id всередині поля owner" — Spring сам зробить JOIN.
	List<Goal> findAllByOwner_IdOrderByCreatedAtDesc(UUID ownerId);

	// Перевіряємо одночасно і id цілі, і власника — захист від доступу до чужих даних
	Optional<Goal> findByIdAndOwner_Id(UUID id, UUID ownerId);

	// Усі цілі з певним статусом модерації (для адмін-панелі)
	List<Goal> findAllByApprovalStatusOrderByCreatedAtDesc(GoalApprovalStatus approvalStatus);

}
