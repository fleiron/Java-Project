-- Перша міграція Flyway. Виконується автоматично при старті застосунку.
-- Імена файлів обов'язково у форматі V<версія>__<опис>.sql

-- Таблиця користувачів. external_subject — це поле "sub" з JWT.
CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    external_subject VARCHAR(512) NOT NULL UNIQUE,
    display_name VARCHAR(512),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

-- Таблиця цілей. ON DELETE CASCADE: при видаленні користувача — його цілі теж видаляються.
CREATE TABLE goal (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(4000),
    due_date DATE,
    status VARCHAR(32) NOT NULL, -- значення enum GoalStatus у вигляді тексту
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

-- Індекси для типових вибірок: усі цілі користувача та фільтр за дедлайном
CREATE INDEX idx_goal_user_id ON goal (user_id);
CREATE INDEX idx_goal_due_date ON goal (due_date);
