//package com.theDreamTeam.client;
//
//import com.theDreamTeam.entities.*;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class App extends Application {
//
//    public static final Client client = new Client("localhost", 6666);
//
//    @Override
//    public void start(Stage stage) throws Exception {
//
//        Button btn = new Button("send msg");
//        btn.setOnAction(e -> {
//            try {
//                Message message = new Message(Message.logIn, new User("Waseem Tannous", "waseem69"));
//                client.sendToServer(message);
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        });
//
//        VBox vbox = new VBox();
//        vbox.getChildren().add(btn);
//
//        Scene scene = new Scene(vbox, 200, 200);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        try {
//            client.openConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        launch(args);
//    }
//}

package com.theDreamTeam.client;

import com.theDreamTeam.entities.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class App extends Application {

    public static int vSize = 800;

    public static int hSize = 1500;

    public static Stage stage;

    public static Scene scene;

    public static BorderPane mainScreen;

    public static Text logo = new Text();

    public static Client client;

    public static User user;

    public static ActivityMain activityMain = new ActivityMain();

    public static LoginBoundary loginBoundary = new LoginBoundary();

    public static Teacher teacher;

    public static Course course;

    public static Exam exam;

    public static ExtendTimeRequest extendTimeRequest;

    public static LoginController loginController = new LoginController();

    public static List<ExamCopy> list = new ArrayList<>();

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