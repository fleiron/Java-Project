package com.ilyabnae.goaltracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// JPA-сутність цілі. Кожна ціль належить одному користувачу (owner).
@Entity
@Table(name = "goal")
@Getter
@Setter
@NoArgsConstructor
public class Goal {

	@Id
	private UUID id;

	// Зв'язок "багато цілей -> один користувач".
	// LAZY: Hibernate не тягне власника, поки до нього не звернутися — економимо запити.
	// optional=false: ціль не може існувати без власника.
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private AppUser owner;

	@Column(nullable = false, length = 200)
	private String title;

	@Column
	private String description;

	// Дедлайн без часу — тип LocalDate (дата). NULL означає "без дедлайну"
	@Column(name = "due_date")
	private LocalDate dueDate;

	// Зберігаємо у вигляді рядка (PENDING/IN_PROGRESS/...) — не залежимо від ordinal
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private GoalStatus status;

	// Модерація: користувач створює ціль → PENDING; адмін схвалює → APPROVED або REJECTED
	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", nullable = false, length = 32)
	private GoalApprovalStatus approvalStatus;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	// При створенні: генеруємо id, ставимо createdAt і одразу updatedAt = createdAt
	@PrePersist
	void onCreate() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		Instant now = Instant.now();
		if (createdAt == null) {
			createdAt = now;
		}
		if (approvalStatus == null) {
			approvalStatus = GoalApprovalStatus.PENDING;
		}
		updatedAt = createdAt;
	}

	// При кожному UPDATE автоматично оновлюємо updatedAt
	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public Goal(AppUser owner, String title, String description, LocalDate dueDate, GoalStatus status) {
		this.owner = owner;
		this.title = title;
		this.description = description;
		this.dueDate = dueDate;
		this.status = status;
		this.approvalStatus = GoalApprovalStatus.PENDING;
	}

}
