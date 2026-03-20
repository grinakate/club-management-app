# Club Management App (MVP)

Клиент-серверное приложение для автоматизации деятельности клубов по интересам

## Стек
- server: Java 25, Spring Boot 4, Maven
- client: Vue 3, Vite, PrimeVue, Tailwind CSS
- database: PostgreSQL 16

## Структура репозитория

```
main/
├── client/         # Исходный код фронтенд-приложения (Vue 3 + TypeScript)
├── database/       # Скрипты для базы данных
└── server/         # Исходный код бэкенд-приложения (Spring Boot)
```

## Как запустить проект
Из корня проекта выполнить команду:
```shell
docker compose up -d
```
Приложения доступны по адресам:
- database: http://localhost:5432
- client: http://localhost:3000
- server: http://localhost:8080
- swagger: http://localhost:8080/swagger-ui/index.html

## API

### Модуль авторизации/регистрации
- `POST /api/auth/register` - регистрация нового пользователя
- `POST /api/auth/login` - логирование, получение JWT токена

### Модуль пользователей
- `GET /api/users` - получение списка пользователей
- `GET /api/users/{id}` - получение пользователя по ИД
- `PUT /api/users/{id}/profile` - редактирование пользователя

### Модуль клубов
- `GET /api/v1/clubs` - получение списка клубов
- `GET /api/v1/clubs/{id}` - получение клуба по ИД
- `POST /api/v1/clubs` - создание клуба
- `PUT /api/v1/clubs/{id}` - обновление клуба
- `DELETE /api/v1/clubs/{id}` - удаление клуба

### Модуль мероприятий
- `GET /api/v1/events` - получение списка мероприятий
- `GET /api/v1/events/{id}` - получение мероприятия по ИД
- `POST /api/v1/events` - создание мероприятия
- `PUT /api/v1/events/{id}` - обновление мероприятия
- `DELETE /api/v1/events/{id}` - удаление мероприятия

### Модуль регистраций на мероприятия
- `POST /api/v1/events/{eventId}/registrations` - регистрация на мероприятие
- `GET /api/v1/events/{eventId}/registrations` - получение списка регистраций
- `DELETE /api/v1/events/{eventId}/registrations/{id}` - отмена регистрации

### Модуль категорий
- `GET /api/v1/categories` - получение списка категорий

## Авторы
- Зорина Яна
- Борисов Игнат
- Гурьянов Никита
- Гусева Екатерина
- Денисов Илья
- Ишанова Надежда
