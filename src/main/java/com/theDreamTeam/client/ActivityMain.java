package com.theDreamTeam.client;

import com.theDreamTeam.entities.*;

//import com.theDreamTeam.ExamBoundary;
//import com.theDreamTeam.ExamController;
//import com.theDreamTeam.LoginController;
//import com.theDreamTeam.QuestionBoundary;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityMain {

    public static ExamController examController = new ExamController();

    public static ExamBoundary examBoundary = new ExamBoundary();

    public static QuestionController questionController = new QuestionController();

    public static QuestionBoundary questionBoundary = new QuestionBoundary();

    public static LoginController loginController = new LoginController();

    public static StatisticsBoundary statisticsBoundary = new StatisticsBoundary();

    public static final Text welcome = new Text();

    public static Button enterExam;

    public static Button myExamsButton;

    public static Button mainScreenBtn;

    public static Button logoutButton;

    // TODO: change greetings
    private static final String studentGreeting = "Welcome to HSTS.";

    private static final String managerGreeting = "Welcome to HSTS.";

    private static final String teacherGreeting = "Welcome to HSTS.";


    public void showMainScreen() {
        App.setStage(new Stage());
        if (App.user instanceof Student) {
            studentMainScreen();
        } else if (App.user instanceof Teacher) {
            teacherMainScreen();
        } else if (App.user instanceof Manager) {
            managerMainScreen();
        }
    }

    public void studentMainScreen() {
        App.mainScreen = new BorderPane();
        HBox topBar = createTopBar();

        VBox sideBar = new VBox(20);
        enterExam = new Button("Enter Exam");
        enterExam.setOnAction(e -> examBoundary.enterExamScreen());

        myExamsButton = new Button("Exams Copies");
        myExamsButton.setOnAction(e -> examBoundary.examsCopies());

        Button logoutButton = createLogoutButton();

        sideBar.getChildren().addAll(enterExam, myExamsButton, logoutButton);
        sideBar.setSpacing(50);
        sideBar.setAlignment(Pos.CENTER);

        welcome.setText(studentGreeting);

        App.mainScreen.setTop(topBar);
        App.mainScreen.setLeft(sideBar);
        App.mainScreen.setCenter(welcome);

        App.scene = new Scene(App.mainScreen, App.hSize, App.vSize );
        try {
            App.scene.getStylesheets().add((new File("test.css")).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            errorHandle("An Error Has Occurred");
        }
        App.stage.setScene(App.scene);
        App.stage.show();
    }

    public void teacherMainScreen() {
        App.mainScreen = new BorderPane();
        HBox topBar = createTopBar();

        VBox sideBar = new VBox(20);
        Button questionsDrawer = new Button("Questions Drawer");
        questionsDrawer.setOnAction(e -> questionBoundary.getQuestionsForDrawer());
        Button examsDrawer = new Button("Exams Drawer");
        examsDrawer.setOnAction(e -> examBoundary.showExamsDrawer());
        Button examsCopies = new Button("Exams Copies");
        examsCopies.setOnAction(e -> examBoundary.examsCopies());
        Button checkExams = new Button(" Check Exams");
        checkExams.setOnAction(e -> examBoundary.checkExams());
        Button stats = new Button("Statistics");
        stats.setOnAction(e -> statisticsBoundary.getStatistics());

        Button extraTime = new Button("Extend Exam Time");
        extraTime.setOnAction(e -> extendTimeForExam());

        Button logoutButton = createLogoutButton();

        sideBar.getChildren().addAll(questionsDrawer, examsDrawer, examsCopies, checkExams, stats, extraTime, logoutButton);
        sideBar.setSpacing(50);
        sideBar.setAlignment(Pos.CENTER);

        welcome.setText(teacherGreeting);

        App.mainScreen.setTop(topBar);
        App.mainScreen.setLeft(sideBar);
        App.mainScreen.setCenter(welcome);

        App.scene = new Scene(App.mainScreen, App.hSize, App.vSize );
//        try {
//            App.scene.getStylesheets().add((new File("test.css")).toURI().toURL().toExternalForm());
//        } catch (MalformedURLException e) {
//            errorHandle("An Error Has Occurred");
//        }
        App.stage.setScene(App.scene);
        App.stage.show();
    }

    public void managerMainScreen() {
        App.mainScreen = new BorderPane();
        HBox topBar = createTopBar();

        VBox sideBar = new VBox(20);

        Button questionsDrawer = new Button("Questions Drawer");
        questionsDrawer.setOnAction(e -> questionBoundary.getQuestionsForDrawer());
        Button examsDrawer = new Button("Exams Drawer");
        examsDrawer.setOnAction(e -> examBoundary.showExamsDrawer());
        Button reports = new Button("Exams Copies");
        reports.setOnAction(e -> examBoundary.examsCopies());
        Button extendTimeRequestsButton = new Button("Extend Time Requests");
        extendTimeRequestsButton.setOnAction(e -> {
            extendTimeRequests();
        });
        Button examsCopies = new Button("Exams Copies");
        examsCopies.setOnAction(e -> examBoundary.examsCopies());
        Button stats = new Button("Statistics");
        stats.setOnAction(e -> statisticsBoundary.getStatistics());

        Button logoutButton = createLogoutButton();

        sideBar.getChildren().addAll(questionsDrawer,  examsDrawer, extendTimeRequestsButton, reports, stats, logoutButton);
        sideBar.setSpacing(50);
        sideBar.setAlignment(Pos.CENTER);

        welcome.setText(managerGreeting);

        App.mainScreen.setTop(topBar);
        App.mainScreen.setLeft(sideBar);
        App.mainScreen.setCenter(welcome);

        App.scene = new Scene(App.mainScreen, App.hSize, App.vSize );
//        try {
//            App.scene.getStylesheets().add((new File("test.css")).toURI().toURL().toExternalForm());
//        } catch (MalformedURLException e) {
//            errorHandle("An Error Has Occurred");
//        }
        App.stage.setScene(App.scene);
        App.stage.show();
    }

    public HBox createTopBar() {
        HBox topBar = new HBox(20);
        Text greetingText = new Text("Hello " + App.user.getUsername());
        App.logo.setText("LOGO");
        mainScreenBtn = new Button("Home");
        mainScreenBtn.setOnAction(e -> App.mainScreen.setCenter(welcome));
//        String imgUrl = getImageURL("logo1.png");
//        ImageView logo = new ImageView(imgUrl);
//        logo.setPreserveRatio(true);
//        logo.setFitHeight(50);
//        logo.setFitWidth(50);

        topBar.getChildren().addAll(greetingText, mainScreenBtn);
        topBar.setAlignment(Pos.CENTER);
        return topBar;
    }

//    public static String getImageURL(String imageName) {
//        return Navigation.class.getResource(imageName).toExternalForm();
//    }

    public Button createLogoutButton() {
        logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> loginController.logout());
        return logoutButton;
    }

    public void extendTimeRequests() {
        ScrollPane scrollPane = new ScrollPane();
        ProgressBar progressBar = new ProgressBar(-0.1f);
        scrollPane.setContent(progressBar);
        App.mainScreen.setCenter(scrollPane);
        Message message = new Message(Message.getExtendTimeRequests);
        App.client.sendMessageToServer(message);
    }

    public void receiveExtendTimeRequests(List<ExtendTimeRequest> requests) {
        VBox vbox = new VBox(20);
        for (ExtendTimeRequest request : requests) {
            VBox temp = new VBox(5);
            HBox hbox = new HBox(20);
            Text exam = new Text("Exam: " + (request.getExecutableExam().getType().equals("regular") ?
                    request.getExecutableExam().getRegularExam().getId() :
                    request.getExecutableExam().getDocumentExam().getId()));
            Text time = new Text("Added time: " + request.getAddedTime());
            Text teacher = new Text("Teacher: " + (request.getExecutableExam().getType().equals("regular") ?
                    request.getExecutableExam().getRegularExam().getTeacher().getId() :
                    request.getExecutableExam().getDocumentExam().getTeacher().getId()));
            Text course = new Text("Course: " + (request.getExecutableExam().getType().equals("regular") ?
                    request.getExecutableExam().getRegularExam().getCourse().getName() :
                    request.getExecutableExam().getDocumentExam().getCourse().getName()));
            hbox.getChildren().addAll(exam, time, teacher, course);
            temp.getChildren().add(hbox);
            Text explanation = new Text("Explanation: " + request.getExplanation());
            temp.getChildren().add(explanation);
            Button accept = new Button("Accept");
            accept.setOnAction(e -> {
                request.setAnswer(true);
                requests.remove(request);
                receiveExtendTimeRequests(requests);
                Message message = new Message(Message.sendExtraTime, request);
                App.client.sendMessageToServer(message);
            });
            Button decline = new Button("Decline");
            decline.setOnAction(e -> {
                request.setAnswer(false);
                requests.remove(request);
                receiveExtendTimeRequests(requests);
                Message message = new Message(Message.sendExtraTime, request);
                App.client.sendMessageToServer(message);
            });

            HBox answers = new HBox(20);
            answers.getChildren().addAll(accept, decline);
            temp.getChildren().add(answers);
            vbox.getChildren().add(temp);
        }
        vbox.setAlignment(Pos.CENTER);
        App.mainScreen.setCenter(vbox);
    }

    public static void errorHandle(String string) {
        Stage stage = new Stage();
        VBox vBox = new VBox(20);

        Text errorText = new Text(string);
        Button button = new Button("Ok");
        button.setOnAction(e -> stage.close());

        errorText.setFont(new Font(15));

        vBox.getChildren().addAll(errorText, button);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 200,100);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void studentDisableButtons() {
        enterExam.setDisable(true);
        myExamsButton.setDisable(true);
        mainScreenBtn.setDisable(true);
        logoutButton.setDisable(true);
    }

    public static void studentEnableButtons() {
        enterExam.setDisable(false);
        myExamsButton.setDisable(false);
        mainScreenBtn.setDisable(false);
        logoutButton.setDisable(false);
    }

    public void extendTimeForExam() {
        VBox vbox = new VBox(20);
        HBox codeHbox = new HBox(20);
        HBox timeHbox = new HBox(20);
        HBox explanationHbox = new HBox(20);
        Button sendBtn = new Button("Send Request");

        Text codeHint = new Text("Executable Exam Code:");
        Text timeHint = new Text("Added Time:");
        Text explanationHint = new Text("Explanation");

        TextField code = new TextField();
        TextField time = new TextField();
        TextField explanation = new TextField();

        time.textProperty().addListener( (item, oldValue, newValue) -> {
            if (newValue.equals("")) {
                time.setText("0");
            }
            if (!newValue.matches("\\d{0,7}?")) {
                time.setText(oldValue);
            }
        });

        explanation.textProperty().addListener( (item, oldValue, newValue) -> {
            if (explanation.getText().equals(""))
                sendBtn.setDisable(true);
            else sendBtn.setDisable(time.getText().equals("0"));
        });

        codeHbox.getChildren().addAll(codeHint, code);
        timeHbox.getChildren().addAll(timeHint, time);
        explanationHbox.getChildren().addAll(explanationHint, explanation);

        sendBtn.setOnAction(e -> {
            ExtendTimeRequest request = new ExtendTimeRequest();
            request.setAddedTime(Integer.parseInt(time.getText()));
            request.setTeacher((Teacher) App.user);
            request.setExplanation(explanation.getText());
            Message message = new Message(Message.extraTime, new Pair<>(request, code.getText()));
            App.client.sendMessageToServer(message);
        });

        vbox.getChildren().addAll(codeHbox, timeHbox, sendBtn);

        vbox.setAlignment(Pos.CENTER);
        App.mainScreen.setCenter(vbox);
    }

}
