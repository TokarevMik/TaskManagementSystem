Task Management System

Описание

Task Management System — это RESTful API для управления задачами, пользователями и комментариями. 
Поддерживает аутентификацию через JWT, ролевую модель (администратор и пользователь).

1. Клонирование репозитория

git clone https://github.com/TokarevMik/TaskManagementSystem.git
cd TaskManagementSystem

2. Сборка и запуск через Docker Compose

version: "3.9"

services:
  mysql_db:
    image: mysql:latest
    restart: always
    environment:
      - MYSQL_DATABASE=TASK_DB
      - MYSQL_USER=ROOT
      - MYSQL_PASSWORD=ROOT
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
  redis:
    image: redis:7.4
    ports:
      - "6379:6379"

docker-compose up -d

3. Запуск приложения

./mvnw spring-boot:run

Использование API

После запуска API будет доступно по адресу: http://localhost:8080
Документация OpenAPI: http://localhost:8080/swagger-ui.html



