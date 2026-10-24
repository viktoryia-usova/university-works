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

public class editTeacherData extends Application {
    private final Gson gson = new Gson();
    @FXML private Button backButton;
    @FXML private Button saveButton;
    @FXML private TextField name;
    @FXML private TextField lastname;
    @FXML private TextField phone;
    @FXML private ChoiceBox<String> teacherChoiceBox; // Изменено на String

    private Map<String, Integer> teacherIdMap = new HashMap<>(); // Для хранения соответствия имен и ID

    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(editSchedule.class.getResource("/view/edit-datateacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Изменение данных преподавателя");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> handleBackButtonAction());
        saveButton.setOnAction(event -> handleUpdateTeacher());
        loadTeacherIds();

        // Добавляем обработчик выбора преподавателя
        teacherChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadTeacherData(teacherIdMap.get(newVal));
            }
        });
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

    private void loadTeacherIds() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.TeacherIds);

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String teacherIdsJson = parts[0];
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                List<Integer> teacherIds = gson.fromJson(teacherIdsJson, listType);

                if (teacherIds != null && !teacherIds.isEmpty()) {
                    loadTeacherNames(teacherIds);
                } else {
                    System.out.println("Список преподавателей пуст или не удалось загрузить данные.");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }

    private void loadTeacherNames(List<Integer> teacherIds) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.TeacherNames);
            request.setMessage(gson.toJson(teacherIds));

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                Type mapType = new TypeToken<Map<Integer, String>>() {}.getType();
                Map<Integer, String> teacherNames = gson.fromJson(response.getMessage(), mapType);

                teacherChoiceBox.getItems().clear();
                teacherIdMap.clear();

                for (Map.Entry<Integer, String> entry : teacherNames.entrySet()) {
                    teacherIdMap.put(entry.getValue(), entry.getKey());
                    teacherChoiceBox.getItems().add(entry.getValue());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении имен преподавателей: " + e.getMessage());
        }
    }

    private void loadTeacherData(int teacherId) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.GetTeacherData);
            request.setMessage(String.valueOf(teacherId));

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                Map<String, String> teacherData = gson.fromJson(response.getMessage(),
                        new TypeToken<Map<String, String>>() {}.getType());

                Platform.runLater(() -> {
                    name.setText(teacherData.get("name"));
                    lastname.setText(teacherData.get("lastname"));
                    phone.setText(teacherData.get("phone"));
                });
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных преподавателя: " + e.getMessage());
        }
    }

    private void handleUpdateTeacher() {
        String selectedTeacherName = teacherChoiceBox.getValue();
        String nametext = name.getText();
        String lastnametext = lastname.getText();
        String phonetext = phone.getText();

        if (selectedTeacherName == null || nametext.isEmpty() || lastnametext.isEmpty() || phonetext.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, выберите преподавателя и введите все данные.");
            return;
        }

        try {
            int selectedTeacherId = teacherIdMap.get(selectedTeacherName);

            Task<Void> updateTeacherTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Request request = new Request();
                    request.setRequestType(RequestType.editTeacher);
                    request.setMessage(gson.toJson(Map.of(
                            "id", selectedTeacherId,
                            "newName", nametext,
                            "newLastname", lastnametext,
                            "newPhone", phonetext
                    )));

                    ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                    ClientSocket.getInstance().getWriter().flush();

                    String responseLine = ClientSocket.getInstance().getReader().readLine();
                    Response response = gson.fromJson(responseLine, Response.class);

                    Platform.runLater(() -> {
                        if (response.getSuccess()) {
                            showAlert("Успех", "Данные успешно обновлены!");
                            // Обновляем имя в ChoiceBox, если изменили фамилию/имя
                            String updatedName = lastnametext + " " + nametext;
                            if (!selectedTeacherName.equals(updatedName)) {
                                teacherChoiceBox.getItems().remove(selectedTeacherName);
                                teacherChoiceBox.getItems().add(updatedName);
                                teacherIdMap.remove(selectedTeacherName);
                                teacherIdMap.put(updatedName, selectedTeacherId);
                                teacherChoiceBox.getSelectionModel().select(updatedName);
                            }
                        } else {
                            showAlert("Ошибка", response.getMessage());
                        }
                    });
                    return null;
                }
            };

            updateTeacherTask.setOnFailed(e -> {
                Throwable exception = updateTeacherTask.getException();
                showAlert("Ошибка", "Ошибка при отправке данных на сервер: " + exception.getMessage());
            });

            new Thread(updateTeacherTask).start();

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