# 🏦 Bank API — REST API для интернет-банка

**Bank API** — серверная часть банковской системы, реализующая основные операции с денежными средствами через REST API.

---

## 📋 Функциональность

| Метод | URL | Описание |
|-------|-----|----------|
| **GET** | `/api/bank/balance?userId=1` | Узнать баланс пользователя |
| **POST** | `/api/bank/put` | Пополнение счёта |
| **POST** | `/api/bank/take` | Снятие денег |
| **POST** | `/api/bank/transfer` | Перевод между пользователями |
| **GET** | `/api/bank/operations?userId=1` | История операций (с фильтром по датам) |

---

## 🛠️ Стек технологий

- **Java 17**
- **Spring Boot** (Web, JDBC)
- **MySQL**
- **Maven**
- **REST API / JSON**
- **Git**

---

## 🗄️ Структура базы данных

### Таблица `users`
| Поле | Тип | Описание |
|------|-----|----------|
| id | BIGINT PK | ID пользователя |
| balance | DECIMAL(15,2) | Текущий баланс |

### Таблица `transactions`
| Поле | Тип | Описание |
|------|-----|----------|
| id | BIGINT PK | ID операции |
| user_id | BIGINT FK | ID пользователя |
| operation_type | VARCHAR(50) | Тип: PUT, TAKE, TRANSFER_OUT, TRANSFER_IN |
| amount | DECIMAL(15,2) | Сумма операции |
| operation_date | TIMESTAMP | Дата и время операции |
| related_user_id | BIGINT | ID второго участника (для переводов) |

---

## 🚀 Быстрый старт

### 1. Создать базу данных
```sql
CREATE DATABASE bank;
USE bank;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00
);

INSERT INTO users (balance) VALUES (1000.00), (2500.50), (500.75);

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    operation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    related_user_id BIGINT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (related_user_id) REFERENCES users(id)
);
2. Настроить подключение
Отредактировать src/main/resources/application.properties:

properties
spring.datasource.url=jdbc:mysql://localhost:3306/bank
spring.datasource.username=root
spring.datasource.password=your_password

3. Запустить приложение
bash
mvn spring-boot:run
Приложение запустится на http://localhost:8080

🧪 Примеры запросов
Получить баланс
text
GET http://localhost:8080/api/bank/balance?userId=1
Пополнить счёт
text
POST http://localhost:8080/api/bank/put
Content-Type: application/json

{ "userId": 1, "amount": 500.00 }
Снять деньги
text
POST http://localhost:8080/api/bank/take
Content-Type: application/json

{ "userId": 1, "amount": 200.00 }
Перевести деньги
text
POST http://localhost:8080/api/bank/transfer
Content-Type: application/json

{ "fromUserId": 1, "toUserId": 2, "amount": 300.00 }
История операций
text
GET http://localhost:8080/api/bank/operations?userId=1
GET http://localhost:8080/api/bank/operations?userId=1&startDate=2026-01-01&endDate=2026-12-31
⭐ Особенности реализации
Транзакции: все операции с балансом и историей выполняются атомарно (@Transactional)

Гибкая фильтрация: история операций поддерживает фильтр по датам (обе даты, одна или без дат)

Валидация: проверка входных данных (ID, сумма, достаточно средств)

Настройки: параметры подключения к БД вынесены в отдельный файл

👤 Автор
Badamshina Rina

GitHub

🏦 Bank API — REST API for Internet Banking (EN)
Bank API — backend for a banking system implementing core financial operations via REST API.

📋 Features
Method	URL	Description
GET	/api/bank/balance?userId=1	Get user balance
POST	/api/bank/put	Deposit money
POST	/api/bank/take	Withdraw money
POST	/api/bank/transfer	Transfer between users
GET	/api/bank/operations?userId=1	Transaction history (with date filter)
🛠️ Tech Stack
Java 17

Spring Boot (Web, JDBC)

MySQL

Maven

REST API / JSON

Git

⭐ Key Features
Transactions: all balance and history operations are atomic (@Transactional)

Flexible filtering: transaction history supports date range filtering

Validation: input data validation (ID, amount, sufficient funds)

Configuration: DB connection settings in separate application.properties file

👤 Author
Badamshina Rina

GitHub

text

---

