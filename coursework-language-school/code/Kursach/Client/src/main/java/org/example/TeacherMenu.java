package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class TeacherMenu extends Application {
    @FXML
    private Accordion accordion;
    @FXML private AnchorPane coursesPane;
    @FXML private AnchorPane pricePane;
    @FXML private AnchorPane reviewsPane;
    @FXML private AnchorPane schedulePane;
    @FXML private AnchorPane testsPane;
    @FXML private AnchorPane mygroupsPane;
    @FXML private TableView<Course> coursesTable;
    @FXML private TableView<Price_list> priceTable;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableView<Test> testsTable;
    @FXML private TableView<Schedule> schedulesTable;
    @FXML private TableView<Student> mygroupTable;

    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseLevelColumn;
    @FXML private TableColumn<Price_list, String> courseNameColumn1;
    @FXML private TableColumn<Price_list, String> coursePriceColumn;
    @FXML private TableColumn<Feedback, String> usernameColumn;
    @FXML private TableColumn<Feedback, String> reviewColumn;
    @FXML private TableColumn<Group, String> scheduleTimeColumn;
    @FXML private TableColumn<Group, String> scheduleDateColumn;
    @FXML private TableColumn<Group, String> scheduleRoomColumn;
    @FXML private TableColumn<Schedule, String> scheduleGroupColumn;
    @FXML private TableColumn<Test, String> LastnameColumn;
    @FXML private TableColumn<Test, String> resultColumn;
    @FXML private TableColumn<Test, String> levelColumn;
    @FXML private TableColumn<Student, String> groupMemberNameColumn;
    @FXML private TableColumn<Student, String> groupMemberLastnameColumn;
    @FXML private TableColumn<Student, String> groupMemberPhoneColumn;

    @FXML private ChoiceBox<Integer> groupChoiceBox;
    @FXML private Button exitButton;
    @FXML private Button reviewButton;

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
                            case "Курсы" -> showCourses();
                            case "Прайс-лист" -> showPrices();
                            case "Отзывы" -> showReviews();
                            case "Расписание" -> showSchedule();
                            case "Результаты тестов" -> showTests();
                            case "Мои группы" -> showMyGroups();
                        }
                    }
                });
            }
            groupChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Создаем фоновую задачу для загрузки студентов
                    Task<Void> task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            loadGroupMembers((Integer) newValue); // Передаем groupId
                            return null;
                        }
                    };

                    task.setOnSucceeded(event -> System.out.println("Данные успешно загружены"));
                    task.setOnFailed(event -> System.out.println("Ошибка загрузки данных: " + task.getException().getMessage()));

                    new Thread(task).start(); // Запуск задачи в фоновом потоке
                }
            });



            exitButton.setOnAction(event -> closeApplication());
            reviewButton.setOnAction(event -> openAddFeedback());
            courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("course_type"));
            courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("course_level"));
            courseNameColumn1.setCellValueFactory(new PropertyValueFactory<>("courseName"));
            coursePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
            reviewColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
            scheduleTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            scheduleDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            scheduleRoomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
            scheduleGroupColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            LastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
            resultColumn.setCellValueFactory(new PropertyValueFactory<>("Test_result"));
            levelColumn.setCellValueFactory(new PropertyValueFactory<>("language_skill"));
            groupMemberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            groupMemberLastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
            groupMemberPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));



        }
    }

    private void closeApplication() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void hideAllPanes() {
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        schedulePane.setVisible(false);
        testsPane.setVisible(false);
        mygroupsPane.setVisible(false);

    }


    private void showCourses() {
        loadCourses();
        coursesPane.setVisible(true);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
       schedulePane.setVisible(false);
        testsPane.setVisible(false);
        mygroupsPane.setVisible(false);

    }

    private void showPrices() {
        loadCourses();
        coursesPane.setVisible(false);
        pricePane.setVisible(true);
        reviewsPane.setVisible(false);
        schedulePane.setVisible(false);
        testsPane.setVisible(false);
        mygroupsPane.setVisible(false);

    }

    private void showReviews() {
        loadCourses();
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(true);
        schedulePane.setVisible(false);
        testsPane.setVisible(false);
        mygroupsPane.setVisible(false);

    }

    private void  showSchedule() {
        loadCourses();
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        schedulePane.setVisible(true);
        testsPane.setVisible(false);
        mygroupsPane.setVisible(false);

    }

    private void showTests() {
        loadCourses();
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        schedulePane.setVisible(false);
        testsPane.setVisible(true);
        mygroupsPane.setVisible(false);

    }

    private void showMyGroups() {
        loadGroups();
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        schedulePane.setVisible(false);
        testsPane.setVisible(false);
        mygroupsPane.setVisible(true);
    }

    private void openAddFeedback() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-feedback.fxml"));
            AnchorPane TestPane = loader.load();
            Scene scene = new Scene(TestPane);
            Stage stage = (Stage) reviewButton.getScene().getWindow();
            stage.setTitle("Оставить отзыв");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadGroups() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.GetGroups);

            // Отправка запроса на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Чтение ответа от сервера
            String responseLine = ClientSocket.getInstance().getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());


            if (response.getSuccess()) {
                String[] parts = response.getMessage().split(";");
                String groupIdsJson = parts[0];
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
    private void loadGroupMembers(int groupId) {
        // Создаем фоновую задачу для загрузки студентов
        javafx.concurrent.Task<Void> loadGroupMembersTask = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Создаем запрос на сервер для получения студентов группы
                    Request request = new Request();
                    request.setRequestType(RequestType.GetGroupMembers); // Тип запроса
                    request.setMessage(String.valueOf(groupId)); // Передаем groupId в запросе

                    // Отправка запроса на сервер
                    ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                    ClientSocket.getInstance().getWriter().flush();
                    System.out.println("запрос отправлен");

                    // Чтение ответа от сервера
                    String responseLine = ClientSocket.getInstance().getReader().readLine();
                    Response response = gson.fromJson(responseLine, Response.class);
                    System.out.println("читаем ответ");
                    System.out.println(response.getMessage());

                    if (response.getSuccess()) {
                        String[] parts = response.getMessage().split(";");
                        String studentsJson = parts[0];
                        Type studentListType = new TypeToken<List<Student>>() {}.getType();
                        List<Student> students = gson.fromJson(studentsJson, studentListType);

                        // Создаем ObservableList для отображения в TableView
                        ObservableList<Student> studentObservableList = FXCollections.observableArrayList(students);

                        // Обновляем TableView в главном потоке
                        Platform.runLater(() -> {
                            mygroupTable.setItems(studentObservableList);
                        });
                    } else {
                        System.out.println("Ошибка при загрузке студентов: " + response.getMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка связи с сервером: " + e.getMessage());
                }
                return null;
            }
        };

        // Запуск фонової задачи
        loadGroupMembersTask.setOnSucceeded(event -> System.out.println("Данные успешно загружены"));
        loadGroupMembersTask.setOnFailed(event -> System.out.println("Ошибка загрузки данных: " + loadGroupMembersTask.getException().getMessage()));

        new Thread(loadGroupMembersTask).start(); // Запуск задачи в фоновом потоке
    }


    private void loadCourses() {
        try {
            // Создаем запрос для получения списка курсов и прайс-листов
            Request request = new Request();
            request.setRequestType(RequestType.TeacherMenu);

            // Отправка запроса на сервер
            ClientSocket.getInstance().getWriter().println(gson.toJson(request));
            ClientSocket.getInstance().getWriter().flush();

            // Чтение ответа от сервера
            String responseLine = ClientSocket.getInstance().getReader().readLine();
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

                String schedulesJson = parts[4];

                Type scheduleListType = new TypeToken<List<Schedule>>() {}.getType();
                List<Schedule> schedules = gson.fromJson(schedulesJson, scheduleListType);
                ObservableList<Schedule> scheduleObservableList = FXCollections.observableArrayList(schedules);
                schedulesTable.setItems(scheduleObservableList);
                String testsJson = parts[5];
                Type testListType = new TypeToken<List<Test>>() {}.getType();
                List<Test> tests = gson.fromJson(testsJson, testListType);
                ObservableList<Test> testObservableList = FXCollections.observableArrayList(tests);
                testsTable.setItems(testObservableList);




            } else {
                System.out.println("Ошибка при загрузке курсов: " + response.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }





    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AdminMenu.class.getResource("/view/teacher-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Меню преподавателя");
        stage.setScene(scene);
        stage.show();
    }
}
