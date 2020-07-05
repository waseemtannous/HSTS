package com.theDreamTeam.client;

import com.theDreamTeam.entities.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;


public class LoginBoundary {

    public static LoginController loginController = new LoginController();

    public static Text invalidText = new Text();

    public static Stage stage;

    public void loginScreen() {
        stage = new Stage();
        stage.setTitle("HSTS - Login");
        stage.setResizable(false);
        stage.setOnCloseRequest((e) -> App.closeApp());
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);

        String imgUrl = null;
        try {
            imgUrl = (new File(System.getProperty("user.dir") + "/logo2.png")).toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assert imgUrl != null;
        ImageView logo = new ImageView(imgUrl);
        logo.setPreserveRatio(true);
        logo.setFitHeight(100);
        logo.setFitWidth(100);

        Button loginBtn = new Button();

        Label userNameLabel = new Label("Username");
        TextField nameInput = new TextField();
        nameInput.setPromptText("User name");
        HBox username = new HBox(15);
        username.setAlignment(Pos.CENTER);

        username.getChildren().addAll(userNameLabel, nameInput);

        Label passwordLabel = new Label("Password");
        TextField passInput = new PasswordField();
        passInput.setPromptText("password");
        HBox password = new HBox(15);
        password.setAlignment(Pos.CENTER);
        password.getChildren().addAll(passwordLabel, passInput);

        loginBtn.setText("Log in");
        loginBtn.setOnAction(e ->{
            String name = nameInput.getText();
            String pass = passInput.getText();
            loginController.logIn(new User(name, pass));
        });
        GridPane.setConstraints(loginBtn,1,2);

        invalidText.setText("");

        vbox.getChildren().addAll(logo, username, password, loginBtn, invalidText);
        Scene scene = new Scene(vbox, 600,300);
        try {
            scene.getStylesheets().add((new File(System.getProperty("user.dir") + "/stylesheet.css")).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            ActivityMain.errorHandle("An Error Has Occurred");
        }
        stage.setScene(scene);
        stage.show();
    }
}
