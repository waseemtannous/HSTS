package com.theDreamTeam.client;

import com.theDreamTeam.entities.*;
import javafx.application.Platform;

import java.io.IOException;

public class LoginController {

    public static LoginBoundary loginBoundary = new LoginBoundary();

    public void logIn(User user) {
        Message message = new Message(Message.logIn, user);
        App.client.sendMessageToServer(message);
    }

    public void loginSuccessful(User user) {
        App.user = user;
        LoginBoundary.stage.close();
        App.activityMain.showMainScreen();
    }

    public void alreadyConnected() {
        LoginBoundary.invalidText.setText("User is already connected to the system.");
    }

    public void invalidInput() {
        LoginBoundary.invalidText.setText("Invalid Input.");
    }

    public void logout() {
        Message message = new Message(Message.logout);
        Platform.runLater(() -> {
            App.stage.close();
            loginBoundary.loginScreen();
        });
        App.client.sendMessageToServer(message);
    }

}
