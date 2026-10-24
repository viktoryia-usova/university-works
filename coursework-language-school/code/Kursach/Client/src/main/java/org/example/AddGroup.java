package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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

public class AddGroup extends Application {
    private final Gson gson = new Gson();
    @FXML
    private Button backButton;
    @FXML
    private Button addButton;
    @FXML
    private ChoiceBox<String> scheduleChoiceBox;
    @FXML
    private ChoiceBox<String> teacherChoiceBox;
    @FXML
    private ChoiceBox<String> languageChoiceBox;
    @FXML
    private ChoiceBox<String> groupChoiceBox;

    private Map<String, Integer> teacherIdMap = new HashMap<>();
    private Map<String, Integer> languageIdMap = new HashMap<>();
    private Map<String, Integer> groupIdMap = new HashMap<>();
    private Map<String, Integer> scheduleIdMap = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddFeedback.class.getResource("/view/add-group.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Добавить группу");
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

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> handleBackButtonAction());
        loadTeacherIds();
        loadLanguagesIds();
        loadGroupIds();
        loadScheduleIds();
        addButton.setOnAction(event -> addGroup());
    }

    private void loadGroupIds() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.CourseIds);

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());

            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String courseIdsJson = parts[0];
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                List<Integer> groupIds = gson.fromJson(courseIdsJson, listType);

                if (groupIds != null && !groupIds.isEmpty()) {
                    loadGroupNames(groupIds); // Загружаем названия групп по полученным ID
                } else {
                    System.out.println("Список групп пуст или не удалось загрузить данные.");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }

    private void loadGroupNames(List<Integer> groupIds) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.CourseNames);
            request.setMessage(gson.toJson(groupIds));

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                Type mapType = new TypeToken<Map<Integer, String>>() {}.getType();
                Map<Integer, String> groupNames = gson.fromJson(response.getMessage(), mapType);

                groupChoiceBox.getItems().clear();
                groupIdMap.clear();

                for (Map.Entry<Integer, String> entry : groupNames.entrySet()) {
                    groupIdMap.put(entry.getValue(), entry.getKey());
                    groupChoiceBox.getItems().add(entry.getValue());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении названий групп: " + e.getMessage());
        }
    }

    private void loadLanguagesIds() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.LanguageIds);

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());

            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String languageIdsJson = parts[0];
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                List<Integer> languageIds = gson.fromJson(languageIdsJson, listType);

                if (languageIds != null && !languageIds.isEmpty()) {
                    loadLanguageNames(languageIds);
                } else {
                    System.out.println("Список языков пуст или не удалось загрузить данные.");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }

    private void loadLanguageNames(List<Integer> languageIds) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.LanguageNames);
            request.setMessage(gson.toJson(languageIds));

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                Type mapType = new TypeToken<Map<Integer, String>>() {}.getType();
                Map<Integer, String> languageNames = gson.fromJson(response.getMessage(), mapType);

                languageChoiceBox.getItems().clear();
                languageIdMap.clear();

                for (Map.Entry<Integer, String> entry : languageNames.entrySet()) {
                    languageIdMap.put(entry.getValue(), entry.getKey());
                    languageChoiceBox.getItems().add(entry.getValue());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении названий языков: " + e.getMessage());
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
            System.out.println(response.getMessage());

            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String teachersIdsJson = parts[0];
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                List<Integer> teacherIds = gson.fromJson(teachersIdsJson, listType);

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

    private void loadScheduleIds() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.ScheduleIds);

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());

            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String schedulesIdsJson = parts[0];
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                List<Integer> scheduleIds = gson.fromJson(schedulesIdsJson, listType);

                if (scheduleIds != null && !scheduleIds.isEmpty()) {
                    loadScheduleNames(scheduleIds);
                } else {
                    System.out.println("Список расписаний пуст или не удалось загрузить данные.");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }

    private void loadScheduleNames(List<Integer> scheduleIds) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.ScheduleNames);
            request.setMessage(gson.toJson(scheduleIds));

            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                Type mapType = new TypeToken<Map<Integer, String>>() {}.getType();
                Map<Integer, String> scheduleNames = gson.fromJson(response.getMessage(), mapType);

                scheduleChoiceBox.getItems().clear();
                scheduleIdMap.clear();

                for (Map.Entry<Integer, String> entry : scheduleNames.entrySet()) {
                    scheduleIdMap.put(entry.getValue(), entry.getKey());
                    scheduleChoiceBox.getItems().add(entry.getValue());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении названий расписаний: " + e.getMessage());
        }
    }

    private void addGroup() {
        try {
            // Получение выбранных значений и их ID
            String teacherName = teacherChoiceBox.getValue();
            String languageName = languageChoiceBox.getValue();
            String groupName = groupChoiceBox.getValue();
            String scheduleName = scheduleChoiceBox.getValue();

            int teacherId = teacherIdMap.get(teacherName);
            int languageId = languageIdMap.get(languageName);
            int courseId = groupIdMap.get(groupName);
            int scheduleId = scheduleIdMap.get(scheduleName);

            // Формирование запроса
            Request request = new Request();
            request.setRequestType(RequestType.addGroup);
            request.setMessage(gson.toJson(Map.of(
                    "teacherId", teacherId,
                    "languageId", languageId,
                    "courseId", courseId,
                    "scheduleId", scheduleId
            )));

            // Отправка запроса на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Чтение ответа от сервера
            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            // Обработка ответа
            if (response.getSuccess()) {
                showAlert("Успех", "Группа успешно добавлена!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Ошибка добавления группы: " + response.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}