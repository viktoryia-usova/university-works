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
import org.example.Server.Entities.Price_list;
import org.example.TCP.RequestType;
import org.example.TCP.Request;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class UserMenu extends Application {

    @FXML private Button btnTraining;
    @FXML private Button btnTeaching;
    @FXML private AnchorPane coursesPane;
    @FXML private TableView<Course> coursesTable;
    @FXML private AnchorPane pricePane;
    @FXML private TableView<Price_list> priceTable;
    @FXML private AnchorPane reviewsPane;
    @FXML private AnchorPane applicationPane;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Price_list, String> courseNameColumn1;
    @FXML private TableColumn<Course, String> courseLevelColumn;
    @FXML private TableColumn<Price_list, String> coursePriceColumn;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> usernameColumn;
    @FXML private TableColumn<Feedback, String> reviewColumn;
    @FXML private Button exitButton;

    @FXML private Accordion accordion;
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
                            case "Оставить заявку" -> showApplicationForm();
                            case "Курсы" -> showCourses();
                            case "Прайс-лист" -> showPrices();
                            case "Отзывы" -> showReviews();
                        }
                    }
                });
            }
            exitButton.setOnAction(event -> closeApplication());
            btnTraining.setOnAction(event -> openTrainingApplicationPage());
            btnTeaching.setOnAction(event -> openTeachingApplicationPage());
            courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("course_type"));
            courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("course_level"));
            courseNameColumn1.setCellValueFactory(new PropertyValueFactory<>("courseName"));
            coursePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
            reviewColumn.setCellValueFactory(new PropertyValueFactory<>("text"));

        } else {
            System.err.println("Ошибка: Accordion не инициализирован");
        }
    }
    private void closeApplication() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    private void openTrainingApplicationPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/study-application.fxml"));
            AnchorPane trainingApplicationPane = loader.load();

            // Замените содержимое текущей сцены или добавьте trainingApplicationPane к родителю
            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) btnTraining.getScene().getWindow();
            stage.setTitle("Заявка на обучение");// Получение текущего окна
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openTeachingApplicationPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/teaching-application.fxml"));
            AnchorPane teachingApplicationPane = loader.load();

            // Замените содержимое текущей сцены или добавьте teachingApplicationPane к родителю
            Scene scene = new Scene(teachingApplicationPane);
            Stage stage = (Stage) btnTeaching.getScene().getWindow();
            stage.setTitle("Заявка на преподавание");
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
            request.setRequestType(RequestType.MenuUser);

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
        applicationPane.setVisible(false);
    }
    public void showCourses() {
        loadCourses();
        coursesPane.setVisible(true);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        applicationPane.setVisible(false);
    }

    // Метод для отображения прайс-листа
    public void showPrices() {
        loadCourses();
        coursesPane.setVisible(false);
        pricePane.setVisible(true);
        reviewsPane.setVisible(false);
        applicationPane.setVisible(false);
    }

    // Метод для отображения отзывов
    public void showReviews() {
        loadCourses();
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(true);
        applicationPane.setVisible(false);
    }

    // Метод для отображения формы заявки
    public void showApplicationForm() {
        coursesPane.setVisible(false);
        pricePane.setVisible(false);
        reviewsPane.setVisible(false);
        applicationPane.setVisible(true);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/view/user-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Меню пользователя");
        stage.setScene(scene);
        stage.show();
    }
}
