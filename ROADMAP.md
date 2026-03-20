# Дорожная карта разработки — Club Management System

## Этап 0: Фундамент (Гурьянов Н.)

**Статус**: Выполнен  
**Ветка**: `dev`

### Что реализовано:

**Backend:**
- Модуль User: регистрация, аутентификация (JWT), профиль, смена пароля
- Модуль Club: CRUD клубов, категории, мои клубы
- Модуль Event: CRUD мероприятий
- Модуль EventRegistration: запись на мероприятия, отмена, лист ожидания, отметка посещения
- Все entity, enums и repositories для всех сущностей (включая MembershipApplication, ClubMembership, Notification)
- Spring Security + JWT (stateless)
- Swagger UI

**Frontend (Vue 3 + PrimeVue + Tailwind):**
- Аутентификация (логин, регистрация)
- Layout (header, sidebar, навигация)
- Страницы клубов (список, детали, создание, мои клубы)
- Страницы мероприятий (список, детали, создание, мои мероприятия)
- Регистрация на мероприятия (кнопка записи, список участников)
- Профиль пользователя (редактирование, смена пароля)

**Инфраструктура:**
- Docker Compose (postgres, server, client)
- CORS конфигурация

---

## Задача 1: Backend — Управление членством в клубах

**Исполнитель**: ____________  
**Ветка**: `feature/membership-backend`  
**Статус**: Не начата

### Описание:
Реализация backend-части для подачи заявок на вступление в клуб, обработки заявок администратором и управления участниками.

### Что нужно реализовать:

**Сервисный слой** (`service/src/main/java/.../service/business/`):
- `MembershipApplicationService` / `MembershipApplicationServiceImpl`
- `ClubMembershipService` / `ClubMembershipServiceImpl`

**DTO** (`application/src/main/java/.../endpoint/dto/`):
- `ApplicationCreateRequest` — comment (String)
- `ApplicationReviewRequest` — status (String: APPROVED/REJECTED), comment (String)
- `ApplicationResponse` — id, clubId, clubName, userId, userFullName, appliedAt, status, reviewedBy, reviewedAt, comment
- `MembershipResponse` — id, clubId, userId, userFullName, joinedAt, memberRole, status
- `MemberRoleUpdateRequest` — role (String: MEMBER/MODERATOR/ADMIN)

**Mappers** (`application/src/main/java/.../mapper/`):
- `MembershipApplicationMapper` (MapStruct)
- `ClubMembershipMapper` (MapStruct)

**Controllers** (`application/src/main/java/.../endpoint/`):
- `MembershipApplicationController`:
  - `POST /api/v1/clubs/{clubId}/applications` — подать заявку
  - `GET /api/v1/clubs/{clubId}/applications` — список заявок (для админа клуба)
  - `PUT /api/v1/applications/{id}/review` — одобрить/отклонить заявку
- `ClubMembershipController`:
  - `GET /api/v1/clubs/{clubId}/members` — список участников клуба
  - `PUT /api/v1/memberships/{id}/role` — изменить роль участника
  - `DELETE /api/v1/memberships/{id}` — исключить участника
  - `POST /api/v1/clubs/{clubId}/leave` — выйти из клуба

**Бизнес-логика:**
- При одобрении заявки автоматически создавать запись в `ClubMembership`
- При отклонении — обновить статус заявки
- Нельзя подать заявку повторно (проверка дубликатов)

**Зависимости**: Entity `MembershipApplication`, `ClubMembership` и репозитории уже есть в Этапе 0.

### Чек-лист:
- [ ] ClubMembershipService + ClubMembershipServiceImpl
- [ ] MembershipApplicationService + MembershipApplicationServiceImpl
- [ ] DTO: ApplicationCreateRequest, ApplicationReviewRequest, ApplicationResponse
- [ ] DTO: MembershipResponse, MemberRoleUpdateRequest
- [ ] MembershipApplicationMapper
- [ ] ClubMembershipMapper
- [ ] MembershipApplicationController (3 эндпоинта)
- [ ] ClubMembershipController (4 эндпоинта)
- [ ] Проверка через Swagger

---

## Задача 2: Frontend — Управление членством в клубах

**Исполнитель**: ____________  
**Ветка**: `feature/membership-frontend`  
**Статус**: Не начата

### Описание:
Frontend-часть для подачи заявки на вступление, панели обработки заявок и управления участниками клуба.

### Что нужно реализовать:

**API модуль** (`src/api/membership.ts`):
- `apply(clubId, data)` — POST /api/v1/clubs/{clubId}/applications
- `getApplications(clubId)` — GET /api/v1/clubs/{clubId}/applications
- `reviewApplication(id, data)` — PUT /api/v1/applications/{id}/review
- `getMembers(clubId)` — GET /api/v1/clubs/{clubId}/members
- `updateRole(id, data)` — PUT /api/v1/memberships/{id}/role
- `removeMember(id)` — DELETE /api/v1/memberships/{id}
- `leaveClub(clubId)` — POST /api/v1/clubs/{clubId}/leave

**Pinia store** (`src/stores/membership.ts`):
- applications, members, loading
- actions для всех API-вызовов

