-- Модерація цілей: адмін погоджує або відхиляє нову ціль користувача.
ALTER TABLE goal
    ADD COLUMN approval_status VARCHAR(32) NOT NULL DEFAULT 'PENDING';

CREATE INDEX idx_goal_approval_status ON goal (approval_status);
