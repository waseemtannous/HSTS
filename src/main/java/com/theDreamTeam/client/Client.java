package com.theDreamTeam.client;

import com.theDreamTeam.entities.*;

import com.theDreamTeam.entities.Message;
import com.theDreamTeam.entities.User;
import com.theDreamTeam.ocsf.client.*;
import javafx.application.Platform;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class Client extends AbstractClient {

    public static LoginController loginController = new LoginController();

    public static QuestionBoundary questionBoundary = new QuestionBoundary();

    public static ExamBoundary examBoundary = new ExamBoundary();

    public static ActivityMain activityMain = new ActivityMain();

    public static StatisticsBoundary statisticsBoundary = new StatisticsBoundary();

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private static final ExamController examController = new ExamController();


    public Client(String host, int port) {
        super(host, port);
    }

    @Override
    protected void connectionEstablished() {
        super.connectionEstablished();
        LOGGER.info("Connected to server.");
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        Message message = (Message) msg;

        switch (message.getMsg()) {
            case receiveExamByCode:
                Platform.runLater(() -> examController.receiveExamByCode((ExecutableExam)message.getObject()));
                break;

            case invalidExamCode :
                Platform.runLater(examController::invalidCode);
                break;

            case invalidStudent :
                Platform.runLater(examController::invalidStudent);
                break;

            case receiveCoursesForReports :
                Platform.runLater(() -> examController.receiveCoursesForReports((List<Course>) message.getObject()));
                break;

            case logInSuccessful:
                Platform.runLater(() -> loginController.loginSuccessful((User) message.getObject()));
                break;

            case logInFailed :
                Platform.runLater(() -> loginController.invalidInput());
                break;

            case userAlreadyConnected :
                Platform.runLater(() -> loginController.alreadyConnected());
                break;

            case extendTimeRequestAnswer :
                Platform.runLater(() -> examController.extendTime((ExtendTimeRequest) message.getObject()));
                break;

            case receiveExamsCopies :
                Platform.runLater(() -> examBoundary.showExamsCopies((List<Pair<ExamCopy, List<Answer>>>) message.getObject()));
                break;

            case receiveCoursesForQuestionsDrawer:
                Platform.runLater(() -> questionBoundary.receiveCoursesForQuestionsDrawer((List<Course>) message.getObject()));
                break;

            case receiveQuestionsDrawer :
                Platform.runLater(() -> questionBoundary.receiveQuestionsDrawer((List<Question>)message.getObject()));
                break;

            case receiveExamsDrawer :
                Platform.runLater(() -> examBoundary.receiveExamsDrawer((List<Exam>) message.getObject()));
                break;

            case receiveCoursesForExamsDrawer :
                Platform.runLater(() -> examBoundary.receiveCoursesForExamsDrawer((List<Course>) message.getObject()));
                break;

            case receiveCoursesForExamsCopies :
                Platform.runLater(() -> examBoundary.receiveCoursesForExamsCopies((List<Course>) message.getObject()));
                break;

            case receiveExtendTimeRequests :
                Platform.runLater(() -> activityMain.receiveExtendTimeRequests((List<ExtendTimeRequest>) message.getObject()));
                break;

            case receiveExamsToCheck :
                Platform.runLater(() -> examBoundary.receiveExamsToCheck((List<ExamCopy>) message.getObject()));
                break;

            case extraTime :
                Platform.runLater(() -> examController.extendTime((ExtendTimeRequest) message.getObject()));
                break;

            case receiveCoursesForExamsCheck :
                Platform.runLater(() -> examBoundary.receiveCoursesForExamsCheck((List<Course>) message.getObject()));
                break;

            case receiveCoursesForStats :
                Platform.runLater(() -> statisticsBoundary.receiveCourses((List<Course>) message.getObject()));
                break;

            case receiveStats :
                Platform.runLater(() -> statisticsBoundary.receiveStats((List<Statistics>) message.getObject()));
                break;

            case wrongCode :
                Platform.runLater(() -> ActivityMain.errorHandle("Code Already Exists"));
                break;

            case extraTimeRequestFromTeacher :
                Platform.runLater(() -> {
                    if ((boolean) message.getObject())
                        App.mainScreen.setCenter(ActivityMain.welcome);
                    else
                        ActivityMain.errorHandle("Invalid Request");
                });
                break;

        }
    }

    @Override
    protected void connectionClosed() {
        super.connectionClosed();
        System.out.println("connection closed");
    }

    public void sendMessageToServer(Message message) {
        try {
            this.sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
