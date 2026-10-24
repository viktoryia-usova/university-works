package org.example;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Entities.Profile;
import org.example.Entities.Role;
import org.example.Entities.Student;
import org.example.Entities.User;
import org.example.TCP.CurrentSession;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import com.google.gson.Gson;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import org.example.Utility.GsonConfig;

import java.io.IOException;
import java.util.Objects;


public class Login extends Application {
    @FXML private TextField textfieldUsername;
    @FXML private PasswordField passwordfieldPassword;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label invalidDataLabel;


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Login.class.getResource("/view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Авторизация");
        stage.setScene(scene);
        stage.show();
    }

    public void loginButton_Pressed(ActionEvent actionEvent) {
        String login = textfieldUsername.getText();
        String password = passwordfieldPassword.getText();
        Gson gson = GsonConfig.createGson();
        // Формируем запрос
        Request loginRequest = new Request();
        loginRequest.setRequestType(RequestType.LOGIN);
        User user = new User();
        user.setUsername(login);
        user.setPassword(password);
        loginRequest.setMessage(new Gson().toJson(user));


        ClientSocket.getInstance().getWriter().println(new Gson().toJson(loginRequest));
        ClientSocket.getInstance().getWriter().flush();

        // Читаем ответ от сервера
        try {
            String responseLine = ClientSocket.getReader().readLine();
            Response response = new Gson().fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());

            if (response.getSuccess()) {
                System.out.println("JSON для десериализации: " + response.getMessage());
                Profile profile = gson.fromJson(response.getMessage(), Profile.class);
                System.out.println("Десериализованный profile: " + profile);
                System.out.println("ID профиля: " + profile.getId());

                System.out.println(profile.getId());
                System.out.println("Сохранённый currentProfileId до установки: " + CurrentSession.getInstance().getCurrentProfileId());
                CurrentSession.getInstance().setCurrentProfileId(profile.getId());

                System.out.println("Сохранённый currentProfileId: " + CurrentSession.getInstance().getCurrentProfileId());
                Role role= profile.getUser().getRole();
                System.out.println(role.getRoleType());
                CurrentSession.getInstance().setCurrentRole(role.getRoleType());  // или "teacher", в зависимости от пользователя
                if (role.getRoleType().equals("STUDENT")){
                    Student student =new Student();
                    student.setProfile(profile);
                    CurrentSession.getInstance().setCurrentStudentId(student.getId());
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ответ от сервера");
                alert.setHeaderText(null);
                alert.setContentText("Вход выполнен успешно");
                alert.showAndWait();

                Stage stage = (Stage) loginButton.getScene().getWindow();
                Parent root;
                if ("user".equalsIgnoreCase(role.getRoleType())) {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user-menu.fxml")));
                } else if ("admin".equalsIgnoreCase(role.getRoleType())) {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/admin-menu.fxml")));
                } else if ("student".equalsIgnoreCase(role.getRoleType())) {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/student-menu.fxml")));}
                else if ("teacher".equalsIgnoreCase(role.getRoleType())) {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/teacher-menu.fxml")));}
                    else {
                    Alert roleAlert = new Alert(Alert.AlertType.ERROR);
                    roleAlert.setTitle("Ошибка");
                    roleAlert.setHeaderText(null);
                    roleAlert.setContentText("Неизвестная роль пользователя.");
                    roleAlert.showAndWait();
                    return;
                }
                Scene newScene = new Scene(root);
                stage.setScene(newScene);

            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ответ от сервера");
                alert.setHeaderText(null);
                alert.setContentText("Ошибка: " + response.getMessage());
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка при получении ответа от сервера: " + e.getMessage());
            alert.showAndWait();
        }}

    public void registerButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register-view.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
    public static void main(String[] args) {
        launch();
    }
}