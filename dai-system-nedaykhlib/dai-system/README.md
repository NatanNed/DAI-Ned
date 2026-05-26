# GUI-додаток для інформаційної системи підтримки діяльності ДАІ

## Автор / учасник проєкту

Недайхліб Натан Сергійович

## Тема проєкту

GUI-додаток для роботи з інформаційною системою підтримки діяльності ДАІ.
Система містить інформацію про транспортні засоби, водіїв, власників, номерні знаки, довіреності та штрафи.

## Обрана таблиця для CRUD

Основна таблиця для роботи через графічний інтерфейс — `fines`.

Додаток реалізує:

- додавання штрафу;
- перегляд списку штрафів;
- пошук за типом порушення;
- оновлення запису;
- видалення запису;
- повідомлення про помилки введення.

## Використані технології

- Java 26;
- JavaFX;
- JDBC;
- PostgreSQL;
- Maven;
- MVC;
- PreparedStatement;
- JavaDoc.

## Архітектура

Проєкт побудовано за архітектурою MVC:

- `model` — класи предметної області;
- `view` — FXML-інтерфейс JavaFX;
- `controller` — обробка подій графічного інтерфейсу;
- `service` — робота з базою даних через JDBC;
- `util` — допоміжні класи.

## Структура проєкту

```text
src/main/java/org/example/dai
 ├── MainApp.java
 ├── controller/FineController.java
 ├── model/Fine.java
 ├── service/DatabaseConnection.java
 ├── service/FineService.java
 └── util/AlertUtil.java

src/main/resources
 ├── db.properties
 └── fines-view.fxml
```

## Налаштування бази даних

1. Створити БД PostgreSQL з назвою `dai_db`.
2. Виконати SQL-скрипт з файлу `database.sql`.
3. Перевірити файл `src/main/resources/db.properties`.

```properties
url=jdbc:postgresql://localhost:5432/dai_db
user=postgres
password=1234
```

## Запуск

```bash
mvn clean javafx:run
```

## Правила Git

- основна гілка: `main`;
- зміни виконуються в окремих гілках;
- злиття змін через Pull Request;
- назви комітів мають бути зрозумілими.

## Приклади комітів

- `init maven project`
- `add database connection`
- `create fine model`
- `implement crud operations`
- `add javafx interface`
- `add search functionality`
