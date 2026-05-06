# User stories та діаграми послідовностей (Goal Tracker)

Джерело підходу: [IAMPM — як писати user stories](https://iampm.club/ua/blog/yak-pisati-user-stories-shhob-bulo-zrozumilo-vsim/).

## Формат

**Як** \<роль\>, **я хочу** \<дія\>, **щоб** \<цінність\>.

## Ролі

- **Користувач** — автентифікований суб’єкт OAuth2 / JWT (`sub` у токені).
- **Система** — Spring Boot REST API + PostgreSQL.

---

### US-1: Переглянути профіль

**Як** користувач, **я хочу** отримати свій профіль у системі, **щоб** переконатися, що акаунт синхронізовано з JWT.

**Критерії приймання:** відповідь містить `id`, `externalSubject`, `displayName`; при першому зверненні користувач створюється в БД.

```mermaid
sequenceDiagram
  actor U as Користувач
  participant API as REST /api/v1/me
  participant S as UserAccountService
  participant DB as PostgreSQL

  U->>API: GET /api/v1/me (Bearer JWT)
  API->>S: getOrCreateCurrentUser()
  S->>S: прочитати sub з JWT
  S->>DB: findByExternalSubject(sub)
  alt користувач існує
    DB-->>S: AppUser
  else новий
    S->>DB: insert AppUser
    DB-->>S: AppUser
  end
  S-->>API: AppUser
  API-->>U: 200 UserProfileResponse
```

---

### US-2: Створити ціль

**Як** користувач, **я хочу** додати ціль (назва, опис, дедлайн, статус), **щоб** фіксувати плани.

**Критерії приймання:** `title` обов’язковий; якщо `status` не передано — `PENDING`; відповідь `201` з об’єктом цілі.

```mermaid
sequenceDiagram
  actor U as Користувач
  participant API as REST /api/v1/goals
  participant GS as GoalService
  participant DB as PostgreSQL

  U->>API: POST /api/v1/goals + JSON
  API->>GS: createGoal(request)
  GS->>GS: getOrCreateCurrentUser()
  GS->>DB: save Goal
  DB-->>GS: Goal з id
  GS-->>API: GoalResponse
  API-->>U: 201 Created
```

---

### US-3: Переглянути список своїх цілей

**Як** користувач, **я хочу** бачити лише свої цілі, **щоб** не змішувати дані з іншими користувачами.

```mermaid
sequenceDiagram
  actor U as Користувач
  participant API as REST /api/v1/goals
  participant GS as GoalService
  participant DB as PostgreSQL

  U->>API: GET /api/v1/goals
  API->>GS: listMyGoals()
  GS->>DB: findAllByOwner_Id(...)
  DB-->>GS: список Goal
  GS-->>API: List GoalResponse
  API-->>U: 200 OK
```

---

### US-4: Оновити / видалити ціль

**Як** користувач, **я хочу** оновлювати поля або видаляти ціль, **щоб** підтримувати актуальний стан.

**Критерії:** доступ лише до власних цілей; чужий `id` → `404` (ненавмисне розкриття відсутнє).

```mermaid
sequenceDiagram
  actor U as Користувач
  participant API as REST /api/v1/goals/{id}
  participant GS as GoalService
  participant DB as PostgreSQL

  U->>API: PUT /api/v1/goals/{id}
  API->>GS: updateGoal(id, body)
  GS->>DB: findByIdAndOwner_Id
  alt знайдено
    GS->>DB: save Goal
    API-->>U: 200 OK
  else не знайдено / не моя ціль
    API-->>U: 404 Not Found
  end
```

---

### US-5: Авторизація запитів

**Як** система, **я хочу** приймати лише запити з валідним JWT (HS256), **щоб** захистити API.

**Критерії:** без `Authorization` → `401`; Swagger та OpenAPI доступні без токена для документації.

---

## Зв’язок з інтеграційними тестами

Сценарії з `GoalApiIntegrationTest` покривають: US-2–US-5 (позитивні та негативні кейси).
