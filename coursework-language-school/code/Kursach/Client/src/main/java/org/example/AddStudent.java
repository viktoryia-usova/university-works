package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.Entities.Group;
import org.example.Entities.Language;
import org.example.Entities.Profile;
import org.example.Entities.Student;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AddStudent extends Application {
    @FXML
    private Button backButton;
    @FXML private Button saveButton;
    @FXML private ChoiceBox<Integer> groupChoiceBox;
    @FXML private TableView<org.example.Entities.Application> studentApplicationTable;
    @FXML private TableColumn<org.example.Entities.Application, String> AppIdColumn;
    @FXML private TableColumn<org.example.Entities.Application, String> StNameColumn;
    @FXML private TableColumn<org.example.Entities.Application, String> StLastnameColumn;
    @FXML private TableColumn<org.example.Entities.Application, String> StLanguageColumn;
    private final Gson gson = new Gson();


    @FXML
    public void initialize() {
        backButton.setOnAction(event -> handleBackButtonAction());
        saveButton.setOnAction(event -> addStudentToDatabase());
        AppIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        StNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        StLastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));;
        StLanguageColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));
        loadStudentApplicationTable();
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
    private void loadStudentApplicationTable() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.StudentApplication);

            // Отправка запроса на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Чтение ответа от сервера
            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());


            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String StApplicationJson = parts[0];
                Type studentApplicationListType = new TypeToken<List<org.example.Entities.Application>>() {}.getType();
                List<org.example.Entities.Application> studentsApplication = gson.fromJson(StApplicationJson, studentApplicationListType);
                ObservableList<org.example.Entities.Application> studentObservableList = FXCollections.observableArrayList(studentsApplication);
                studentApplicationTable.setItems(studentObservableList);
                String groupIdsJson = parts[1];
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                List<Integer> groupIds = gson.fromJson(groupIdsJson, listType);
                if (groupIds != null) {
                    groupChoiceBox.getItems().clear();
                    groupChoiceBox.getItems().addAll(groupIds);
                } else {
                    System.out.println("Список групп пуст или не удалось загрузить данные.");
                }

            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddStudent.class.getResource("/view/add-student.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Добавить студента");
        stage.setScene(scene);
        stage.show();
    }
    private void addStudentToDatabase() {
        // Получить выбранное заявление
        org.example.Entities.Application selectedApplication = studentApplicationTable.getSelectionModel().getSelectedItem();
        Integer selectedGroupId = groupChoiceBox.getValue();

        if (selectedApplication == null || selectedGroupId == null) {
            System.out.println("Не выбрано заявление или группа!");
            return;
        }

        try {
            // Создать запрос на добавление студента
            Request request = new Request();
            request.setRequestType(RequestType.AddStudent);

            // Создать объект для передачи
            Student student = new Student();
            student.setProfile(new Profile(selectedApplication.getProfile().getId())); // ID профиля из заявления
            student.setLanguage(new Language(selectedApplication.getLanguage().getId())); // ID языка из заявления
            student.setGroup(new Group(selectedGroupId)); // ID выбранной группы

            // Добавить данные в запрос
            request.setMessage(gson.toJson(student));

            // Отправить запрос
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Получить ответ
            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                System.out.println("Студент успешно добавлен!");
            } else {
                System.out.println("Ошибка при добавлении студента: " + response.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Ошибка соединения с сервером: " + e.getMessage());
        }
    }
}
