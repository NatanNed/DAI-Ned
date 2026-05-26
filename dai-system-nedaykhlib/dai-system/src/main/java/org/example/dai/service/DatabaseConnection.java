package org.example.dai.service;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Клас для створення підключення до бази даних PostgreSQL.
 */
public class DatabaseConnection {

    /**
     * Повертає нове підключення до бази даних на основі db.properties.
     *
     * @return об'єкт Connection
     */
    public static Connection getConnection() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = DatabaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            if (inputStream == null) {
                throw new RuntimeException("Файл db.properties не знайдено");
            }

            properties.load(inputStream);

            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException("Помилка підключення до бази даних", e);
        }
    }
}
