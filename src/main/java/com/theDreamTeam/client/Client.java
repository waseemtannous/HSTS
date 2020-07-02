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

//    private static final Logger LOGGER =
//            Logger.getLogger(Client.class.getName());

    public Client(String host, int port) {
        super(host, port);
    }

    @Override
    protected void connectionEstablished() {
        // TODO Auto-generated method stub
        super.connectionEstablished();
        LOGGER.info("Connected to server.");
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        System.out.println("msg received from server");
//        System.out.println(((User)(((Message) msg).getObject())).getUsername());

        Message message = (Message) msg;

        switch (message.getMsg()) {
            case Message.receiveExamByCode:
                System.out.println("receiveExamByCode");
                Platform.runLater(() -> examController.receiveExamByCode((ExecutableExam)message.getObject()));
                break;

            case Message.invalidExamCode :
                System.out.println("invalidExamCode");
                Platform.runLater(() -> examController.invalidCode());
                break;

            case Message.invalidStudent :
                System.out.println("invalidStudent");
                Platform.runLater(() -> examController.invalidStudent());
                break;

            case Message.receiveCoursesForReports :
                System.out.println("receiveCoursesForReports");
                Platform.runLater(() -> examController.receiveCoursesForReports((List<Course>) message.getObject()));
                break;

            case Message.logInSuccessful:
                System.out.println("logInSuccessful");
                Platform.runLater(() -> loginController.loginSuccessful((User) message.getObject()));
                break;

            case Message.logInFailed :
                System.out.println("logInFailed");
                Platform.runLater(() -> loginController.invalidInput());
                break;

            case Message.userAlreadyConnected :
                System.out.println("userAlreadyConnected");
                Platform.runLater(() -> loginController.alreadyConnected());
                break;

            case Message.extendTimeRequestAnswer :
                System.out.println("extendTimeRequestAnswer");
                Platform.runLater(() -> examController.extendTime((ExtendTimeRequest) message.getObject()));
                break;

            case Message.receiveExamsCopies :
                System.out.println("receiveExamsCopies");
                Platform.runLater(() -> examBoundary.showExamsCopies((List<Pair<ExamCopy, List<Answer>>>) message.getObject()));
                break;

            case Message.receiveCoursesForQuestionsDrawer:
                System.out.println("receiveCoursesForQuestionsDrawer");
                Platform.runLater(() -> questionBoundary.receiveCoursesForQuestionsDrawer((List<Course>) message.getObject()));
                break;

            case Message.receiveQuestionsDrawer :
                System.out.println("receiveQuestionsDrawer");
                Platform.runLater(() -> questionBoundary.receiveQuestionsDrawer((List<Question>)message.getObject()));
                break;

            case Message.receiveExamsDrawer :
                System.out.println("receiveExamsDrawer");
                Platform.runLater(() -> examBoundary.receiveExamsDrawer((List<Exam>) message.getObject()));
                break;

            case Message.receiveCoursesForExamsDrawer :
                System.out.println("receiveCoursesForExamsDrawer");
                Platform.runLater(() -> examBoundary.receiveCoursesForExamsDrawer((List<Course>) message.getObject()));
                break;

            case Message.receiveCoursesForExamsCopies :
                System.out.println("receiveCoursesForExamsCopies");
                Platform.runLater(() -> examBoundary.receiveCoursesForExamsCopies((List<Course>) message.getObject()));
                break;

            case Message.receiveExtendTimeRequests :
                System.out.println("receiveExtendTimeRequests");
                Platform.runLater(() -> activityMain.receiveExtendTimeRequests((List<ExtendTimeRequest>) message.getObject()));
                break;

            case Message.receiveExamsToCheck :
                System.out.println("receiveExamsToCheck");
                Platform.runLater(() -> examBoundary.receiveExamsToCheck((List<ExamCopy>) message.getObject()));
                break;

            case Message.extraTime :
                System.out.println("extraTime");
                Platform.runLater(() -> examController.extendTime((ExtendTimeRequest) message.getObject()));
                break;

            case Message.receiveCoursesForExamsCheck :
                System.out.println("receiveCoursesForExamsCheck");
                Platform.runLater(() -> examBoundary.receiveCoursesForExamsCheck((List<Course>) message.getObject()));
                break;

            case Message.receiveCoursesForStats :
                System.out.println("receiveCoursesForStats");
                Platform.runLater(() -> statisticsBoundary.receiveCourses((List<Course>) message.getObject()));
                break;

            case Message.receiveStats :
                System.out.println("receiveStats");
                Platform.runLater(() -> statisticsBoundary.receiveStats((List<Statistics>) message.getObject()));
                break;

            case Message.extraTimeRequestFromTeacher :
                System.out.println("extraTimeRequestFromTeacher");
                Platform.runLater(() -> {
                    if ((boolean) message.getObject())
                        App.mainScreen.setCenter(ActivityMain.welcome);
                    else
                        ActivityMain.errorHandle("Invalid Request");
                });

        }
    }

    @Override
    protected void connectionClosed() {
        // TODO Auto-generated method stub
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
