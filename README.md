# 💪 WorkoutApp

Повнофункціональний застосунок для персоналізованого планування тренувань з автоматичною генерацією планів, відстеженням прогресу та адмін-панеллю.

---

## 🧩 Технологічний стек

### Backend
- **Java 21** + **Spring Boot 4.0.4**
- **Spring Security** + **JWT** автентифікація
- **MongoDB** (Spring Data MongoDB)
- **MapStruct** для маппінгу між шарами
- **Lombok 1.18.36** для скорочення boilerplate
- **JavaMailSender** для email-сповіщень
- **ArchUnit 1.3.0** для архітектурних тестів
- **JUnit 5** + **Mockito** для unit-тестів

### Frontend
- **Angular 21** (standalone компоненти, signals API)
- **TypeScript 5.9**
- **RxJS** для реактивних потоків
- **Lucide Angular** для іконок
- **Vitest** для unit-тестів
- CSS Variables + темна/світла теми

---

## 🏗️ Архітектура

Проєкт побудований на принципах **Clean Architecture** з чітким розділенням на 4 шари:

- **domain/**          ← Сутності, репозиторії (інтерфейси), виключення, enum-и
- **application/**     ← Use Cases, команди, порти, сервіси, стратегії
- **infrastructure/**  ← MongoDB документи, репозиторії (реалізації), маппери
- **presentation/**    ← REST контролери, DTO, маппери

Дотримання архітектурних правил перевіряється автоматично через **ArchUnit** тести.

---

## ✨ Функціональність

### 👤 Користувач
- Реєстрація та вхід з JWT токеном
- Скидання пароля через email
- Редагування профілю (ім'я, вага, зріст, вік, ціль, рівень, обладнання)
- Відображення BMI, прогресу до цільової ваги

### 🏋️ Плани тренувань
- **Автоматична генерація** персоналізованого плану на основі профілю
- 5 типів планів: Гіпертрофія (8 тижнів), Сила (10 тижнів), Сила+Маса (9 тижнів), Спалення жиру (8 тижнів), Витривалість (8 тижнів)
- Підтримка кількох планів одночасно
- Детальний перегляд по тижнях і днях

### 📋 Журналювання тренувань (Live Mode)
- Режим реального часу з таймером
- Введення фактичної ваги та повторень для кожної вправи
- Рекомендації ваги на основі попередніх результатів
- Можливість пропустити вправу
- Фіксація **особистих рекордів (PR)** автоматично

### 📈 Прогрес
- Графіки прогресу ваги по кожній вправі (SVG)
- Динаміка ваги тіла
- Таблиця особистих рекордів
- Статистика стріків (consecutivних днів)

### 🔐 Адмін-панель
- Перегляд всіх користувачів з пагінацією
- Управління ролями (USER / ADMIN / OWNER)
- Перегляд профілю та планів кожного користувача
- Видалення користувачів і планів
- **Журнал аудиту** всіх дій у системі з фільтрацією

### 🏅 База вправ
- 60+ вправ з детальними описами
- Фільтрація за групою м'язів, складністю, обладнанням
- Пошук по назві
- Додавання YouTube відео до вправ (адмін)
- CRUD операції для адміністраторів

---

## 🔑 Ролі та доступ

| Роль    | Можливості |
|---------|-----------|
| `USER`  | Профіль, плани, тренування, прогрес, вправи |
| `ADMIN` | Все що USER + адмін-панель (перегляд юзерів, управління вправами) |
| `OWNER` | Все що ADMIN + зміна ролей, видалення юзерів, журнал аудиту |

---

## 🚀 Запуск проєкту

### Вимоги
- **Java 17+**
- **Node.js 20+** + **npm 11+**
- **MongoDB** (локально на порту `27017`)

### Backend
```bash
cd workout-app
./mvnw spring-boot:run
```
Сервер запуститься на `http://localhost:8080`

### Frontend
```bash
cd workout-frontend
npm install
npm start
```
Застосунок буде доступний на `http://localhost:4200`

---

## ⚙️ Конфігурація

Файл `workout-app/src/main/resources/application.yml`:

```yaml
spring:
  mongodb:
    uri: mongodb://localhost:27017/workout_db
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password

application:
  security:
    jwt:
      secret-key: your-secret-key
      expiration: 86400000

app:
  frontend-url: http://localhost:4200
```

---

## 🧪 Тестування

### Backend (Unit тести)
```bash
cd workout-app
./mvnw test
```

Покриття включає:
- `RegisterUserUseCase`, `LoginUserUseCase`
- `GetUserUseCase`, `UpdateUserUseCase`, `GetUserProfileUseCase`, `SaveUserProfileUseCase`
- `GenerateWorkoutPlanUseCase`, `GetUserPlansUseCase`, `DeleteWorkoutPlanUseCase`
- `GetExercisesUseCase`, `ManageExerciseUseCase`
- `GetStatsUseCase`
- **Архітектурні тести** (ArchUnit) — перевірка залежностей між шарами
- **Інтеграційні тести стратегій** — всі 5 планів для різних рівнів

### Frontend
```bash
cd workout-frontend
npm test
```

---

## 📁 Структура проєкту

```
workout-app/                          ← Spring Boot backend
│
├── domain/
│   ├── entity/                       ← User, UserProfile, WorkoutPlan, Exercise, logs...
│   ├── repository/                   ← Інтерфейси репозиторіїв
│   ├── enums/                        ← Role, PlanType, MuscleGroup, Difficulty...
│   └── exception/                    ← DomainException і підкласи
│
├── application/
│   ├── usecase/                      ← Бізнес-логіка (один клас = одна дія)
│   ├── strategy/                     ← Генерація планів (5 стратегій)
│   ├── service/                      ← AuditService, EmailService, CurrentUserService
│   └── command/                      ← Command objects для use cases
│
├── infrastructure/
│   ├── document/                     ← MongoDB документи
│   ├── repository/                   ← Spring Data MongoDB репозиторії
│   ├── repoImplement/                ← Реалізації domain-репозиторіїв
│   ├── mapper/                       ← MapStruct маппери
│   └── security/                     ← JwtService, JwtFilter, BCryptPasswordHasher
│
└── presentation/
    ├── controller/                   ← REST контролери
    ├── dto/                          ← Request/Response DTO
    └── mapper/                       ← Presentation маппери

workout-frontend/                     ← Angular 21 frontend
│
└── src/app/
    ├── core/
    │   ├── services/                 ← AuthService, WorkoutLogService, UserProfileService
    │   ├── guards/                   ← authGuard
    │   ├── interceptors/             ← jwtInterceptor
    │   └── layout/                   ← NavbarComponent
    │
    └── features/
        ├── auth/                     ← Login, Register, ForgotPassword, ResetPassword
        ├── workout-plan/             ← Плани тренувань + Live Mode
        ├── exercises/                ← База вправ
        ├── progress/                 ← Графіки прогресу
        ├── profile/                  ← Профіль користувача
        └── admin/                    ← Адмін-панель + Журнал аудиту
```

## 🎨 UI/UX

- **Світла та темна теми** (перемикаються в navbar, зберігаються в localStorage)
- CSS Variables для консистентної кольорової схеми
- Анімації та hover-ефекти
- Інтерфейс повністю **українською мовою**
