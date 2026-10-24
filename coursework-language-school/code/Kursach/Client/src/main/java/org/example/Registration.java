package org.example;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.example.Entities.Profile;
import org.example.Entities.Role;
import org.example.Entities.User;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;
import org.example.Utility.GsonConfig;


import java.io.IOException;

public class Registration extends Application {
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private TextField textfieldUsername;
    @FXML private PasswordField passwordfieldPassword;
    @FXML private TextField textfieldName;
    @FXML private TextField textfieldLastName;
    @FXML private TextField textfieldPhonenumber;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Registration.class.getResource("/view/register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Регистрация");
        stage.setScene(scene);
        stage.show();
    }
    public void loginButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
    public void registerButton_Pressed(ActionEvent actionEvent) throws IOException {
        User user = new User();
        Gson gson = GsonConfig.createGson();
        user.setUsername(textfieldUsername.getText());
        user.setPassword(passwordfieldPassword.getText());

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setName(textfieldName.getText());
        profile.setLastname(textfieldLastName.getText());
        profile.setPhonenumber(textfieldPhonenumber.getText());
        Request request = new Request();
        request.setRequestType(RequestType.REGISTER);
        request.setMessage(new Gson().toJson(profile));


        ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
        ClientSocket.getInstance().getWriter().flush();
        try {
            String responseLine = ClientSocket.getReader().readLine();
            Response response = new Gson().fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());

            if (response.getSuccess()) {


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ответ от сервера");
                alert.setHeaderText(null);
                alert.setContentText("Регистрация выполнена успешно");
                alert.showAndWait();

                Stage stage = (Stage) registerButton.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/view/login-view.fxml"));
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
        }
    }
    public static void main(String[] args) {launch();}
}