**Страницы** (`src/pages/`):
- `ClubApplicationPage.vue` — форма подачи заявки с комментарием
- `ClubApplicationsListPage.vue` — список заявок для админа (одобрить/отклонить)
- `ClubMembersPage.vue` — список участников с управлением ролями

**Компоненты** (`src/components/`):
- `ApplicationCard.vue` — карточка заявки (имя, дата, статус, кнопки)
- `MemberCard.vue` — карточка участника (имя, роль, кнопки)

**Роутер** — добавить маршруты:
- `/clubs/:id/apply` -> ClubApplicationPage
- `/clubs/:id/applications` -> ClubApplicationsListPage
- `/clubs/:id/members` -> ClubMembersPage

**Интеграция**:
- На `ClubDetailPage.vue` добавить кнопку "Подать заявку" (если пользователь не участник)
- На `ClubDetailPage.vue` добавить ссылки "Заявки" и "Участники" (если пользователь — владелец)

### Чек-лист:
- [ ] src/api/membership.ts
- [ ] src/stores/membership.ts
- [ ] ClubApplicationPage.vue
- [ ] ClubApplicationsListPage.vue
- [ ] ClubMembersPage.vue
- [ ] ApplicationCard.vue
- [ ] MemberCard.vue
- [ ] Маршруты в router/index.ts
- [ ] Интеграция с ClubDetailPage.vue

---

## Задача 3: Backend — Система уведомлений

**Исполнитель**: ____________  
**Ветка**: `feature/notifications-backend`  
**Статус**: Не начата

### Описание:
Backend для in-app уведомлений: CRUD, отметка прочитанности, автоматическая генерация при ключевых событиях.

### Что нужно реализовать:

**Сервисный слой**:
- `NotificationService` / `NotificationServiceImpl`
  - Методы: findByUserId, countUnread, markAsRead, markAllAsRead, createNotification

**DTO**:
- `NotificationResponse` — id, userId, eventId, clubId, subject, messageText, channel, scheduledAt, sentAt, deliveryStatus, isRead
- `NotificationCreateRequest` — userId, eventId, clubId, subject, messageText, channel

**Mapper**:
- `NotificationMapper` (MapStruct)

**Controller** — `NotificationController`:
- `GET /api/v1/notifications` — список уведомлений текущего пользователя (пагинация)
- `GET /api/v1/notifications/unread-count` — количество непрочитанных
- `PUT /api/v1/notifications/{id}/read` — отметить как прочитанное
- `PUT /api/v1/notifications/read-all` — отметить все как прочитанные

**Автоматическая генерация** (метод createNotification):
- При создании мероприятия — уведомить всех участников клуба
- При отмене мероприятия — уведомить всех зарегистрированных
- При изменении статуса заявки — уведомить заявителя

**Зависимости**: Entity `Notification` и `NotificationRepository` уже есть в Этапе 0.

### Чек-лист:
- [ ] NotificationService + NotificationServiceImpl
- [ ] NotificationResponse, NotificationCreateRequest
- [ ] NotificationMapper
- [ ] NotificationController (4 эндпоинта)
- [ ] Логика автоматической генерации уведомлений
- [ ] Проверка через Swagger

---

## Задача 4: Frontend — Система уведомлений

**Исполнитель**: ____________  
**Ветка**: `feature/notifications-frontend`  
**Статус**: Не начата

### Описание:
Frontend для отображения уведомлений: колокольчик с badge, выпадающий список, страница всех уведомлений.

### Что нужно реализовать:

**API модуль** (`src/api/notifications.ts`):
- `getAll()` — GET /api/v1/notifications
- `getUnreadCount()` — GET /api/v1/notifications/unread-count
- `markAsRead(id)` — PUT /api/v1/notifications/{id}/read
- `markAllAsRead()` — PUT /api/v1/notifications/read-all

**Pinia store** (`src/stores/notification.ts`):
- notifications, unreadCount, loading
- fetchNotifications, fetchUnreadCount, markAsRead, markAllAsRead
- Поллинг каждые 30 секунд

**Компоненты**:
- `NotificationBell.vue` — иконка колокольчика в header с badge (Badge от PrimeVue)
- `NotificationDropdown.vue` — выпадающий список последних 5 уведомлений

**Страницы**:
- `NotificationsPage.vue` — полный список уведомлений с пагинацией

**Роутер**: добавить `/notifications` -> NotificationsPage

**Интеграция**: добавить NotificationBell в AppHeader.vue

### Чек-лист:
- [ ] src/api/notifications.ts
- [ ] src/stores/notification.ts
- [ ] NotificationBell.vue
- [ ] NotificationDropdown.vue
- [ ] NotificationsPage.vue
- [ ] Маршрут /notifications
- [ ] Интеграция с AppHeader.vue
- [ ] Поллинг unread count

---

## Git Workflow

1. Этап 0 коммитится в `dev`
2. Каждая задача (1-4) — отдельная ветка `feature/*` от `dev`
3. По завершении задачи — Pull Request в `dev`
4. Финальный PR из `dev` в `main`

### Формат коммитов:
- `feat: описание` — новая функциональность
- `fix: описание` — исправление бага
- `refactor: описание` — рефакторинг

### Pull Request должен содержать:
- Краткое описание реализованной функциональности
- Ссылку на соответствующее требование из SRS
- Список изменённых файлов
