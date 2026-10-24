package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.Entities.Profile;
import org.example.Entities.Type_application;
import org.example.TCP.CurrentSession;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;



import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class StudyApplication extends Application {

    @FXML
    private ChoiceBox<String> languageChoiceBox;
    @FXML
    private Button submitButton;
    @FXML
    private Button backButton;
    private Gson gson = new Gson();
    private boolean languagesLoaded = false;
    private List<Language> languages; // Хранение загруженных языков

    @FXML
    private TitledPane titledPane;



    @Override
    public void start(Stage stage) throws Exception {
        // Здесь реализуется запуск приложения, если нужно
    }

    static class Language {
        private int id;
        private String languageType;

        public String getLanguageType() {
            return languageType;
        }

        @Override
        public String toString() {
            return languageType;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> handleBackButtonAction());

        titledPane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                loadLanguagesIfNeeded(); // Загружаем языки при раскрытии панели
            }
        });
    }

    private void handleBackButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-menu.fxml"));
            AnchorPane trainingApplicationPane = loader.load();
            Scene scene = new Scene(trainingApplicationPane);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Меню пользователя");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLanguagesIfNeeded() {
        if (!languagesLoaded) {
            loadLanguages();
        }
    }

    private void loadLanguages() {
        // Создаем фоновую задачу для загрузки языков
        javafx.concurrent.Task<Void> loadLanguagesTask = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    System.out.println("Отправляем запрос на сервер...");
                    Request request = new Request();
                    request.setRequestType(RequestType.StudyApplication);

                    // Отправляем запрос на сервер
                    ClientSocket.getInstance().getWriter().println(gson.toJson(request));
                    ClientSocket.getInstance().getWriter().flush();
                    System.out.println("Запрос отправлен на сервер");

                    // Читаем ответ от сервера с тайм-аутом
                    String responseLine = null;
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < 5000) { // Ожидание 5 секунд
                        if (ClientSocket.getReader().ready()) {
                            responseLine = ClientSocket.getReader().readLine();
                            break;
                        }
                    }

                    if (responseLine == null) {
                        System.out.println("Таймаут: не удалось получить ответ от сервера.");
                        return null;
                    }

                    // Отладочный вывод
                    System.out.println("Ответ от сервера: " + responseLine);

                    // Обрабатываем полученный ответ
                    Response response = gson.fromJson(responseLine, Response.class);

                    if (response.getSuccess()) {
                        // Получаем строку JSON с языками
                        String languagesJson = response.getMessage();
                        Type languageListType = new TypeToken<List<Language>>() {}.getType(); // Список объектов Language
                        languages = gson.fromJson(languagesJson, languageListType);

                        System.out.println("Языки получены: " + languages); // Вывод полученных языков

                        // Извлекаем только languageType для отображения в ChoiceBox
                        ObservableList<String> languageObservableList = FXCollections.observableArrayList();
                        for (Language language : languages) {
                            languageObservableList.add(language.getLanguageType());
                        }

                        // Обновляем ChoiceBox в главном потоке
                        Platform.runLater(() -> {
                            languageChoiceBox.setItems(languageObservableList);

                            // Устанавливаем флаг, чтобы избежать повторной загрузки языков
                            languagesLoaded = true;
                        });
                    } else {
                        System.out.println("Ошибка при загрузке языков: " + response.getMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка получения данных с сервера: " + e.getMessage());
                }
                return null;
            }
        };

        // Запускаем задачу в фоновом потоке
        new Thread(loadLanguagesTask).start();
    }

    private int getLanguageIdByType(String languageType) {
        if (languages == null || languages.isEmpty()) {
            throw new IllegalStateException("Языки еще не загружены.");
        }

        for (Language language : languages) {
            if (language.getLanguageType().equals(languageType)) {
                return language.getId();
            }
        }
        throw new IllegalArgumentException("Язык не найден: " + languageType);
    }

    public void onSubmitApplication() {
        if (!languagesLoaded) {
            System.out.println("Языки еще не загружены.");
            return;
        }
        String selectedLanguage = languageChoiceBox.getValue();
        if (selectedLanguage == null) {
            System.out.println("Выберите язык.");
            return;
        }

        // Получаем ID языка
        int languageId = getLanguageIdByType(selectedLanguage);
        System.out.println(languageId);

        // Получаем currentProfileId из сессии
        int currentProfileId = CurrentSession.getInstance().getCurrentProfileId();
        System.out.println(currentProfileId);


        Profile profile = new Profile(); // Заполните этот объект нужными данными
        profile.setId(currentProfileId);

        Type_application typeApplication = new Type_application(); // Заполните этот объект нужными данными
        typeApplication.setId(1); // Например, ID типа заявки

        org.example.Entities.Language language = new org.example.Entities.Language(); // Заполните этот объект нужными данными
        language.setId(languageId);
        System.out.println(languageId);

        // Создаем объект заявки
        org.example.Entities.Application application = new org.example.Entities.Application();
        application.setProfile(profile); // Заполняем профиль
        application.setType_application(typeApplication); // Заполняем тип заявки
        application.setLanguage(language); // Заполняем язык
        application.setStatus("pending"); // Статус заявки
        System.out.println(application);
        // Формируем запрос
        Request request = new Request();
        request.setRequestType(RequestType.SubmitApplicationStudy);
        System.out.println(request);// Убедитесь, что этот тип соответствует типу на сервере
        request.setMessage(new Gson().toJson(application));
        // Отправка запроса на сервер
        new Thread(() -> {
            try {
                // Отправляем запрос
                ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
                ClientSocket.getInstance().getWriter().flush();
                System.out.println("данные отправлены");

                // Получаем ответ от сервера
                String responseLine = ClientSocket.getReader().readLine();
                Response response = new Gson().fromJson(responseLine, Response.class);

                Platform.runLater(() -> {
                    // Проверяем успех отправки заявки
                    if (response.getSuccess()) {
                        showAlert("Заявка отправлена успешно!", "Успех");
                    } else {
                        showAlert("Ошибка отправки заявки: " + response.getMessage(), "Ошибка");
                    }
                });
            } catch (IOException e) {
                System.out.println("Ошибка при отправке заявки: " + e.getMessage());
            }
        }).start();
    }

    private void showAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); // Устанавливаем заголовок окна
        alert.setHeaderText(null); // Можно установить заголовок, но мы оставим его пустым
        alert.setContentText(message); // Устанавливаем текст сообщения

        // Показываем диалоговое окно
        alert.showAndWait();
    }
}
