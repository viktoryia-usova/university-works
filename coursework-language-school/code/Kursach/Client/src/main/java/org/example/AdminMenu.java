package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.Entities.*;
import org.example.Server.Entities.Price_list;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AdminMenu extends Application {

    @FXML private Accordion accordion;
    @FXML private AnchorPane applicationPane;
    @FXML private AnchorPane coursesPane;
    @FXML private AnchorPane pricePane;
    @FXML private AnchorPane reviewsPane;
    @FXML private AnchorPane groupsPane;
    @FXML private AnchorPane schedulePane;
    @FXML private AnchorPane teachersPane;
    @FXML private AnchorPane studentsPane;
    @FXML private TableView<Course> coursesTable;
    @FXML private TableView<Price_list> priceTable;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableView<Group> groupsTable;
    @FXML private TableView<Schedule> schedulesTable;
    @FXML private TableView<Teacher> teachersTable;
    @FXML private TableView<Student> studentsTable;

    @FXML private Button exitButton;
    @FXML private Button studentApplication;
    @FXML private Button teacherApplication;
    @FXML private Button courseApplication;
    @FXML private Button deleteCourseButton;
    @FXML private Button editPriceButton;
    @FXML private Button deleteFeedbackButton;
    @FXML private Button addScheduleButton;
    @FXML private Button deleteScheduleButton;
    @FXML private Button editScheduleButton;
    @FXML private Button addGroupButton;
    @FXML private Button deleteGroupButton;
    @FXML private Button deleteTeacherButton;
    @FXML private Button deleteStudentButton;
    @FXML private Button editTeacherButton;
    @FXML private Button editStudentButton;
    @FXML private Button addToGroupButton;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseLevelColumn;
    @FXML private TableColumn<Price_list, String> courseNameColumn1;
    @FXML private TableColumn<Price_list, String> coursePriceColumn;
    @FXML private TableColumn<Feedback, String> usernameColumn;
    @FXML private TableColumn<Feedback, String> reviewColumn;
    @FXML private TableColumn<Group, String> groupNumberColumn;
    @FXML private TableColumn<Group, String> TeacherColumn;
    @FXML private TableColumn<Group, String> languageColumn;
    @FXML private TableColumn<Group, String> groupDateColumn;
    @FXML private TableColumn<Group, String> groupTimeColumn;
    @FXML private TableColumn<Schedule, String> scheduleTimeColumn;
    @FXML private TableColumn<Schedule, String> scheduleDateColumn;
    @FXML private TableColumn<Schedule, String> scheduleRoomColumn;
    @FXML private TableColumn<Schedule, String> scheduleGroupColumn;
    @FXML private TableColumn<Teacher, String> NameColumn;
    @FXML private TableColumn<Teacher, String> LastnameColumn;
    @FXML private TableColumn<Teacher, String> PhoneColumn;
    @FXML private TableColumn<Teacher, String> LangColumn;
    @FXML private TableColumn<Student, String> StNameColumn;
    @FXML private TableColumn<Student, String> StLastnameColumn;
    @FXML private TableColumn<Student, String> StPhoneColumn;
    @FXML private TableColumn<Student, String> StLanguageColumn;
    @FXML private TableColumn<Student, String> StGroupColumn;


    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        if (accordion != null) {
            hideAllPanes();

            for (TitledPane pane : accordion.getPanes()) {
                pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        hideAllPanes();
                        switch (pane.getText()) {
                            case "Заявки" -> showApplications();
                            case "Курсы" -> showCourses();
                            case "Прайс-лист" -> showPrices();
                            case "Отзывы" -> showReviews();
                            case "Группы" -> showGroups();
                            case "Расписание" -> showSchedule();
                            case "Преподаватели" -> showTeachers();
                            case "Студенты" -> showStudents();
                        }
                    }
                });
            }

            exitButton.setOnAction(event -> closeApplication());
            studentApplication.setOnAction(event -> openStudentApplicationPage());
            teacherApplication.setOnAction(event -> openTeachersApplicationPage());
            courseApplication.setOnAction(event->openAddCoursePage());
            deleteCourseButton.setOnAction(event -> handleDeleteCourse() );
            editPriceButton.setOnAction(event -> openEditPricePage());
            deleteFeedbackButton.setOnAction(event -> handleDeleteFeedback() );
            addScheduleButton.setOnAction(event -> openAddSchedulePage());
            deleteScheduleButton.setOnAction(event -> handleDeleteSchedule() );
            editScheduleButton.setOnAction(event -> openEditSchedulePage());
            addGroupButton.setOnAction(event -> openAddGroupPage());
            deleteGroupButton.setOnAction(event -> handleDeleteGroup() );
            deleteTeacherButton.setOnAction(event -> handleDeleteTeacher() );
            deleteStudentButton.setOnAction(event -> handleDeleteStudent() );
            editTeacherButton.setOnAction(event -> openEditTeacherPage());
            editStudentButton.setOnAction(event -> openEditStudentPage());
            addToGroupButton.setOnAction(event -> openAddToGroupPage());
            courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("course_type"));
            courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("course_level"));
            courseNameColumn1.setCellValueFactory(new PropertyValueFactory<>("courseName"));
            coursePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
            reviewColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
            groupNumberColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            TeacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
            languageColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));
            groupDateColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleDate"));
            groupTimeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleTime"));
            scheduleTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            scheduleDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            scheduleRoomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
            scheduleGroupColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            NameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            LastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
            PhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
            LangColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));
            StNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            StLastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
            StPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
            StLanguageColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));
            StGroupColumn.setCellValueFactory(new PropertyValueFactory<>("groupNumber"));



        }
    }
    private void openAddGroupPage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-group.fxml"));
            AnchorPane trainingApplicationPane = loader.load();

            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) addGroupButton.getScene().getWindow();
            stage.setTitle("Добавить группу");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openAddToGroupPage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-to-group.fxml"));
            AnchorPane trainingApplicationPane = loader.load();

            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) addToGroupButton.getScene().getWindow();
            stage.setTitle("Добавить в группу");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openEditTeacherPage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit-datateacher.fxml"));
            AnchorPane trainingApplicationPane = loader.load();

            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) editTeacherButton.getScene().getWindow();
            stage.setTitle("Изменить данные учителя");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openEditStudentPage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit-data.fxml"));
            AnchorPane trainingApplicationPane = loader.load();
            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) editStudentButton.getScene().getWindow();
            stage.setTitle("Изменить данные студента");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openEditPricePage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update-price.fxml"));
            AnchorPane trainingApplicationPane = loader.load();

            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) editPriceButton.getScene().getWindow();
            stage.setTitle("Изменить стоимость");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openEditSchedulePage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit-schedule.fxml"));
            AnchorPane trainingApplicationPane = loader.load();

            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage)  editScheduleButton.getScene().getWindow();
            stage.setTitle("Изменить стоимость");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openAddCoursePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-course.fxml"));
            AnchorPane trainingApplicationPane = loader.load();

            // Замените содержимое текущей сцены или добавьте trainingApplicationPane к родителю
            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) courseApplication.getScene().getWindow();
            stage.setTitle("Добавить курс");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openAddSchedulePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-schedule.fxml"));
            AnchorPane trainingApplicationPane = loader.load();

            // Замените содержимое текущей сцены или добавьте trainingApplicationPane к родителю
            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) addScheduleButton.getScene().getWindow();
            stage.setTitle("Добавить расписание");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openStudentApplicationPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-student.fxml"));
            AnchorPane trainingApplicationPane = loader.load();

            // Замените содержимое текущей сцены или добавьте trainingApplicationPane к родителю
            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) studentApplication.getScene().getWindow();
            stage.setTitle("Заявка на обучение");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openTeachersApplicationPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-teacher.fxml"));
            AnchorPane teachingApplicationPane = loader.load();

            // Замените содержимое текущей сцены или добавьте teachingApplicationPane к родителю
            Scene scene = new Scene(teachingApplicationPane);
            Stage stage = (Stage) teacherApplication.getScene().getWindow();
            stage.setTitle("Заявка на преподавание");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void closeApplication() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void hideAllPanes() {
        applicationPane.setVisible(false);
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        groupsPane.setVisible(false);
        schedulePane.setVisible(false);
        teachersPane.setVisible(false);
        studentsPane.setVisible(false);
    }

    private void showApplications() {
        applicationPane.setVisible(true);
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        groupsPane.setVisible(false);
        schedulePane.setVisible(false);
        teachersPane.setVisible(false);
        studentsPane.setVisible(false);
    }

    private void showCourses() {
        loadCourses();
        applicationPane.setVisible(false);
        coursesPane.setVisible(true);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        groupsPane.setVisible(false);
        schedulePane.setVisible(false);
        teachersPane.setVisible(false);
        studentsPane.setVisible(false);
    }

    private void showPrices() {
        loadCourses();
        applicationPane.setVisible(false);
        coursesPane.setVisible(false);
        pricePane.setVisible(true);
        reviewsPane.setVisible(false);
        groupsPane.setVisible(false);
        schedulePane.setVisible(false);
        teachersPane.setVisible(false);
        studentsPane.setVisible(false);
    }

    private void showReviews() {
        loadCourses();
        applicationPane.setVisible(false);
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(true);
        groupsPane.setVisible(false);
        schedulePane.setVisible(false);
        teachersPane.setVisible(false);
        studentsPane.setVisible(false);
    }

    private void showGroups() {
        loadCourses();
        applicationPane.setVisible(false);
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        groupsPane.setVisible(true);
        schedulePane.setVisible(false);
        teachersPane.setVisible(false);
        studentsPane.setVisible(false);
    }

    private void showSchedule() {
        loadCourses();
        applicationPane.setVisible(false);
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        groupsPane.setVisible(false);
        schedulePane.setVisible(true);
        teachersPane.setVisible(false);
        studentsPane.setVisible(false);
    }

    private void showTeachers() {
        loadCourses();
        applicationPane.setVisible(false);
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        groupsPane.setVisible(false);
        schedulePane.setVisible(false);
        teachersPane.setVisible(true);
        studentsPane.setVisible(false);
    }

    private void showStudents() {
        loadCourses();
        applicationPane.setVisible(false);
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        groupsPane.setVisible(false);
        schedulePane.setVisible(false);
        teachersPane.setVisible(false);
        studentsPane.setVisible(true);
    }
    private void handleDeleteCourse() {
        Course selectedCourse = coursesTable.getSelectionModel().getSelectedItem(); // Получаем выбранный курс
        if (selectedCourse == null) {
            showAlert("Ошибка", "Выберите курс для удаления.", Alert.AlertType.ERROR);
            return;
        }

        // Формируем запрос на удаление
        Request request = new Request();
        request.setRequestType(RequestType.DeleteCourse); // Указываем тип запроса
        request.setMessage(String.valueOf(selectedCourse.getId())); // Передаем ID курса

        new Thread(() -> {
            try {
                ClientSocket.getInstance().getWriter().println(gson.toJson(request)); // Отправляем запрос
                ClientSocket.getInstance().getWriter().flush();

                // Чтение ответа от сервера
                String responseLine = ClientSocket.getReader().readLine();
                Response response = gson.fromJson(responseLine, Response.class);

                // Обработка ответа
                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        showAlert("Успех", "Курс успешно удалён.", Alert.AlertType.INFORMATION);
                        coursesTable.getItems().remove(selectedCourse); // Удаляем курс из таблицы
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
   private void handleDeleteSchedule(){
        Schedule selectedSchedule = schedulesTable.getSelectionModel().getSelectedItem();
        if (selectedSchedule == null) {
            showAlert("Ошибка", "Выберите расписание для удаления.", Alert.AlertType.ERROR);
            return;
        }
       // Формируем запрос на удаление
       Request request = new Request();
       request.setRequestType(RequestType.DeleteSchedule); // Указываем тип запроса
       request.setMessage(String.valueOf(selectedSchedule.getId()));

       new Thread(() -> {
           try {
               ClientSocket.getInstance().getWriter().println(gson.toJson(request)); // Отправляем запрос
               ClientSocket.getInstance().getWriter().flush();

               // Чтение ответа от сервера
               String responseLine = ClientSocket.getReader().readLine();
               Response response = gson.fromJson(responseLine, Response.class);

               // Обработка ответа
               Platform.runLater(() -> {
                   if (response.getSuccess()) {
                       showAlert("Успех", "Расписание успешно удалён.", Alert.AlertType.INFORMATION);
                       schedulesTable.getItems().remove(selectedSchedule); // Удаляем курс из таблицы
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
    private void handleDeleteGroup(){
        Group selectedGroup = groupsTable.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showAlert("Ошибка", "Выберите группу для удаления.", Alert.AlertType.ERROR);
            return;
        }
        // Формируем запрос на удаление
        Request request = new Request();
        request.setRequestType(RequestType.DeleteGroup); // Указываем тип запроса
        request.setMessage(String.valueOf(selectedGroup.getId()));

        new Thread(() -> {
            try {
                ClientSocket.getInstance().getWriter().println(gson.toJson(request)); // Отправляем запрос
                ClientSocket.getInstance().getWriter().flush();

                // Чтение ответа от сервера
                String responseLine = ClientSocket.getReader().readLine();
                Response response = gson.fromJson(responseLine, Response.class);

                // Обработка ответа
                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        showAlert("Успех", "Группа успешно удалён.", Alert.AlertType.INFORMATION);
                        groupsTable.getItems().remove(selectedGroup); // Удаляем курс из таблицы
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
    private void handleDeleteTeacher(){
        Teacher selectedTeacher = teachersTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            showAlert("Ошибка", "Выберите преподавателя для удаления.", Alert.AlertType.ERROR);
            return;
        }
        // Формируем запрос на удаление
        Request request = new Request();
        request.setRequestType(RequestType.DeleteTeacher); // Указываем тип запроса
        request.setMessage(String.valueOf(selectedTeacher.getId()));

        new Thread(() -> {
            try {
                ClientSocket.getInstance().getWriter().println(gson.toJson(request)); // Отправляем запрос
                ClientSocket.getInstance().getWriter().flush();

                // Чтение ответа от сервера
                String responseLine = ClientSocket.getReader().readLine();
                Response response = gson.fromJson(responseLine, Response.class);

                // Обработка ответа
                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        showAlert("Успех", "Преподаватель успешно удалён.", Alert.AlertType.INFORMATION);
                       teachersTable.getItems().remove(selectedTeacher); // Удаляем курс из таблицы
                    } else {
                        showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Ошибка", "Не удалось связаться с сервером.", Alert.AlertType.ERROR));
                e.printStackTrace();
            }
        }).start();
    } private void handleDeleteStudent(){
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("Ошибка", "Выберите студента для удаления.", Alert.AlertType.ERROR);
            return;
        }
        // Формируем запрос на удаление
        Request request = new Request();
        request.setRequestType(RequestType.DeleteStudent); // Указываем тип запроса
        request.setMessage(String.valueOf(selectedStudent.getId()));

        new Thread(() -> {
            try {
                ClientSocket.getInstance().getWriter().println(gson.toJson(request)); // Отправляем запрос
                ClientSocket.getInstance().getWriter().flush();

                // Чтение ответа от сервера
                String responseLine = ClientSocket.getReader().readLine();
                Response response = gson.fromJson(responseLine, Response.class);

                // Обработка ответа
                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        showAlert("Успех", "Студент успешно удалён.", Alert.AlertType.INFORMATION);
                        studentsTable.getItems().remove(selectedStudent); // Удаляем курс из таблицы
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
   private void handleDeleteFeedback() {
       Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem(); // Получаем выбранный курс
       if (selectedFeedback == null) {
           showAlert("Ошибка", "Выберите отзыв для удаления.", Alert.AlertType.ERROR);
           return;
       }

       // Формируем запрос на удаление
       Request request = new Request();
       request.setRequestType(RequestType.DeleteFeedback); // Указываем тип запроса
       request.setMessage(String.valueOf(selectedFeedback.getId()));

       new Thread(() -> {
           try {
               ClientSocket.getInstance().getWriter().println(gson.toJson(request)); // Отправляем запрос
               ClientSocket.getInstance().getWriter().flush();

               // Чтение ответа от сервера
               String responseLine = ClientSocket.getReader().readLine();
               Response response = gson.fromJson(responseLine, Response.class);

               // Обработка ответа
               Platform.runLater(() -> {
                   if (response.getSuccess()) {
                       showAlert("Успех", "Отзыв успешно удалён.", Alert.AlertType.INFORMATION);
                       feedbackTable.getItems().remove(selectedFeedback); // Удаляем курс из таблицы
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
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void loadCourses() {
        try {
            // Создаем запрос для получения списка курсов и прайс-листов
            Request request = new Request();
            request.setRequestType(RequestType.MenuAdmin);

            // Отправка запроса на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Чтение ответа от сервера
            String responseLine = ClientSocket.getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);


            if (response.getSuccess()) {
                // Разделение сообщения на курсы и прайс-листы
                String[] parts = response.getMessage().split(";");
                String coursesJson = parts[0];
                String priceListsJson = parts[1];

                // Преобразование JSON-строки в список курсов
                Type courseListType = new TypeToken<List<Course>>() {}.getType();
                List<Course> courses = gson.fromJson(coursesJson, courseListType);

                // Преобразование JSON-строки в список прайс-листов
                Type priceListType = new TypeToken<List<Price_list>>() {}.getType();
                List<Price_list> priceLists = gson.fromJson(priceListsJson, priceListType);

                // Загрузка списка курсов в таблицу
                ObservableList<Course> courseObservableList = FXCollections.observableArrayList(courses);
                coursesTable.setItems(courseObservableList);

                // Загрузка прайс-листов в таблицу с названием курса
                ObservableList<Price_list> priceListObservableList = FXCollections.observableArrayList(priceLists);
                priceTable.setItems(priceListObservableList);
                String feedbackJson = parts[2]; // Предположим, отзывы — третья часть сообщения
                Type feedbackListType = new TypeToken<List<Feedback>>() {}.getType();
                List<Feedback> feedbacks = gson.fromJson(feedbackJson, feedbackListType);

                ObservableList<Feedback> feedbackObservableList = FXCollections.observableArrayList(feedbacks);
                feedbackTable.setItems(feedbackObservableList);

                String groupsJson = parts[3];
                Type groupListType = new TypeToken<List<Group>>() {}.getType();
                List<Group> groups = gson.fromJson(groupsJson, groupListType);
                ObservableList<Group> groupObservableList = FXCollections.observableArrayList(groups);
                groupsTable.setItems(groupObservableList);
                String schedulesJson = parts[4];
                Type scheduleListType = new TypeToken<List<Schedule>>() {}.getType();
                List<Schedule> schedules = gson.fromJson(schedulesJson, scheduleListType);
                ObservableList<Schedule> scheduleObservableList = FXCollections.observableArrayList(schedules);
                schedulesTable.setItems(scheduleObservableList);
                String teachersJson = parts[5];
                Type teacherListType = new TypeToken<List<Teacher>>() {}.getType();
                List<Teacher> teachers = gson.fromJson(teachersJson, teacherListType);
                ObservableList<Teacher> teacherObservableList = FXCollections.observableArrayList(teachers);
                teachersTable.setItems(teacherObservableList);
                String studentsJson = parts[6];
                Type studentListType = new TypeToken<List<Student>>() {}.getType();
                List<Student> students = gson.fromJson(studentsJson, studentListType);
                ObservableList<Student> studentObservableList = FXCollections.observableArrayList(students);
                studentsTable.setItems(studentObservableList);



            } else {
                System.out.println("Ошибка при загрузке курсов: " + response.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }





    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AdminMenu.class.getResource("/view/admin-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Меню администратора");
        stage.setScene(scene);
        stage.show();
    }


}
