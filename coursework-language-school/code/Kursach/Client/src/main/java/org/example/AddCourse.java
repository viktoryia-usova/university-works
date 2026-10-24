package org.example;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.Entities.Course;
import org.example.Entities.Price_list;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;

public class AddCourse extends Application {
    private final Gson gson = new Gson();
    @FXML
    private Button backButton;
    @FXML
    private TextField courseNameField;

    @FXML
    private TextField courseLevelField;
    @FXML  private TextField coursePriceField;
    @FXML
    private Button addCourseButton;
    @FXML public void initialize() {
       backButton.setOnAction(event -> handleBackButtonAction());
       addCourseButton.setOnAction(event -> handleAddCourseButtonAction());
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddCourse.class.getResource("/view/add-course.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Добавить курс");
        stage.setScene(scene);
        stage.show();
    }
    private void handleBackButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin-menu.fxml"));
            AnchorPane trainingApplicationPane = loader.load();
            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Меню администратора");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handleAddCourseButtonAction() {
        // Получение данных из текстовых полей
        String courseName = courseNameField.getText();
        String courseLevel = courseLevelField.getText();
        String coursePriceText = coursePriceField.getText();
        System.out.println(courseName);
        System.out.println(courseLevel);
        System.out.println(coursePriceText);
        if (courseName.isEmpty() || courseLevel.isEmpty() || coursePriceText.isEmpty()) {
            showAlert("Ошибка", "Все поля должны быть заполнены.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int coursePrice = Integer.parseInt(coursePriceText); // Преобразование цены в int

            // Создание объектов
            Course course = new Course();
            course.setCourse_type(courseName);
            course.setCourse_level(courseLevel);

            Price_list priceList = new Price_list();
            priceList.setCourse(course);
            priceList.setPrice(coursePrice);

            // Сериализация объектов в JSON строки
            String courseJson = gson.toJson(course);
            String priceListJson = gson.toJson(priceList);

            // Объединение двух JSON строк, разделённых символом ";"
            String combinedMessage = courseJson + ";" + priceListJson;

            // Формирование запроса
            Request request = new Request();
            request.setRequestType(RequestType.AddCourse);
            request.setMessage(combinedMessage); // Отправка объединенной строки

            // Асинхронная отправка запроса
            new Thread(() -> {
                try {
                    ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                    ClientSocket.getInstance().getWriter().flush();

                    // Чтение ответа от сервера
                    String responseLine = ClientSocket.getReader().readLine();
                    Response response = gson.fromJson(responseLine, Response.class);
                    System.out.println(response.getMessage());

                    Platform.runLater(() -> {
                        if (response.getSuccess()) {
                            showAlert("Успех", "Курс успешно добавлен.", Alert.AlertType.INFORMATION);
                            courseNameField.clear();
                            courseLevelField.clear();
                            coursePriceField.clear();
                        } else {
                            showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(() -> showAlert("Ошибка", "Не удалось связаться с сервером.", Alert.AlertType.ERROR));
                    e.printStackTrace();
                }
            }).start();
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Цена должна быть числом.", Alert.AlertType.ERROR);
        }
    }


    // Вспомогательный метод для отображения уведомлений
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
