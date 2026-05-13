package com.ilyabnae.goaltracker.domain;

// Стани цілі. У БД зберігаються як рядки (через @Enumerated(STRING) в Goal)
public enum GoalStatus {
	PENDING,      // створена, ще не почата
	IN_PROGRESS,  // в процесі виконання
	COMPLETED,    // виконана
	CANCELLED     // скасована користувачем
}
