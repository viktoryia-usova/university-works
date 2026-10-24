package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class editSchedule extends Application {
    private final Gson gson = new Gson();
    @FXML
    private Button backButton;
    @FXML private Button saveButton;
    @FXML private TextField date;
    @FXML private TextField time;
    @FXML private TextField room;
    @FXML  private ChoiceBox<Integer> scheduleChoiceBox;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(editSchedule.class.getResource("/view/edit-schedule.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Добавить отзыв");
        stage.setScene(scene);
        stage.show();
    }
    @FXML public void initialize() {
        backButton.setOnAction(event -> handleBackButtonAction());
        saveButton.setOnAction(event -> handleUpdatePrice());
        loadIds();
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
    private void loadIds(){
        try {
            Request request = new Request();
            request.setRequestType(RequestType.ScheduleIds);

            // Отправка запроса на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Чтение ответа от сервера
            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());


            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String schedulesIdsJson = parts[0];
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                List<Integer> scheduleIds = gson.fromJson(schedulesIdsJson, listType);
                if (scheduleIds != null) {
                    scheduleChoiceBox.getItems().clear();
                    scheduleChoiceBox.getItems().addAll(scheduleIds);
                } else {
                    System.out.println("Список расписания пуст или не удалось загрузить данные.");
                }

            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }
    @FXML
    private void handleUpdatePrice() {
        Integer selectedScheduleId = scheduleChoiceBox.getValue();
        String datetext = date.getText();
        String timetext = time.getText();
        String roomtext = room.getText();

        if (selectedScheduleId == null || datetext.isEmpty()  || timetext.isEmpty() || roomtext.isEmpty())  {
            showAlert("Ошибка", "Пожалуйста, выберите курс и введите все данные.");
            return;
        }

        try {

            // Создаем фоновую задачу для отправки запроса на сервер
            Task<Void> updateScheduleTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Формируем запрос
                    Request request = new Request();
                    request.setRequestType(RequestType.editSchedule);
                    request.setMessage(gson.toJson(Map.of(
                            "id", selectedScheduleId,
                            "newDate", datetext,
                            "newTime", timetext,
                            "newRoom", roomtext
                    )));

                    // Отправляем запрос на сервер
                    ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                    ClientSocket.getInstance().getWriter().flush();
                    System.out.println("Запрос отправлен");

                    // Читаем ответ от сервера
                    String responseLine = ClientSocket.getInstance().getReader().readLine();
                    Response response = gson.fromJson(responseLine, Response.class);

                    // Проверяем успех операции
                    if (response.getSuccess()) {
                        // Выполняем обновление UI в основном потоке
                        Platform.runLater(() -> {
                            showAlert("Успех", "Расписание успешно обновлено!");
                        });
                    } else {
                        // Выполняем обновление UI в основном потоке
                        Platform.runLater(() -> {
                            showAlert("Ошибка", response.getMessage());
                        });
                    }
                    return null;
                }
            };

            // Обработка ошибок в фоновом потоке
            updateScheduleTask.setOnFailed(e -> {
                Throwable exception = updateScheduleTask.getException();
                showAlert("Ошибка", "Ошибка при отправке данных на сервер: " + exception.getMessage());
            });

            // Запуск фоново задачи
            new Thread(updateScheduleTask).start();

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Пожалуйста, введите корректное значение");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
