# ✈️ AeroReserve - Система управления бронированием авиабилетов

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![Stripe](https://img.shields.io/badge/Stripe-Payments-purple)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Templates-green)
![Flyway](https://img.shields.io/badge/Flyway-Migrations-red)
![MinIO](https://img.shields.io/badge/MinIO-Storage-yellow)

## 📖 О проекте

**AeroReserve** - это полнофункциональная веб-платформа для бронирования авиабилетов,
### 🎯 Ключевые преимущества

- **🚀 Высокая производительность**
- **🔐 Безопасность**
- **📱 Адаптивность** 
- **🛠️ Масштабируемость**
- **💾 Надежность** 


## 🚀 Возможности

### ✨ Основной функционал
- **🔍 Умный поиск рейсов** - Фильтрация по направлениям, датам и авиакомпаниям
- **💺 Выбор мест** - Интерактивная карта мест с отображением доступности
- **💳 Безопасные платежи** - Полная интеграция с Stripe (карты, Google/Apple Pay)
- **📧 Email уведомления** - Автоматические уведомления о статусе бронирования
- **🎫 Электронные билеты** - Генерация PDF с QR-кодами для посадки

### 🛠️ Административная панель
- **✈️ Управление рейсами** - Полный CRUD для расписания и тарифов
- **👥 Управление пользователями** - Модерация и ролевое управление
- **📈 Статистика**
- **💾 Резервное копирование** - Автоматические бэкапы в MinIO

## 🛠️ Технологический стек

### Backend
- **Java 17** - Основной язык программирования
- **Spring Boot 3.1** - Фреймворк для enterprise приложений
- **Spring Security** - Аутентификация и авторизация
- **Spring Data JPA** - Работа с базой данных
- **Hibernate** - ORM mapping

### Frontend
- **Thymeleaf** - Server-side шаблонизатор
- **Bootstrap 5** - CSS фреймворк
- **JavaScript** - Клиентская логика
- **Chart.js** - Визуализация статистики

### База данных
- **PostgreSQL 15** - Основная реляционная БД
- **Flyway** - Миграции и управление схемой
- **HikariCP** - Connection pooling

### Платежи и интеграции
- **Stripe API** - Обработка платежей
- **SMTP/Gmail** - Email рассылки
- **MinIO** - Object storage для бэкапов

### Инфраструктура
- **Docker** - Контейнеризация
- **Docker Compose** - Оркестрация сервисов
- **Maven** - Сборка проекта

# 🛠️ Архитектура проекта

### System Design
![sd (2).png](about/sd%20%282%29.png)

### ER в нотации Чена
![Er Чена](about/Er%20%D1%87%D0%B5%D0%BD%D0%B0.drawio.png)

### ER в нотации Crow’s Foot
![Crows foot.png](about/Crows%20foot.png)


## 📸 Скрины проекта
![1.png](about/1.png)

![2.png](about/2.png)

![3.png](about/3.png)

![4.png](about/4.png)

![5.png](about/5.png)

![6.png](about/6.png)

![7.png](about/7.png)

![8.png](about/8.png)

![9.png](about/9.png)