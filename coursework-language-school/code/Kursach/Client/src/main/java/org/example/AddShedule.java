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
import org.example.Entities.Schedule;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;

public class AddShedule extends Application {
    private final Gson gson = new Gson();
    @FXML private Button backButton;
    @FXML private Button addButton;
    @FXML private TextField date;
    @FXML private TextField time;
    @FXML private TextField room;
    @FXML public void initialize() {
        backButton.setOnAction(event -> handleBackButtonAction());
        addButton.setOnAction(event -> handleAddScheduleAction());
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddShedule.class.getResource("/view/add-schedule.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Добавить отзыв");
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
    private void handleAddScheduleAction() {
        String dateText = date.getText();
        String timeText = time.getText();
        String roomText = room.getText();
        if (dateText.isEmpty() || timeText.isEmpty() || roomText.isEmpty()) {
            showAlert("Ошибка", "Все поля должны быть заполнены.", Alert.AlertType.ERROR);
            return;
        }
            // Создание объектов
            Schedule schedule = new Schedule();
            schedule.setDate(dateText);
            schedule.setTime(timeText);
            schedule.setRoom(roomText);

           String scheduleJson = gson.toJson(schedule);

            // Формирование запроса
            Request request = new Request();
            request.setRequestType(RequestType.AddSchedule);
            request.setMessage(scheduleJson); // Отправка объединенной строки

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
                            showAlert("Успех", "Расписание успешно добавлен.", Alert.AlertType.INFORMATION);
                            date.clear();
                            time.clear();
                            room.clear();
                        } else {
                            showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(() -> showAlert("Ошибка", "Не удалось связаться с сервером.", Alert.AlertType.ERROR));
                    e.printStackTrace();
                }
            }).start();


    }
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
