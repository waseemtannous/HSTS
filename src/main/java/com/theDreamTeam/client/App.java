package com.theDreamTeam.client;

import com.theDreamTeam.entities.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;


public class App extends Application {

    public static int vSize = 800;

    public static int hSize = 1500;

    public static Stage stage;

    public static Scene scene;

    public static BorderPane mainScreen;

    public static Client client;

    public static User user;

    public static ActivityMain activityMain = new ActivityMain();

    public static LoginBoundary loginBoundary = new LoginBoundary();

    public static LoginController loginController = new LoginController();

    @Override
    public void start(Stage stage) {
        String host = "localhost";
        int port = 6666;
        client = new Client(host, port);
        try{
            client.openConnection();
            loginBoundary.loginScreen();
        } catch (IOException e) {
            ActivityMain.errorHandle("Cannot Connect To Server.");
        }
    }

    public static void setStage(Stage stage) {
        App.stage = stage;
//        App.stage.setMaximized(true);
        App.stage.initStyle(StageStyle.UNDECORATED);
        stage.setOnCloseRequest((e) -> loginController.logout());
    }

    public static void closeApp() {
        if (client != null && client.isConnected()) {
            try {
                client.closeConnection();
            } catch (IOException e) {
                // todo change text
                ActivityMain.errorHandle("Error");
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

}