# Тестові сценарії

Орієнтир: [BrowserStack — test scenarios](https://www.browserstack.com/guide/how-to-create-test-scenarios).

Нижче — сценарії для звіту; автоматизовані відповідники вказано в дужках.

## Профіль (US-1)

| ID | Сценарій | Очікування |
|----|-----------|------------|
| TS-P-01 | Запит `GET /api/v1/me` з валідним JWT | 200, поля профілю заповнені |
| TS-P-02 | Перший візит користувача (новий `sub`) | створено запис у БД, 200 |
| TS-N-01 | Запит без `Authorization` | 401 |

*(Інтеграційно покрито в `profile_and_goals_happyPath`)*

## Цілі — CRUD (US-2 … US-4)

| ID | Сценарій | Очікування |
|----|-----------|------------|
| TS-G-01 | `POST /api/v1/goals` з коректним JSON | 201, `status` = `PENDING` якщо не передано |
| TS-G-02 | `GET /api/v1/goals` після створення | 200, список містить ціль |
| TS-G-03 | `PUT /api/v1/goals/{id}` змінює title | 200, оновлені дані |
| TS-G-04 | `DELETE /api/v1/goals/{id}` | 204, потім список порожній |
| TS-G-N01 | `POST` з порожнім `title` | 400 |
| TS-G-N02 | `GET /goals/{id}` чужої цілі (інший `sub`) | 404 |
| TS-G-N03 | `GET /goals/{id}` випадкового UUID | 404 |
| TS-G-N04 | Будь-який `/api/**` без JWT | 401 |

## Unit (бізнес-правила)

| ID | Сценарій | Місце в коді |
|----|-----------|----------------|
| TS-U-01 | Створення цілі без статусу → `PENDING` | `GoalServiceTest#createGoal_defaultsStatusToPending` |
| TS-U-02 | Відсутня ціль → виняток | `GoalServiceTest#getGoal_notFound_throws` |
| TS-U-03 | Часткове оновлення полів | `GoalServiceTest#updateGoal_updatesFields` |

## Зв’язок «етап 4» курсової

Інтеграційні тести в `GoalApiIntegrationTest` реалізують позитивний сквозний сценарій і негативні кейси для авторизації та ізоляції даних між користувачами.
