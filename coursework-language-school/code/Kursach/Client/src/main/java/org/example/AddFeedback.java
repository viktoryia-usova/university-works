package org.example;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.Entities.Feedback;
import org.example.Entities.User;
import org.example.TCP.CurrentSession;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;


import java.io.IOException;


public class AddFeedback extends Application {
    private final Gson gson = new Gson();
    @FXML
    private TextArea feedback;
    @FXML
    private Button addFeedback;
    @FXML
    private Button backButton;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddFeedback.class.getResource("/view/add-feedback.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Добавить отзыв");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        addFeedback.setOnAction(event -> handleAddFeedbackAction());
        backButton.setOnAction(event -> handleBackButtonAction());

    }

    private void handleBackButtonAction() {
        try {
            FXMLLoader loader;
            String role = CurrentSession.getInstance().getCurrentRole();  // Получаем роль текущего пользователя

            if ("student".equalsIgnoreCase(role)) {
                loader = new FXMLLoader(getClass().getResource("/view/student-menu.fxml"));
            } else if ("teacher".equalsIgnoreCase(role)) {
                loader = new FXMLLoader(getClass().getResource("/view/teacher-menu.fxml"));
            } else {
                showAlert("Ошибка", "Неизвестная роль пользователя: " + role, Alert.AlertType.ERROR);
                return;
            }

            // Загружаем соответствующее меню
            AnchorPane menuPane = loader.load();
            Scene scene = new Scene(menuPane);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Меню");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить меню.", Alert.AlertType.ERROR);
        }
    }


    private void handleAddFeedbackAction() {
        String feedbackText = feedback.getText().trim(); // Убираем пробелы
        int userId = CurrentSession.getInstance().getCurrentProfileId(); // Получаем ID текущего пользователя
        System.out.println(feedbackText);
        System.out.println(userId);
        if (feedbackText.isEmpty()) {
            showAlert("Ошибка", "Отзыв не может быть пустым.", Alert.AlertType.ERROR);
            return;
        }

        User user = new User();
        user.setId(userId); // Устанавливаем ID пользователя

        Feedback feedbackData = new Feedback(feedbackText, user);
        Request request = new Request();
        request.setRequestType(RequestType.addFeedback);
        request.setMessage(gson.toJson(feedbackData));
        System.out.println("Отправляемый JSON: " + gson.toJson(request));



        new Thread(() -> {
            try {
                ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                ClientSocket.getInstance().getWriter().flush();

                String responseLine = ClientSocket.getReader().readLine();
                Response response = gson.fromJson(responseLine, Response.class);

                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        showAlert("Успех", "Отзыв успешно добавлен.", Alert.AlertType.INFORMATION);
                        feedback.clear();
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


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}