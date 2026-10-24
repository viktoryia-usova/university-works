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
import org.example.Entities.Student;
import org.example.Entities.Test;
import org.example.TCP.CurrentSession;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;

public class EnglishTest extends Application {
    @FXML
    private TextField answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8;
    @FXML private Button backButton;
    @FXML private Button sendButton;
    private final Gson gson = new Gson();
    private final String[] correctAnswers = {
            "live", "doesn't like", "is reading", "have already finished",
            "go", "is being repaired", "had studied", "had not missed"
    };
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(EnglishTest.class.getResource("/view/english-test.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Тест на уровень английского");
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void initialize() {
        backButton.setOnAction(event -> handleBackButtonAction());
        sendButton.setOnAction(event -> handleSendAnswers());
    }
    private void handleBackButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/student-menu.fxml"));
            AnchorPane trainingApplicationPane = loader.load();
            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Меню студента");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final int currentUserId = CurrentSession.getInstance().getCurrentStudentId();



    private void handleSendAnswers() {
        // Получение введённых пользователем ответов
        String[] userAnswers = {
                answer1.getText().trim(), answer2.getText().trim(), answer3.getText().trim(),
                answer4.getText().trim(), answer5.getText().trim(), answer6.getText().trim(),
                answer7.getText().trim(), answer8.getText().trim()
        };

        // Подсчёт правильных ответов
        int correctCount = 0;
        for (int i = 0; i < correctAnswers.length; i++) {
            if (userAnswers[i].equalsIgnoreCase(correctAnswers[i])) {
                correctCount++;
            }
        }

        Student student = new Student();
        System.out.println(currentUserId);
        student.setId(currentUserId);
        int userLevel = determineUserLevel(correctCount);
        String level =getLevelName(userLevel);

        Test testResult = new Test(student, correctCount, level);

        Request request = new Request();
        request.setRequestType(RequestType.testResult);
        request.setMessage(gson.toJson(testResult));
        new Thread(() -> {
            try {


                // Отправка запроса
                ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                ClientSocket.getInstance().getWriter().flush();

                // Чтение ответа от сервера
                String responseLine = ClientSocket.getReader().readLine();
                Response response = gson.fromJson(responseLine, Response.class);

                // Показ результата в UI
                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        showAlert("Успех", "Ваш результат успешно сохранён.\nВаш уровень: " + getLevelName(userLevel), Alert.AlertType.INFORMATION);
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

    private int determineUserLevel(int correctCount) {
        return switch (correctCount) {
            case 1 -> 1; // Beginner
            case 2 -> 2; // Elementary
            case 3 -> 3; // Pre-Intermediate
            case 4 -> 4; // Intermediate
            case 5 -> 5; // Intermediate Plus
            case 6 -> 6; // Upper-Intermediate
            case 7 -> 7; // Advanced
            case 8 -> 8; // Advanced Plus
            default -> 1; // Минимальный уровень
        };
    }

    private String getLevelName(int userLevel) {
        return switch (userLevel) {
            case 1 -> "Beginner";
            case 2 -> "Elementary";
            case 3 -> "Pre-Intermediate";
            case 4 -> "Intermediate";
            case 5 -> "Intermediate Plus";
            case 6 -> "Upper-Intermediate";
            case 7 -> "Advanced";
            case 8 -> "Advanced Plus";
            default -> "Unknown";
        };
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
