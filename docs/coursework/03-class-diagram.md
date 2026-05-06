# Діаграма класів (лише бізнес-логіка)

Орієнтир: [UML class diagrams (GeeksforGeeks)](https://www.geeksforgeeks.org/unified-modeling-language-uml-class-diagrams/).

Нижче — класи, що реалізують **правила предметної області** та оркестрацію (без REST-контролерів, DTO та інфраструктури безпеки).

```mermaid
classDiagram
  direction TB

  class GoalStatus {
    <<enumeration>>
    PENDING
    IN_PROGRESS
    COMPLETED
    CANCELLED
  }

  class AppUser {
    -UUID id
    -String externalSubject
    -String displayName
    -Instant createdAt
  }

  class Goal {
    -UUID id
    -String title
    -String description
    -LocalDate dueDate
    -GoalStatus status
    -Instant createdAt
    -Instant updatedAt
  }

  class UserAccountService {
    +getOrCreateCurrentUser() AppUser
  }

  class GoalService {
    +listMyGoals() List~GoalResponse~
    +getGoal(UUID id) GoalResponse
    +createGoal(CreateGoalRequest r) GoalResponse
    +updateGoal(UUID id, UpdateGoalRequest r) GoalResponse
    +deleteGoal(UUID id) void
  }

  Goal "1" --> "1" AppUser : owner
  Goal --> GoalStatus : status
  GoalService ..> UserAccountService : поточний користувач
  GoalService ..> Goal : створення / оновлення
```

## Короткі пояснення

- **`UserAccountService`** — зв’язує JWT (`sub`) із записом `AppUser`; породжує запис при першій взаємодії.
- **`GoalService`** — усі операції з цілями виконуються в контексті поточного власника; доступ до чужої цілі трактується як відсутність ресурсу (`ResourceNotFoundException`).
