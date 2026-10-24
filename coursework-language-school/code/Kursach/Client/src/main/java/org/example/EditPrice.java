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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPrice extends Application {
    private final Gson gson = new Gson();
    @FXML private Button backButton;
    @FXML private TextField coursePriceField;
    @FXML private Button editCourseButton;
    @FXML private ChoiceBox<String> groupChoiceBox; // Используем имя из FXML

    private Map<String, Integer> courseIdMap = new HashMap<>();

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> handleBackButtonAction());
        editCourseButton.setOnAction(event -> handleUpdatePrice());
        loadCourseIds();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddCourse.class.getResource("/view/update-price.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Изменить стоимость");
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

    private void loadCourseIds() {
        try {
            // Запрашиваем ID курсов
            Request request = new Request();
            request.setRequestType(RequestType.CourseIds);

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String courseIdsJson = parts[0];
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                List<Integer> courseIds = gson.fromJson(courseIdsJson, listType);

                if (courseIds != null && !courseIds.isEmpty()) {
                    // Запрашиваем названия курсов по полученным ID
                    loadCourseNames(courseIds);
                } else {
                    System.out.println("Список курсов пуст или не удалось загрузить данные.");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }

    private void loadCourseNames(List<Integer> courseIds) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.CourseNames);
            request.setMessage(gson.toJson(courseIds));

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                Type mapType = new TypeToken<Map<Integer, String>>() {}.getType();
                Map<Integer, String> courseNames = gson.fromJson(response.getMessage(), mapType);

                // Используем groupChoiceBox вместо courseChoiceBox
                groupChoiceBox.getItems().clear();
                courseIdMap.clear();

                for (Map.Entry<Integer, String> entry : courseNames.entrySet()) {
                    courseIdMap.put(entry.getValue(), entry.getKey());
                    groupChoiceBox.getItems().add(entry.getValue());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении названий курсов: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdatePrice() {
        // Получаем выбранное название курса из groupChoiceBox
        String selectedCourseName = groupChoiceBox.getValue();
        String priceText = coursePriceField.getText();

        if (selectedCourseName == null || priceText.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, выберите курс и введите стоимость.");
            return;
        }

        try {
            int newPrice = Integer.parseInt(priceText);
            int selectedCourseId = courseIdMap.get(selectedCourseName);

            // Создаем фоновую задачу для отправки запроса на сервер
            Task<Void> updatePriceTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Формируем запрос
                    Request request = new Request();
                    request.setRequestType(RequestType.UpdatePrice);
                    request.setMessage(gson.toJson(Map.of(
                            "courseId", selectedCourseId,
                            "newPrice", newPrice
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
                        Platform.runLater(() ->
                                showAlert("Успех", "Стоимость курса успешно обновлена!"));
                    } else {
                        Platform.runLater(() ->
                                showAlert("Ошибка", response.getMessage()));
                    }
                    return null;
                }
            };

            updatePriceTask.setOnFailed(e -> {
                Throwable exception = updatePriceTask.getException();
                showAlert("Ошибка", "Ошибка при отправке данных на сервер: " + exception.getMessage());
            });

            new Thread(updatePriceTask).start();

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Пожалуйста, введите корректное значение стоимости.");
        } catch (Exception e) {
            showAlert("Ошибка", "Произошла ошибка: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}