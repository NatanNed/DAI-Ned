package org.example.dai.service;

import org.example.dai.model.Fine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервісний клас для виконання CRUD-операцій з таблицею fines.
 */
public class FineService {

    /**
     * Додає новий штраф до бази даних.
     *
     * @param fine об'єкт штрафу
     */
    public void addFine(Fine fine) {
        String sql = """
                INSERT INTO fines
                (vehicle_id, driver_id, fine_date, amount, violation_type, is_paid, is_accident)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, fine.getVehicleId());
            statement.setInt(2, fine.getDriverId());
            statement.setDate(3, java.sql.Date.valueOf(fine.getFineDate()));
            statement.setDouble(4, fine.getAmount());
            statement.setString(5, fine.getViolationType());
            statement.setBoolean(6, fine.isPaid());
            statement.setBoolean(7, fine.isAccident());

            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Помилка додавання штрафу", e);
        }
    }

    /**
     * Повертає всі штрафи з бази даних.
     *
     * @return список штрафів
     */
    public List<Fine> getAllFines() {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fines ORDER BY fine_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                fines.add(mapResultSetToFine(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException("Помилка отримання списку штрафів", e);
        }

        return fines;
    }

    /**
     * Оновлює дані про штраф.
     *
     * @param fine оновлений об'єкт штрафу
     */
    public void updateFine(Fine fine) {
        String sql = """
                UPDATE fines
                SET vehicle_id = ?, driver_id = ?, fine_date = ?, amount = ?,
                    violation_type = ?, is_paid = ?, is_accident = ?
                WHERE fine_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, fine.getVehicleId());
            statement.setInt(2, fine.getDriverId());
            statement.setDate(3, java.sql.Date.valueOf(fine.getFineDate()));
            statement.setDouble(4, fine.getAmount());
            statement.setString(5, fine.getViolationType());
            statement.setBoolean(6, fine.isPaid());
            statement.setBoolean(7, fine.isAccident());
            statement.setInt(8, fine.getFineId());

            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Помилка оновлення штрафу", e);
        }
    }

    /**
     * Видаляє штраф за ідентифікатором.
     *
     * @param fineId ідентифікатор штрафу
     */
    public void deleteFine(int fineId) {
        String sql = "DELETE FROM fines WHERE fine_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, fineId);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Помилка видалення штрафу", e);
        }
    }

    /**
     * Шукає штрафи за типом порушення.
     *
     * @param text текст для пошуку
     * @return список знайдених штрафів
     */
    public List<Fine> searchByViolation(String text) {
        List<Fine> fines = new ArrayList<>();
        String sql = """
                SELECT *
                FROM fines
                WHERE LOWER(violation_type) LIKE LOWER(?)
                ORDER BY fine_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + text + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    fines.add(mapResultSetToFine(resultSet));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Помилка пошуку штрафів", e);
        }

        return fines;
    }

    private Fine mapResultSetToFine(ResultSet resultSet) throws Exception {
        Fine fine = new Fine();
        fine.setFineId(resultSet.getInt("fine_id"));
        fine.setVehicleId(resultSet.getInt("vehicle_id"));
        fine.setDriverId(resultSet.getInt("driver_id"));
        fine.setFineDate(resultSet.getDate("fine_date").toString());
        fine.setAmount(resultSet.getDouble("amount"));
        fine.setViolationType(resultSet.getString("violation_type"));
        fine.setPaid(resultSet.getBoolean("is_paid"));
        fine.setAccident(resultSet.getBoolean("is_accident"));
        return fine;
    }
}
