package com.theDreamTeam.client;

import com.theDreamTeam.entities.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QuestionBoundary {

    public static List<Course> courses;

    public static HBox selectorPane;

    public static ScrollPane pane;

    public static QuestionController questionController = new QuestionController();

    public static List<Question> selectedQuestions = new ArrayList<>();

    public static Course selectedCourse;


    public void getQuestionsForDrawer() {
        ScrollPane scrollPane = new ScrollPane();
        ProgressBar progressBar = new ProgressBar(-0.1f);
        scrollPane.setContent(progressBar);
        App.mainScreen.setCenter(scrollPane);
        Message message = new Message(Query.getCoursesForQuestionsDrawer);
        App.client.sendMessageToServer(message);
    }

    public void receiveCoursesForQuestionsDrawer(List<Course> courses) {
        QuestionBoundary.courses = courses;
        VBox vbox = new VBox(20);
        selectorPane = new HBox(20);
        selectorPane.setPadding(new Insets(20, 0, 0, 0));
        pane = new ScrollPane();
        selectorPane.setAlignment(Pos.CENTER);

        List<String> coursesNames = new ArrayList<>();
        for (Course course : courses) {
            coursesNames.add(course.getName());
        }

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        for (String name : coursesNames) {
            choiceBox.getItems().add(name);
        }
        choiceBox.getSelectionModel().select(0);
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            for (Course course : courses) {
                if (course.getName().equals(choiceBox.getSelectionModel().getSelectedItem())){
                    selectedCourse = course;
                    break;
                }
            }
            Message message = new Message(Query.getQuestionsDrawer, selectedCourse);
            App.client.sendMessageToServer(message);
        });

        selectorPane.getChildren().addAll(choiceBox, searchButton);
        vbox.getChildren().addAll(selectorPane, pane);
        App.mainScreen.setCenter(vbox);
    }

    public void receiveQuestionsDrawer(List<Question> questions) {
        VBox vbox = new VBox(10);
        if (App.user instanceof Teacher) {
            Button addBtn = new Button("Add Question");
            addBtn.setOnAction(e -> addQuestionToDrawer(selectedCourse));
            vbox.getChildren().add(addBtn);
        }

        for (Question question : questions) {
            Text id = new Text("Question id: " + question.getId());
            HBox hbox = new HBox(20);
            hbox.getChildren().add(showQuestion(question));
            if (App.user instanceof Teacher) {
                Button editBtn = new Button("Edit");
                editBtn.setOnAction(e -> editQuestion(question));
                hbox.getChildren().add(editBtn);
            }
            vbox.getChildren().addAll(id, hbox);
            vbox.setPadding(new Insets(0,50,0,50));
        }
        vbox.setAlignment(Pos.CENTER);
        pane.setContent(vbox);
    }

    public void addQuestionToDrawer(Course course) {
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        Button saveBtn = new Button("Save");
        TextField title = new TextField();
        title.setPromptText("Title ...");
        TextField answerA = new TextField();
        title.setPromptText("Answer A ...");
        TextField answerB = new TextField();
        title.setPromptText("Answer B ...");
        TextField answerC = new TextField();
        title.setPromptText("Answer C ...");
        TextField answerD = new TextField();
        title.setPromptText("Answer D ...");

        ToggleGroup group = new ToggleGroup();
        RadioButton rb1 = new RadioButton("A");
        rb1.setUserData("A");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);
        RadioButton rb2 = new RadioButton("B");
        rb2.setUserData("B");
        rb2.setToggleGroup(group);
        RadioButton rb3 = new RadioButton("C");
        rb3.setUserData("C");
        rb3.setToggleGroup(group);
        RadioButton rb4 = new RadioButton("D");
        rb4.setUserData("D");
        rb4.setToggleGroup(group);

        HBox ansA = new HBox(20);
        ansA.getChildren().addAll(rb1, answerA);
        HBox ansB = new HBox(20);
        ansB.getChildren().addAll(rb2, answerB);
        HBox ansC = new HBox(20);
        ansC.getChildren().addAll(rb3, answerC);
        HBox ansD = new HBox(20);
        ansD.getChildren().addAll(rb4, answerD);

        Text txt = new Text("");

        vbox.getChildren().addAll(saveBtn, title, ansA, ansB, ansC,ansD, txt);
        vbox.setPadding(new Insets(0,50,0,50));

        saveBtn.setOnAction(e -> {
            if (checkInput(title.getText(), answerA.getText(), answerB.getText(), answerC.getText(), answerD.getText())) {
                Question tempQuest = new Question(title.getText(),
                        answerA.getText(),
                        answerB.getText(),
                        answerC.getText(),
                        answerD.getText(),
                        (String) group.getSelectedToggle().getUserData(), course);

                questionController.saveQuestion(tempQuest);
                Message message = new Message(Query.getQuestionsDrawer, selectedCourse);
                App.client.sendMessageToServer(message);
            } else {
                txt.setText("Invalid");
            }
        });
        pane.setContent(vbox);
    }

    public void editQuestion(Question question) {
        TextField title = new TextField(question.getTitle());
        title.setFont(new Font(20));

        TextField answerA = new TextField(question.getAnswerA());
        answerA.setFont(new Font(15));
        TextField answerB = new TextField(question.getAnswerB());
        answerB.setFont(new Font(15));
        TextField answerC = new TextField(question.getAnswerC());
        answerC.setFont(new Font(15));
        TextField answerD = new TextField(question.getAnswerD());
        answerD.setFont(new Font(15));

        ToggleGroup toggleGroup = new ToggleGroup();    // radio buttons for selecting the correct answer
        RadioButton ansA = new RadioButton();
        ansA.setToggleGroup(toggleGroup);
        ansA.setUserData("A");
        RadioButton ansB = new RadioButton();
        ansB.setToggleGroup(toggleGroup);
        ansB.setUserData("B");
        RadioButton ansC = new RadioButton();
        ansC.setToggleGroup(toggleGroup);
        ansC.setUserData("C");
        RadioButton ansD = new RadioButton();
        ansD.setToggleGroup(toggleGroup);
        ansD.setUserData("D");

        switch (question.getCorrectAns()) { // toggle the correct radio button
            case "A": ansA.setSelected(true);
                break;
            case "B": ansB.setSelected(true);
                break;
            case "C": ansC.setSelected(true);
                break;
            case "D": ansD.setSelected(true);
                break;
        }

        Button savebtn = new Button("Save");

        HBox ansAHbox = new HBox();
        ansAHbox.getChildren().addAll(answerA, ansA);
        ansAHbox.setSpacing(10);

        HBox ansBHbox = new HBox();
        ansBHbox.getChildren().addAll(answerB, ansB);
        ansBHbox.setSpacing(10);

        HBox ansCHbox = new HBox();
        ansCHbox.getChildren().addAll(answerC, ansC);
        ansCHbox.setSpacing(10);

        HBox ansDHbox = new HBox();
        ansDHbox.getChildren().addAll(answerD, ansD);
        ansDHbox.setSpacing(10);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(title, savebtn);
        hbox.setSpacing(10);
        hbox.setMaxSize(HBox.USE_PREF_SIZE, HBox.USE_PREF_SIZE);    // wrap content
        hbox.setAlignment(Pos.CENTER);

        VBox textVbox = new VBox();
        textVbox.getChildren().addAll(hbox, ansAHbox, ansBHbox, ansCHbox, ansDHbox);
        textVbox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);    // wrap content
        textVbox.setPadding(new Insets(0,50,0,50));

        Text txt = new Text();
        txt.setFont(new Font(20));

        VBox vbox = new VBox();
        vbox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);    // wrap content
        vbox.getChildren().addAll(textVbox, txt);
        vbox.setAlignment(Pos.CENTER);

        savebtn.setOnAction(e -> {
            if (checkInput(title.getText(), answerA.getText(), answerB.getText(), answerC.getText(), answerD.getText())) {
                Question tempQuest = new Question(title.getText(),
                        answerA.getText(),
                        answerB.getText(),
                        answerC.getText(),
                        answerD.getText(),
                        (String) toggleGroup.getSelectedToggle().getUserData(), selectedCourse);

                questionController.saveQuestion(tempQuest);
                Message message = new Message(Query.getQuestionsDrawer, selectedCourse);
                App.client.sendMessageToServer(message);
            } else {
                txt.setText("Invalid");
            }
        });

        pane.setContent(vbox);
    }

    public boolean checkInput(String title, String answerA, String answerB, String answerC, String answerD) {
        return !title.equals("") && !answerA.equals("") && !answerB.equals("") && !answerC.equals("") && !answerD.equals("");
    }

    public VBox showQuestionInExamCopy(Answer answer) {
        VBox vbox = new VBox(10);
        Text title = new Text(answer.getQuestion().getTitle());
        Text ansA = new Text("A. " + answer.getQuestion().getAnswerA());
        Text ansB = new Text("B. " + answer.getQuestion().getAnswerB());
        Text ansC = new Text("C. " + answer.getQuestion().getAnswerC());
        Text ansD = new Text("D. " + answer.getQuestion().getAnswerD());
        Text correctAns = new Text("Correct Answer: " + answer.getQuestion().getCorrectAns());
        Text yourAns = new Text("Your Answer: " + answer.getAnswer());
        Text points = new Text("Points: " + answer.getPoints() + " / " + answer.getQuestion().getGrade());

        VBox answers = new VBox(5);
        answers.getChildren().addAll(ansA, ansB, ansC, ansD);

        HBox hbox = new HBox(30);
        hbox.getChildren().addAll(correctAns, yourAns, points);

        vbox.getChildren().addAll(title, answers, hbox);

        return vbox;
    }

    public void setTextBold(Text text) {
        text.setStyle("-fx-font-weight: bold");
    }

    public VBox showQuestion(Question question) {
        String cssLayout = "-fx-border-color: black;";
        VBox vbox = new VBox();
        vbox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);    // wrap content

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setMaxSize(HBox.USE_PREF_SIZE, HBox.USE_PREF_SIZE);    // wrap content
        hbox.setSpacing(10);

        Text title = new Text(question.getTitle());
        title.setFont(new Font(20));
        Text answerA = new Text(question.getAnswerA());
        answerA.setFont(new Font(15));
        Text answerB = new Text(question.getAnswerB());
        answerB.setFont(new Font(15));
        Text answerC = new Text(question.getAnswerC());
        answerC.setFont(new Font(15));
        Text answerD = new Text(question.getAnswerD());
        answerD.setFont(new Font(15));
        String answer = question.getCorrectAns();
        switch (answer) {   // the correct answer will be in bold
            case "A":
                setTextBold(answerA);
                break;
            case "B":
                setTextBold(answerB);
                break;
            case "C":
                setTextBold(answerC);
                break;
            case "D":
                setTextBold(answerD);
                break;
        }
        VBox textVbox = new VBox();
        textVbox.getChildren().addAll(answerA, answerB, answerC, answerD);
        textVbox.setAlignment(Pos.CENTER_LEFT);

        hbox.getChildren().add(title);
        hbox.setStyle(cssLayout);
        vbox.getChildren().addAll(hbox, textVbox);
        vbox.setStyle(cssLayout);
        return vbox;
    }

    public void selectQuestionsForNewExam() {
        ExamController.questionsForNewExam = new ArrayList<>();
        selectedQuestions = new ArrayList<>();
        Stage stage = new Stage();
        VBox mainVbox = new VBox(20);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainVbox);
        mainVbox.setAlignment(Pos.CENTER);
        Button nextBtn = new Button("Next");
        mainVbox.getChildren().add(nextBtn);
        List<Question> quests = ExamBoundary.selectedCourse.getQuestions();
        for (Question question : quests) {
            HBox hbox = new HBox(20);
            VBox vbox = showQuestion(question);

            CheckBox checkBox = new CheckBox();

            for (GradedQuestion gradedQuestion : ExamBoundary.editingRegularExam.getGradedQuestions()) {
                if (gradedQuestion.getParentId() == question.getId()) {
                    selectedQuestions.add(question);
                    checkBox.setSelected(true);
                }
            }

            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                if (new_val){
                    boolean exists = false;
                    for (Question temp : selectedQuestions){
                        if (temp.getId() == question.getId()) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists)
                        selectedQuestions.add(question);
                } else {
                    selectedQuestions.remove(question);
                }
            });

            hbox.getChildren().addAll(checkBox, vbox);
            hbox.setPadding(new Insets(0,10,0,10));

            mainVbox.getChildren().add(hbox);
        }

        nextBtn.setOnAction(e -> setPointsForQuestion(stage));

        Scene scene = new Scene(scrollPane, 800, 800);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Select Questions");
        stage.show();
    }

    public void setPointsForQuestion(Stage stage) {
        ScrollPane scrollPane = new ScrollPane();
        VBox vbox = new VBox(10);
        Button saveBtn = new Button("Save");
        Text invalid = new Text();
        List<TextField> textFields = new ArrayList<>();
        List<HBox> hBoxes = new ArrayList<>();
        for (Question selectedQuestion : selectedQuestions) {
            TextField textField = new TextField();
            textField.textProperty().addListener( (item, oldValue, newValue) -> {
                if (!newValue.matches("\\d{0,7}?")) {
                    textField.setText(oldValue);
                }
            });
            textFields.add(textField);
            HBox hbox = new HBox(20);
            hbox.getChildren().addAll(showQuestion(selectedQuestion), textField);
            hbox.setPadding(new Insets(0,10,0,10));
            hBoxes.add(hbox);
        }

        saveBtn.setOnAction(e -> {
            boolean valid = true;
            for (TextField textField : textFields) {
                if (textField.getText().equals(""))
                    valid = false;
            }
            if (valid) {
                ExamController.maxGradeForNewExam = 0;
                ExamController.questionsForNewExam = new ArrayList<>();
                for (int i = 0; i < textFields.size(); i++){
                    TextField textField = textFields.get(i);
                    Question question = selectedQuestions.get(i);
                    int grade = Integer.parseInt(textField.getText());
                    ExamController.maxGradeForNewExam += grade;
                    ExamController.questionsForNewExam.add(questionController.makeGradedQuestion(question, grade));
                }
                stage.close();
            }
        });

        vbox.getChildren().addAll(saveBtn, invalid);
        for (HBox temp : hBoxes)
            vbox.getChildren().add(temp);
        scrollPane.setContent(vbox);
        Scene scene = new Scene(scrollPane, 800, 800);
        stage.setScene(scene);
    }

    public VBox showQuestionInExamsDrawer(GradedQuestion question) {
        Text title = new Text(question.getTitle());
        Text answerA = new Text("A. " + question.getAnswerA());
        Text answerB = new Text("B. " + question.getAnswerB());
        Text answerC = new Text("C. " + question.getAnswerC());
        Text answerD = new Text("D. " + question.getAnswerD());
        Text correctAns = new Text("Correct Answer: " + question.getCorrectAns());
        Text points = new Text("Points: " + question.getGrade());

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(title, answerA, answerB, answerC, answerD, correctAns, points);
        vbox.setPadding(new Insets(0,50,0,50));

        return vbox;
    }
}
