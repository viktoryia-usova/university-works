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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.Entities.*;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AddTeacher extends Application {
    @FXML
    private Button backButton;
    @FXML private Button addButton;
    @FXML private TableView<org.example.Entities.Application> teacherApplicationTable;
    @FXML private TableColumn<org.example.Entities.Application, String> AppIdColumn;
    @FXML private TableColumn<org.example.Entities.Application, String> TrNameColumn;
    @FXML private TableColumn<org.example.Entities.Application, String> TrLastnameColumn;
    @FXML private TableColumn<org.example.Entities.Application, String> TrLanguageColumn;
    private final Gson gson = new Gson();





    @FXML
    public void initialize() {
        backButton.setOnAction(event -> handleBackButtonAction());
        addButton.setOnAction(event -> addTeacherToDatabase());
        AppIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TrNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TrLastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));;
        TrLanguageColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));
        loadTeacherApplicationTable();
    }

    private void loadTeacherApplicationTable() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.TeacherApplication);

            // Отправка запроса на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Чтение ответа от сервера
            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());


            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String TrApplicationJson = parts[0];
                Type teacherApplicationListType = new TypeToken<List<org.example.Entities.Application>>() {}.getType();
                List<org.example.Entities.Application> teachersApplication = gson.fromJson(TrApplicationJson, teacherApplicationListType);
                ObservableList<org.example.Entities.Application> teacherObservableList = FXCollections.observableArrayList(teachersApplication);
                teacherApplicationTable.setItems(teacherObservableList);



            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
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
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddTeacher.class.getResource("/view/add-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Добавить преподавателя");
        stage.setScene(scene);
        stage.show();
    }
    private void addTeacherToDatabase() {
        // Получить выбранное заявление
        org.example.Entities.Application selectedApplication = teacherApplicationTable.getSelectionModel().getSelectedItem();


        if (selectedApplication == null ) {
            System.out.println("Не выбрано заявление");
            return;
        }

        try {
            // Создать запрос на добавление студента
            Request request = new Request();
            request.setRequestType(RequestType.AddTeacher);

            // Создать объект для передачи
            Teacher teacher = new Teacher();
            teacher.setProfile(new Profile(selectedApplication.getProfile().getId())); // ID профиля из заявления
            teacher.setLanguage(new Language(selectedApplication.getLanguage().getId())); // ID языка из заявления
           // ID выбранной группы

            // Добавить данные в запрос
            request.setMessage(gson.toJson(teacher));

            // Отправить запрос
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Получить ответ
            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                System.out.println("Преподаватель успешно добавлен!");
            } else {
                System.out.println("Ошибка при добавлении преподавателя: " + response.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Ошибка соединения с сервером: " + e.getMessage());
        }
    }
}
