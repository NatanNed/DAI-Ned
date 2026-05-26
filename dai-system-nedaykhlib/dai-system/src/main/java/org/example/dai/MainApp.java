package org.example.dai;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Головний клас JavaFX-застосунку інформаційної системи ДАІ.
 */
public class MainApp extends Application {

    /**
     * Запускає головне вікно програми.
     *
     * @param stage головна сцена JavaFX
     * @throws Exception якщо FXML-файл не знайдено або не завантажено
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fines-view.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("DAI Information System");
        stage.setScene(scene);
        stage.setWidth(1150);
        stage.setHeight(720);
        stage.show();
    }

    /**
     * Точка входу в програму.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        launch(args);
    }
}
