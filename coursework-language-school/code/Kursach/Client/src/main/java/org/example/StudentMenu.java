package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.Entities.Course;
import org.example.Entities.Feedback;
import org.example.Entities.Schedule;
import org.example.Server.Entities.Price_list;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class StudentMenu extends Application {
    @FXML private AnchorPane coursesPane;
    @FXML private AnchorPane pricePane;
    @FXML private AnchorPane reviewsPane;
    @FXML private AnchorPane schedulePane;
    @FXML private Accordion accordion;
    @FXML private Button exitButton;
    @FXML private Button testButton;
    @FXML private Button reviewButton;
    @FXML private TableView<Course> coursesTable;
    @FXML private TableView<Price_list> priceTable;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Price_list, String> courseNameColumn1;
    @FXML private TableColumn<Course, String> courseLevelColumn;
    @FXML private TableColumn<Price_list, String> coursePriceColumn;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableView<Schedule> scheduleTable;
    @FXML private TableColumn<Schedule, String> scheduleTimeColumn;
    @FXML private TableColumn<Schedule, String> scheduleDateColumn;
    @FXML private TableColumn<Schedule, String> scheduleRoomColumn;
    @FXML private TableColumn<Schedule, String> scheduleGroupColumn;
    @FXML private TableColumn<Feedback, String> usernameColumn;
    @FXML private TableColumn<Feedback, String> reviewColumn;
    private Gson gson = new Gson();
    @FXML
    public void initialize() {
        if (accordion != null) {
            // Скрыть все панели по умолчанию
            hideAllPanes();

            // Добавление обработчиков для раскрытия панелей
            for (TitledPane pane : accordion.getPanes()) {
                pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        hideAllPanes();
                        switch (pane.getText()) {

                            case "Курсы" -> showCourses();
                            case "Прайс-лист" -> showPrices();
                            case "Отзывы" -> showReviews();
                            case "Расписание" -> showSchedules();
                        }
                    }
                });
            }
            exitButton.setOnAction(event -> closeApplication());
            testButton.setOnAction(event -> openEnglishTest());
            reviewButton.setOnAction(event->openAddFeedback());
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

        } else {
            System.err.println("Ошибка: Accordion не инициализирован");
        }
    }
    private void closeApplication() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    private void openEnglishTest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/english-test.fxml"));
            AnchorPane TestPane = loader.load();
            Scene scene = new Scene(TestPane);
            Stage stage = (Stage) testButton.getScene().getWindow();
            stage.setTitle("Тест");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private void loadCourses() {
        try {
            // Создаем запрос для получения списка курсов и прайс-листов
            Request request = new Request();
            request.setRequestType(RequestType.StudentMenu);

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
                String scheduleJson = parts[3];
                Type scheduleListType = new TypeToken<List<Schedule>>() {}.getType();
                List<Schedule> schedules = gson.fromJson(scheduleJson, scheduleListType);
                ObservableList<Schedule> scheduleObservableList = FXCollections.observableArrayList(schedules);
                scheduleTable.setItems(scheduleObservableList);


            } else {
                System.out.println("Ошибка при загрузке курсов: " + response.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных с сервера: " + e.getMessage());
        }
    }

    private void hideAllPanes() {
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        schedulePane.setVisible(false);
    }
    public void showCourses() {
        loadCourses();
        coursesPane.setVisible(true);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        schedulePane.setVisible(false);
    }

    // Метод для отображения прайс-листа
    public void showPrices() {
        loadCourses();
        coursesPane.setVisible(false);
        pricePane.setVisible(true);
        reviewsPane.setVisible(false);
        schedulePane.setVisible(false);
    }

    // Метод для отображения отзывов
    public void showReviews() {
        loadCourses();
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(true);
        schedulePane.setVisible(false);
    }

    // Метод для отображения формы заявки
    public void showSchedules() {
        loadCourses();
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        schedulePane.setVisible(true);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AdminMenu.class.getResource("/view/student-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Меню студента");
        stage.setScene(scene);
        stage.show();
    }
}
