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
import org.example.Entities.Student;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AddToGroup extends Application {
    private final Gson gson = new Gson();
    @FXML
    private Button backButton;
    @FXML
    private Button addButton;
    @FXML
    private ChoiceBox<Integer> groupChoiceBox;
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String> StNameColumn;
    @FXML private TableColumn<Student, String> StLastnameColumn;
    @FXML private TableColumn<Student, String> StPhoneColumn;
    @FXML private TableColumn<Student, String> StLanguageColumn;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddFeedback.class.getResource("/view/add-to-group.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Добавить в группу");
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
    @FXML public void initialize() {
        loadStudents();
        StNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        StLastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        StPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
        StLanguageColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));
        backButton.setOnAction(event -> handleBackButtonAction());
        loadGroupIds();
        addButton.setOnAction(event -> addToGroup());
    }
    private void loadGroupIds(){
        try {
            Request request = new Request();
            request.setRequestType(RequestType.GroupIds);

            // Отправка запроса на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Чтение ответа от сервера
            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());


            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String courseIdsJson = parts[0];
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                List<Integer> groupIds = gson.fromJson(courseIdsJson, listType);
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
    private void loadStudents(){
        try {
            // Создаем запрос для получения списка курсов и прайс-листов
            Request request = new Request();
            request.setRequestType(RequestType.loadStudent);

            // Отправка запроса на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Чтение ответа от сервера
            String responseLine = ClientSocket.getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);


            if (response.getSuccess()) {
                // Разделение сообщения на курсы и прайс-листы
                String[] parts = response.getMessage().split(";");
                String studentsJson = parts[0];
                Type studentListType = new TypeToken<List<Student>>() {}.getType();
                List<Student> students = gson.fromJson(studentsJson, studentListType);
                ObservableList<Student> studentObservableList = FXCollections.observableArrayList(students);
                studentsTable.setItems(studentObservableList);
            } else {
                System.out.println("Ошибка при загрузке студентов: " + response.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }
    private void addToGroup() {
        // Получение выбранного студента и группы
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        Integer selectedGroupId = groupChoiceBox.getValue();

        if (selectedStudent == null || selectedGroupId == null) {
            System.out.println("Пожалуйста, выберите студента и группу.");
            return;
        }
            Group group =new Group(selectedGroupId);
            Student student =new Student();
            student.setId(selectedStudent.getId());
            student.setName(selectedStudent.getName());
            student.setLastname(selectedStudent.getLastname());
            student.setLanguage(selectedStudent.getLanguage());
            student.setGroup(group);
        try {
            Request request = new Request();
            request.setRequestType(RequestType.AddToGroup);
            request.setMessage(gson.toJson(student));
            System.out.println(request.getMessage());
            // Отправляем запрос на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Получаем ответ от сервера
        String responseLine = ClientSocket.getReader().readLine();
        Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                System.out.println("Студент успешно добавлен в группу.");
                loadStudents(); // Обновляем список студентов
            } else {
                System.out.println("Ошибка при добавлении студента в группу: " + response.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Ошибка соединения с сервером: " + e.getMessage());
        }

    }

}
