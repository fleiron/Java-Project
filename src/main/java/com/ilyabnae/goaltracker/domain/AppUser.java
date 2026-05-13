package com.ilyabnae.goaltracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// JPA-сутність користувача. Мапиться на таблицю app_user (див. V1__init.sql).
// Lombok-анотації (@Getter/@Setter/@NoArgsConstructor) генерують геттери/сеттери
// та конструктор без аргументів, який потрібен Hibernate.
@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class AppUser {

	@Id
	private UUID id;

	// external_subject = поле "sub" з JWT. По ньому ми розпізнаємо, хто прийшов з токеном.
	// unique = жоден користувач не може дублюватися; updatable=false — змінити sub не можна
	@Column(name = "external_subject", nullable = false, unique = true, updatable = false)
	private String externalSubject;

	// Людинозрозуміле ім'я для UI (беремо з claim "name" / "preferred_username" / "email")
	@Column(name = "display_name")
	private String displayName;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	// JPA-callback: викликається перед першим INSERT.
	// Тут ми самі генеруємо UUID, щоб не залежати від послідовностей у БД.
	@PrePersist
	void onCreate() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}

	public AppUser(String externalSubject, String displayName) {
		this.externalSubject = externalSubject;
		this.displayName = displayName;
	}

}
