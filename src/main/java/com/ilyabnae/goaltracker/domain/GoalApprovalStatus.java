package com.ilyabnae.goaltracker.domain;

/**
 * Модерація цілі адміністратором: після створення користувачем ціль чекає на рішення.
 */
public enum GoalApprovalStatus {
	PENDING,
	APPROVED,
	REJECTED
}
