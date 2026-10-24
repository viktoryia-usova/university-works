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

public class editStudentData extends Application {
    private final Gson gson = new Gson();
    @FXML private Button backButton;
    @FXML private Button saveButton;
    @FXML private TextField name;
    @FXML private TextField lastname;
    @FXML private TextField phone;
    @FXML private ChoiceBox<String> studentChoiceBox; // Изменено на String

    private Map<String, Integer> studentIdMap = new HashMap<>(); // Для хранения соответствия имен и ID

    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(editStudentData.class.getResource("/view/edit-data.fxml")); // Убедитесь, что это правильный FXML
        fxmlLoader.setController(this); // Устанавливаем текущий экземпляр как контроллер
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Изменение данных студента");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(event -> handleBackButtonAction());
        }
        if (saveButton != null) {
            saveButton.setOnAction(event -> handleUpdateStudent());
        }
        if (studentChoiceBox != null) {
            loadStudentIds();

            // Добавляем обработчик выбора студента
            studentChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    Integer studentId = studentIdMap.get(newVal);
                    if (studentId != null) {
                        loadStudentData(studentId);
                    }
                } else {
                    // Очищаем поля, если ничего не выбрано
                    name.clear();
                    lastname.clear();
                    phone.clear();
                }
            });
        }
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
            showAlert("Ошибка загрузки", "Не удалось загрузить страницу меню администратора.");
        }
    }

    private void loadStudentIds() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Request request = new Request();
                    request.setRequestType(RequestType.StudentIds);

                    ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                    ClientSocket.getInstance().getWriter().flush();

                    String responseLine = ClientSocket.getInstance().getReader().readLine();
                    Response response = gson.fromJson(responseLine, Response.class);

                    if (response.getSuccess()) {
                        // Предполагаем, что сервер возвращает только список ID, без split
                        Type listType = new TypeToken<List<Integer>>() {}.getType();
                        List<Integer> studentIds = gson.fromJson(response.getMessage(), listType);

                        if (studentIds != null && !studentIds.isEmpty()) {
                            loadStudentNames(studentIds);
                        } else {
                            Platform.runLater(() -> System.out.println("Список студентов пуст или не удалось загрузить данные."));
                        }
                    } else {
                        Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> System.out.println("Ошибка при получении данных с сервера: " + e.getMessage()));
                    Platform.runLater(() -> showAlert("Ошибка соединения", "Не удалось получить ID студентов с сервера."));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void loadStudentNames(List<Integer> studentIds) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Request request = new Request();
                    request.setRequestType(RequestType.StudentNames); // Новый тип запроса для получения имен студентов
                    request.setMessage(gson.toJson(studentIds));

                    ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                    ClientSocket.getInstance().getWriter().flush();

                    String responseLine = ClientSocket.getInstance().getReader().readLine();
                    Response response = gson.fromJson(responseLine, Response.class);

                    if (response.getSuccess()) {
                        Type mapType = new TypeToken<Map<Integer, String>>() {}.getType();
                        Map<Integer, String> studentNames = gson.fromJson(response.getMessage(), mapType);

                        Platform.runLater(() -> {
                            studentChoiceBox.getItems().clear();
                            studentIdMap.clear();

                            for (Map.Entry<Integer, String> entry : studentNames.entrySet()) {
                                studentIdMap.put(entry.getValue(), entry.getKey());
                                studentChoiceBox.getItems().add(entry.getValue());
                            }
                            if (!studentChoiceBox.getItems().isEmpty()) {
                                studentChoiceBox.getSelectionModel().selectFirst(); // Выбираем первый элемент
                            }
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> System.out.println("Ошибка при получении имен студентов: " + e.getMessage()));
                    Platform.runLater(() -> showAlert("Ошибка соединения", "Не удалось получить имена студентов с сервера."));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void loadStudentData(int studentId) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Request request = new Request();
                    request.setRequestType(RequestType.GetStudentData); // Новый тип запроса для получения данных одного студента
                    request.setMessage(String.valueOf(studentId));

                    ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                    ClientSocket.getInstance().getWriter().flush();

                    String responseLine = ClientSocket.getInstance().getReader().readLine();
                    Response response = gson.fromJson(responseLine, Response.class);

                    if (response.getSuccess()) {
                        Map<String, String> studentData = gson.fromJson(response.getMessage(),
                                new TypeToken<Map<String, String>>() {}.getType());

                        Platform.runLater(() -> {
                            name.setText(studentData.get("name"));
                            lastname.setText(studentData.get("lastname"));
                            phone.setText(studentData.get("phone")); // Убедитесь, что ключ "phone" соответствует серверу
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> System.out.println("Ошибка при получении данных студента: " + e.getMessage()));
                    Platform.runLater(() -> showAlert("Ошибка соединения", "Не удалось получить данные студента."));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void handleUpdateStudent() {
        String selectedStudentName = studentChoiceBox.getValue();
        String nametext = name.getText();
        String lastnametext = lastname.getText();
        String phonetext = phone.getText();

        if (selectedStudentName == null || nametext.isEmpty() || lastnametext.isEmpty() || phonetext.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, выберите студента и введите все данные.");
            return;
        }

        try {
            int selectedStudentId = studentIdMap.get(selectedStudentName);

            Task<Void> updateStudentTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Request request = new Request();
                    request.setRequestType(RequestType.editStudent); // Убедитесь, что это правильный RequestType на сервере
                    request.setMessage(gson.toJson(Map.of(
                            "id", selectedStudentId,
                            "newName", nametext,
                            "newLastname", lastnametext,
                            "newPhone", phonetext
                    )));

                    ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                    ClientSocket.getInstance().getWriter().flush();
                    System.out.println("Запрос на обновление студента отправлен");

                    String responseLine = ClientSocket.getInstance().getReader().readLine();
                    Response response = gson.fromJson(responseLine, Response.class);

                    Platform.runLater(() -> {
                        if (response.getSuccess()) {
                            showAlert("Успех", "Данные успешно обновлены!");
                            // Обновляем имя в ChoiceBox, если изменили фамилию/имя
                            String updatedName = lastnametext + " " + nametext; // Пример формирования полного имени
                            // Важно: если имя на сервере формируется по-другому,
                            // нужно получить его оттуда или убедиться в совпадении.
                            if (!selectedStudentName.equals(updatedName)) {
                                studentChoiceBox.getItems().remove(selectedStudentName);
                                studentChoiceBox.getItems().add(updatedName);
                                studentIdMap.remove(selectedStudentName);
                                studentIdMap.put(updatedName, selectedStudentId);
                                studentChoiceBox.getSelectionModel().select(updatedName);
                            }
                        } else {
                            showAlert("Ошибка", response.getMessage());
                        }
                    });
                    return null;
                }
            };

            updateStudentTask.setOnFailed(e -> {
                Throwable exception = updateStudentTask.getException();
                Platform.runLater(() -> showAlert("Ошибка", "Ошибка при отправке данных на сервер: " + exception.getMessage()));
            });

            new Thread(updateStudentTask).start();

        } catch (Exception e) {
            showAlert("Ошибка", "Произошла ошибка: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}