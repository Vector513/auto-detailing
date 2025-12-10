# Auto Detailing — Backend/Frontend Setup

## 1) База данных PostgreSQL

Создать роль и базу (под суперпользователем `postgres`):
```sql
CREATE ROLE autodetailer WITH LOGIN PASSWORD 'your_password';
CREATE DATABASE autodetailing OWNER autodetailer;
GRANT ALL ON SCHEMA public TO autodetailer;
ALTER SCHEMA public OWNER TO autodetailer;
```

Проверка подключения (PowerShell пример):
```powershell
$env:PGPASSWORD="your_password"
psql -h 127.0.0.1 -p 5432 -U autodetailer -d autodetailing -c "SELECT 1;"
```

## 2) Переменные окружения для приложения

Минимум нужно выставить перед запуском бэкенда:
```
DATABASE_URL=jdbc:postgresql://127.0.0.1:5432/autodetailing
DATABASE_USER=autodetailer
DATABASE_PASSWORD=your_password
```

Опционально:
- `DB_AUTO_MIGRATE` — по умолчанию `true`. Если `false`, автоматическое создание таблиц отключено.
- `DB_AUTO_SEED` — по умолчанию `true`. Если `false`, авто-заполнение 8 услуг отключено.

Сиды происходят один раз, если таблица `services` пуста.

## 3) Запуск backend (Ktor)

Из каталога `backend`:
```powershell
# Windows PowerShell пример
$env:DATABASE_URL="jdbc:postgresql://127.0.0.1:5432/autodetailing"
$env:DATABASE_USER="autodetailer"
$env:DATABASE_PASSWORD="your_password"
$env:DB_AUTO_MIGRATE="true"
$env:DB_AUTO_SEED="true"
./gradlew run
```

Сервер поднимается на `http://localhost:8080`. API префикс `/api` (например, `GET /api/services`).

## 4) Запуск frontend (Vite)

Из каталога `frontend`:
```bash
npm install
npm run dev
```

Фронт по умолчанию на `http://localhost:5173`. При запросах к бэкенду указывайте `http://localhost:8080/api/...` или настройте прокси в Vite config при необходимости.

## 5) Структура сидов (для справки)

При включённом `DB_AUTO_SEED` в таблицу `services` добавляются 8 услуг со схемой:
- `id` (serial, PK)
- `title`, `image_url`, `duration`, `short_desc`
- `bullet_points` (TEXT с JSON-массивом строк)
- `price_from` (INT)

