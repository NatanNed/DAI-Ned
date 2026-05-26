package org.example.dai.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.dai.model.Fine;
import org.example.dai.service.FineService;
import org.example.dai.util.AlertUtil;

/**
 * Контролер головного вікна для роботи з таблицею штрафів.
 */
public class FineController {

    @FXML private TableView<Fine> finesTable;
    @FXML private TableColumn<Fine, Number> idColumn;
    @FXML private TableColumn<Fine, Number> vehicleColumn;
    @FXML private TableColumn<Fine, Number> driverColumn;
    @FXML private TableColumn<Fine, String> dateColumn;
    @FXML private TableColumn<Fine, Number> amountColumn;
    @FXML private TableColumn<Fine, String> violationColumn;
    @FXML private TableColumn<Fine, String> paidColumn;
    @FXML private TableColumn<Fine, String> accidentColumn;

    @FXML private TextField vehicleField;
    @FXML private TextField driverField;
    @FXML private TextField dateField;
    @FXML private TextField amountField;
    @FXML private TextField violationField;
    @FXML private CheckBox paidCheck;
    @FXML private CheckBox accidentCheck;
    @FXML private TextField searchField;

    private final FineService fineService = new FineService();
    private final ObservableList<Fine> fineList = FXCollections.observableArrayList();

    /**
     * Ініціалізує таблицю, колонки та завантажує дані.
     */
    @FXML
    public void initialize() {
        configureColumns();
        loadData();
        configureTableSelection();
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getFineId()));
        vehicleColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getVehicleId()));
        driverColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDriverId()));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFineDate()));
        amountColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAmount()));
        violationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getViolationType()));
        paidColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isPaid() ? "Так" : "Ні"));
        accidentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isAccident() ? "Так" : "Ні"));
    }

    private void configureTableSelection() {
        finesTable.setOnMouseClicked(event -> {
            Fine fine = finesTable.getSelectionModel().getSelectedItem();
            if (fine != null) {
                vehicleField.setText(String.valueOf(fine.getVehicleId()));
                driverField.setText(String.valueOf(fine.getDriverId()));
                dateField.setText(fine.getFineDate());
                amountField.setText(String.valueOf(fine.getAmount()));
                violationField.setText(fine.getViolationType());
                paidCheck.setSelected(fine.isPaid());
                accidentCheck.setSelected(fine.isAccident());
            }
        });
    }

    private void loadData() {
        try {
            fineList.clear();
            fineList.addAll(fineService.getAllFines());
            finesTable.setItems(fineList);
        } catch (Exception e) {
            AlertUtil.showError(e.getMessage());
        }
    }

    /**
     * Додає новий штраф.
     */
    @FXML
    private void addFine() {
        try {
            Fine fine = readFineFromFields();
            fineService.addFine(fine);
            loadData();
            clearFields();
            AlertUtil.showInfo("Штраф успішно додано");
        } catch (Exception e) {
            AlertUtil.showError("Некоректні дані. Перевірте поля введення.");
        }
    }

    /**
     * Оновлює вибраний штраф.
     */
    @FXML
    private void updateFine() {
        Fine selected = finesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showError("Оберіть запис для оновлення");
            return;
        }

        try {
            Fine updatedFine = readFineFromFields();
            updatedFine.setFineId(selected.getFineId());
            fineService.updateFine(updatedFine);
            loadData();
            clearFields();
            AlertUtil.showInfo("Штраф успішно оновлено");
        } catch (Exception e) {
            AlertUtil.showError("Помилка оновлення. Перевірте введені дані.");
        }
    }

    /**
     * Видаляє вибраний штраф.
     */
    @FXML
    private void deleteFine() {
        Fine selected = finesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showError("Оберіть запис для видалення");
            return;
        }

        try {
            fineService.deleteFine(selected.getFineId());
            loadData();
            clearFields();
            AlertUtil.showInfo("Штраф успішно видалено");
        } catch (Exception e) {
            AlertUtil.showError(e.getMessage());
        }
    }

    /**
     * Виконує пошук за типом порушення.
     */
    @FXML
    private void searchFine() {
        try {
            fineList.clear();
            fineList.addAll(fineService.searchByViolation(searchField.getText()));
            if (fineList.isEmpty()) {
                AlertUtil.showInfo("Нічого не знайдено");
            }
        } catch (Exception e) {
            AlertUtil.showError(e.getMessage());
        }
    }

    /**
     * Оновлює таблицю та показує всі записи.
     */
    @FXML
    private void refreshData() {
        loadData();
        searchField.clear();
    }

    /**
     * Очищує поля введення.
     */
    @FXML
    private void clearFields() {
        vehicleField.clear();
        driverField.clear();
        dateField.clear();
        amountField.clear();
        violationField.clear();
        paidCheck.setSelected(false);
        accidentCheck.setSelected(false);
        finesTable.getSelectionModel().clearSelection();
    }

    private Fine readFineFromFields() {
        String vehicleText = vehicleField.getText().trim();
        String driverText = driverField.getText().trim();
        String dateText = dateField.getText().trim();
        String amountText = amountField.getText().trim();
        String violationText = violationField.getText().trim();

        if (vehicleText.isEmpty() || driverText.isEmpty() || dateText.isEmpty()
                || amountText.isEmpty() || violationText.isEmpty()) {
            throw new IllegalArgumentException("Усі поля мають бути заповнені");
        }

        double amount = Double.parseDouble(amountText);
        if (amount <= 0) {
            throw new IllegalArgumentException("Сума штрафу має бути більшою за 0");
        }

        Fine fine = new Fine();
        fine.setVehicleId(Integer.parseInt(vehicleText));
        fine.setDriverId(Integer.parseInt(driverText));
        fine.setFineDate(dateText);
        fine.setAmount(amount);
        fine.setViolationType(violationText);
        fine.setPaid(paidCheck.isSelected());
        fine.setAccident(accidentCheck.isSelected());
        return fine;
    }
}
