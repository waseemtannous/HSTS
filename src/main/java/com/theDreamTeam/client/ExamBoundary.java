package com.theDreamTeam.client;

import com.theDreamTeam.entities.*;

//import com.theDreamTeam.ExamController;
//import com.theDreamTeam.QuestionBoundary;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamBoundary {

    public static ExamController examController = new ExamController();

    public static QuestionBoundary questionBoundary = new QuestionBoundary();

    public static HBox selectorPane;

    public static ScrollPane pane;

    public static List<Course> courses;

    public static Course selectedCourse;

    public static RegularExam editingRegularExam = new RegularExam();

    public static Exam executeExam;

    public static Text endTime;

    public static Date end;

    public static byte[] document = null;

    ////////////////// student reports ////////////////////

//    public void studentReports() {
//        VBox vbox = new VBox(50);
//        selectorPane = new HBox();
//        pane = new ScrollPane();
//        selectorPane.setAlignment(Pos.CENTER);
//
//        List<Course> courses = ((Student)App.user).getCourses();
//
//        List<String> coursesNames = new ArrayList<>();
//        for (Course course : courses) {
//            coursesNames.add(course.getName());
//        }
//
//        ChoiceBox<String> choiceBox = new ChoiceBox<>();
//        for (String name : coursesNames) {
//            choiceBox.getItems().add(name);
//        }
//        choiceBox.getSelectionModel().select(0);
//        Button searchButton = new Button("Search");
//        searchButton.setOnAction(e -> examController.getExamsCopiesForStudent(choiceBox.getSelectionModel().getSelectedItem()));
////        searchButton.setOnAction(e -> showExamsCopies(App.list));
//        selectorPane.getChildren().addAll(choiceBox, searchButton);
//        vbox.getChildren().addAll(selectorPane, pane);
//        App.mainScreen.setCenter(vbox);
//    }

    public void showExamsCopies(List<Pair<ExamCopy, List<Answer>>> list) {
        List<ExamCopy> exams = new ArrayList<>();
        for (Pair<ExamCopy, List<Answer>> examCopyListPair : list) {
            exams.add(examCopyListPair.getKey());
        }
        VBox vbox = new VBox(10);
        for (int i = 0; i < exams.size(); i++) {
            int index = i;
            ExamCopy exam = exams.get(i);
            HBox hbox = new HBox(10);
            Text examId = new Text("Exam Id:  " +exam.getId());
            if (exam.getType().equals("regular")) {
                Text student = new Text("Student Id:  " + exam.getStudent().getId());
                Text grade = new Text(exam.getGrade() + " / " + exam.getExecutableExam().getRegularExam().getMaxGrade());
                Button showButton = new Button("Show");
                showButton.setOnAction(e -> showExamCopy(list.get(index)));
                hbox.getChildren().addAll(student, examId, grade, showButton);

            } else if (exam.getType().equals("document")) {
                Text student = new Text("Student Id:  " + exam.getStudent().getId());
                Text grade = new Text(exam.getGrade() + " / " + exam.getExecutableExam().getDocumentExam().getMaxGrade());
                Button showButton = new Button("Show");
                showButton.setOnAction(e -> showExamCopy(list.get(index)));
                hbox.getChildren().addAll(student, examId, grade, showButton);
            }
            vbox.getChildren().add(hbox);
        }
        pane.setContent(vbox);
    }

    public void showExamCopy(Pair<ExamCopy, List<Answer>> pair) {
        ExamCopy exam = pair.getKey();
        VBox vbox = new VBox(10);

        if (exam.getExecutableExam().getType().equals("regular")) {
            Text teacher = new Text("Teacher: " + exam.getExecutableExam().getRegularExam().getTeacher().getUsername());
            Text student = new Text("Student" + exam.getStudent().getId());
            Text grade = new Text("Final Grade: " + exam.getGrade() + " / " + exam.getExecutableExam().getRegularExam().getMaxGrade());

            HBox bar = new HBox(20);
            bar.getChildren().addAll(teacher, student, grade);

            VBox top = new VBox(20);
            Text gradeChangeNotes = new Text("Notes For Changing grade: " + exam.getNotesForGradeChange());
            top.getChildren().addAll(bar, gradeChangeNotes);

            vbox.getChildren().add(top);
            ScrollPane scrollPane = new ScrollPane();
            VBox questions = new VBox(25);

            System.out.println("waseem123");
            for (Answer answer : pair.getValue()) {
                System.out.println("waseem69");
                questions.getChildren().add(questionBoundary.showQuestionInExamCopy(answer));
                System.out.println("waseem696969");
            }
            scrollPane.setContent(questions);
            vbox.getChildren().add(scrollPane);
        } else {
            Text teacher = new Text("Teacher: " + exam.getExecutableExam().getDocumentExam().getTeacher().getUsername());
            Text student = new Text("Student" + exam.getStudent().getId());
            Text grade = new Text("Final Grade: " + exam.getGrade() + " / " + exam.getExecutableExam().getDocumentExam().getMaxGrade());

            HBox bar = new HBox(20);
            bar.getChildren().addAll(teacher, student, grade);

            VBox top = new VBox(20);
            Text gradeChangeNotes = new Text("Notes For Changing grade: " + exam.getNotesForGradeChange());
            top.getChildren().addAll(bar, gradeChangeNotes);

            vbox.getChildren().add(top);
            Button questionnaire = new Button("Download Questionnaire");
            Button answer = new Button("Download Answer");

            HBox hBox = new HBox(20);
            hBox.getChildren().addAll(questionnaire, answer);

            questionnaire.setOnAction(e -> examController.downloadExam(exam.getExecutableExam().getDocumentExam().getFile()));
            answer.setOnAction(e -> examController.downloadExam(exam.getAnswerDoc()));

            vbox.getChildren().add(hBox);
        }


        pane.setContent(vbox);
    }

    //////////////////////////////////////////////////////////////

    public void showExamsDrawer() {
        Message message = new Message(Message.getCoursesForExamsDrawer);
        App.client.sendMessageToServer(message);
    }

    public void receiveCoursesForExamsDrawer(List<Course> courses) {
        ExamBoundary.courses = courses;
        VBox vbox = new VBox(50);
        selectorPane = new HBox();
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
        Button newExambtn = new Button("Make New Exam");
        HBox hBox = new HBox(20);
        Button searchButton = new Button("Search");
        hBox.getChildren().addAll(choiceBox, searchButton);
        VBox upperVBox = new VBox(20);
        upperVBox.getChildren().add(hBox);

        if (App.user instanceof Teacher) {
            newExambtn.setDisable(true);
            newExambtn.setOnAction(e -> makeNewExam());
            upperVBox.getChildren().add(newExambtn);
        }

        searchButton.setOnAction(e -> {
            for (Course tempCourse : courses) {
                if (tempCourse.getName().equals(choiceBox.getSelectionModel().getSelectedItem()))
                    selectedCourse = tempCourse;
            }
            System.out.println("exams drawer search btn");
            Message message = new Message(Message.getExamsDrawer, selectedCourse.getId());
            App.client.sendMessageToServer(message);
            newExambtn.setDisable(false);
        });


        selectorPane.getChildren().add(upperVBox);
        vbox.getChildren().addAll(selectorPane, pane);
        App.mainScreen.setCenter(vbox);
    }

    public void receiveExamsDrawer(List<Exam> exams) {
        VBox vbox = new VBox(10);
        for (Exam exam : exams) {
            HBox hbox = new HBox(20);
            if (exam instanceof RegularExam) {
                Text examId = new Text("Exam id: " + exam.getId());
                Text teacherId = new Text("Teacher: " + exam.getTeacher().getId());
                Text cours = new Text("Course: " + exam.getCourse().getName());
                Button showbtn = new Button("Show");
                showbtn.setOnAction(e -> showExamInDrawer((RegularExam) exam));
                Button executeExam = new Button("Execute Exam");
                executeExam.setOnAction(e -> {
                    ExamBoundary.executeExam = exam;
                    executeExam(ExamBoundary.executeExam);
                });
                hbox.getChildren().addAll(examId, teacherId, cours, showbtn, executeExam);
            } else {
                Text examId = new Text("Exam id: " + exam.getId());
                Text teacherId = new Text("Teacher: " + exam.getTeacher().getId());
                Text cours = new Text("Course: " + exam.getCourse().getName());
                Button executeExam = new Button("Execute Exam");
                executeExam.setOnAction(e -> {
                    ExamBoundary.executeExam = exam;
                    executeExam(ExamBoundary.executeExam);
                });
                Button downloadbtn = new Button("Download");
                downloadbtn.setOnAction(e -> examController.downloadExam(((DocumentExam) exam).getFile()));
                Button editbtn = new Button("Edit");
                editbtn.setOnAction(e -> editDocumentExam((DocumentExam) exam));
                hbox.getChildren().addAll(examId, teacherId, cours, downloadbtn, editbtn, executeExam);
            }
            vbox.getChildren().add(hbox);
        }
        vbox.setAlignment(Pos.CENTER);
        pane.setContent(vbox);
    }

    public void editDocumentExam(DocumentExam documentExam) {
        Button savebtn = new Button("Save");

        TextField duration = new TextField("" + documentExam.getDuration());

        TextField notesForTeacher = new TextField(documentExam.getNotesForTeacher());
        TextField notesForStudent = new TextField(documentExam.getNotesForStudent());

        Text durationHint = new Text("Duration: ");
        Text notesForTeacherHint = new Text("Notes For Teacher: ");
        Text notesForStudentHint = new Text("Notes For Student: ");

        VBox attributes = new VBox(20);

        HBox durationHbox = new HBox(10);
        durationHbox.getChildren().addAll(durationHint, duration);
        HBox notesForTeacherHbox = new HBox(10);
        notesForTeacherHbox.getChildren().addAll(notesForTeacherHint, notesForTeacher);
        HBox notesForStudentHbox = new HBox(10);
        notesForStudentHbox.getChildren().addAll(notesForStudentHint, notesForStudent);

        Button addDocumentbtn = new Button("Add Document");
        addDocumentbtn.setOnAction(e -> documentExam.setFile(examController.uploadDocument()));


        attributes.getChildren().addAll(savebtn, durationHbox, notesForTeacherHbox, notesForStudentHbox, addDocumentbtn);
        savebtn.setOnAction(e -> {
            Exam exam = new Exam(notesForTeacher.getText(), notesForStudent.getText(), documentExam.getTeacher(), documentExam.getCourse(), Integer.parseInt(duration.getText()), 100);
            DocumentExam newDocumentExam = new DocumentExam(exam, documentExam.getFile());
            Message message = new Message(Message.saveNewDocumentExam, newDocumentExam);
            App.client.sendMessageToServer(message);
        });

        duration.textProperty().addListener( (item, oldValue, newValue) -> {
            // xor
            savebtn.setDisable(newValue.equals(""));

            if (!newValue.equals("")) {
                if (!(newValue.matches("\\d{0,100}?"))) {
                    duration.setText(oldValue);
                }
            }
        });
        pane.setContent(attributes);
    }

    public void showExamInDrawer(RegularExam exam) {
        VBox vbox = new VBox();
        Text examTitle = new Text("Exam #" + exam.getId());
        Text teacher = new Text("Teacher: " + exam.getTeacher().getId());
        Text duration = new Text("Duration: " + exam.getDuration() + " minutes");
        Text grade = new Text("Max Grade: " + exam.getMaxGrade());
        VBox mainVbox = new VBox(20);
        mainVbox.setAlignment(Pos.CENTER);
        List<VBox> questions = new ArrayList<>();
        for (GradedQuestion question : exam.getGradedQuestions()) {
            questions.add(questionBoundary.showQuestionInExamsDrawer(question));
        }

        for (VBox temp : questions) {
            mainVbox.getChildren().add(temp);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainVbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        HBox hbox = new HBox(50);
        hbox.getChildren().addAll(examTitle, teacher, duration, grade);
        hbox.setAlignment(Pos.CENTER);

        if (App.user instanceof Teacher) {
            Button editExamBtn = new Button("Edit Exam");
            editExamBtn.setOnAction(e -> editRegularExam(exam));
            hbox.getChildren().add(editExamBtn);
        }

        vbox.getChildren().addAll(hbox, scrollPane);

        pane.setContent(vbox);
    }

    public void makeNewExam() {
        Button savebtn = new Button("Save");

        TextField duration = new TextField();
        TextField notesForTeacher = new TextField();
        TextField notesForStudent = new TextField();

        Text durationHint = new Text("Duration: ");
        Text notesForTeacherHint = new Text("Notes For Teacher: ");
        Text notesForStudentHint = new Text("Notes For Student: ");

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("Regular", "Document");
        choiceBox.setValue("Regular");
        Text typeHint = new Text("Type: ");

        VBox attributes = new VBox(20);

        HBox durationHbox = new HBox(10);
        durationHbox.getChildren().addAll(durationHint, duration);
        HBox notesForTeacherHbox = new HBox(10);
        notesForTeacherHbox.getChildren().addAll(notesForTeacherHint, notesForTeacher);
        HBox notesForStudentHbox = new HBox(10);
        notesForStudentHbox.getChildren().addAll(notesForStudentHint, notesForStudent);

        Button btn = new Button("Add Questions");
        btn.setOnAction(e -> questionBoundary.selectQuestionsForNewExam());

        HBox typeHbox = new HBox(10);
        typeHbox.getChildren().addAll(typeHint, choiceBox, btn);
        choiceBox.getSelectionModel().selectedItemProperty().addListener( (item, oldValue, newValue) -> {
            if (newValue.equals("Regular")) {
                btn.setText("Add questions");
                btn.setOnAction(e -> questionBoundary.selectQuestionsForNewExam());
            } else if (newValue.equals("Document")) {
                btn.setText("Add Document");
                btn.setOnAction(e -> document = examController.uploadDocument());
            }
        });

        attributes.getChildren().addAll(savebtn, durationHbox, notesForTeacherHbox, notesForStudentHbox, typeHbox);
        savebtn.setOnAction(e -> examController.checkExam(choiceBox.getSelectionModel().getSelectedItem(),
                duration.getText(), notesForTeacher.getText(), notesForStudent.getText()));
        pane.setContent(attributes);
    }

    public void invalidExam() {
        Stage stage = new Stage();
        VBox vbox = new VBox(30);
        Text invalid = new Text("Invalid Exam.");
        Button btn = new Button("Ok");
        btn.setOnAction(e -> stage.close());
        vbox.getChildren().addAll(invalid, btn);
        Scene scene = new Scene(vbox, 100, 100);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void startExam(ExecutableExam executableExam) {
        VBox vbox = new VBox(20);
        Button finishbtn = new Button("Finish");
        vbox.getChildren().add(finishbtn);

        Text examId = new Text("Exam Id: " + executableExam.getRegularExam().getId());
        Text course = new Text("Course: " + executableExam.getRegularExam().getCourse().getId());
        Text teacher = new Text("Teacher: " + executableExam.getRegularExam().getTeacher().getId());
        Text grade = new Text("Max Grade: " + executableExam.getRegularExam().getMaxGrade());
        Text notes = new Text("Notes: " + executableExam.getRegularExam().getNotesForStudent());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        long curTimeInMs = now.getTime();
        end = new Date(curTimeInMs + (long)(ExamController.examTime * 60000));

        Text startTime = new Text("Start Time: " + formatter.format(now));
        endTime = new Text("Finish Time: " + formatter.format(end));
        vbox.getChildren().addAll(examId, course, teacher, grade, notes, startTime, endTime);

        if (executableExam.getType().equals("regular")) {
            finishbtn.setOnAction(e -> {
                ExamController.timer.stop();
                examController.finishExam(executableExam);
            });
            ExamController.answerIndex = 0;
            ExamController.answers = new ArrayList<>();
            ExamController.examQuestions = executableExam.getRegularExam().getGradedQuestions();
            for (GradedQuestion question : ExamController.examQuestions)
                ExamController.answers.add(new Answer(question));

            VBox temp = new VBox(20);

            Button savebtn = new Button("Save");
            savebtn.setOnAction(e -> {
                ExamController.finished = true;
                examController.finishExam(executableExam);
            });

            vbox.getChildren().add(temp);

            showQuestionInExam(temp, ExamController.answerIndex);

            Button next = new Button("Next");
            Button previous = new Button("previous");
            next.setOnAction(e -> {
                if (ExamController.answerIndex < (ExamController.examQuestions.size() - 1)) {
                    showQuestionInExam(temp, ++ExamController.answerIndex);
                }
                next.setDisable(ExamController.answerIndex == (ExamController.examQuestions.size() - 1));
                previous.setDisable(false);
            });
            previous.setDisable(true);
            previous.setOnAction(e -> {
                if (ExamController.answerIndex > 0)
                    showQuestionInExam(temp, --ExamController.answerIndex);
                next.setDisable(false);
                previous.setDisable(ExamController.answerIndex == 0);
            });
            HBox buttons = new HBox(20);
            buttons.getChildren().addAll(previous, next);
            vbox.getChildren().add(buttons);

        } else {
            Button questionnaire = new Button("Download Questionnaire");
            Button answer = new Button("Upload Your Answer");

            HBox hBox = new HBox(20);
            hBox.getChildren().addAll(questionnaire, answer);

            questionnaire.setOnAction(e -> examController.downloadExam(executableExam.getDocumentExam().getFile()));
            answer.setOnAction(e -> ExamController.answerDoc = examController.uploadDocument());

            vbox.getChildren().add(hBox);
        }
        App.mainScreen.setCenter(vbox);
    }

    public void showQuestionInExam(VBox vbox, int index) {
        GradedQuestion gradedQuestion = ExamController.examQuestions.get(index);
        vbox.getChildren().clear();

        Text title = new Text(gradedQuestion.getTitle());
        Text answerA = new Text("A.  " + gradedQuestion.getAnswerA());
        Text answerB = new Text("B.  " + gradedQuestion.getAnswerB());
        Text answerC = new Text("C.  " + gradedQuestion.getAnswerC());
        Text answerD = new Text("D.  " + gradedQuestion.getAnswerD());

        ToggleGroup toggleGroup = new ToggleGroup();    // radio buttons for selecting the correct answer
        RadioButton ansA = new RadioButton();
        ansA.setToggleGroup(toggleGroup);
        ansA.setOnAction(e -> ExamController.answers.get(index).setAnswer("A"));
        RadioButton ansB = new RadioButton();
        ansB.setToggleGroup(toggleGroup);
        ansB.setOnAction(e -> ExamController.answers.get(index).setAnswer("B"));
        RadioButton ansC = new RadioButton();
        ansC.setToggleGroup(toggleGroup);
        ansC.setOnAction(e -> ExamController.answers.get(index).setAnswer("C"));
        RadioButton ansD = new RadioButton();
        ansD.setToggleGroup(toggleGroup);
        ansD.setOnAction(e -> ExamController.answers.get(index).setAnswer("D"));

        switch (ExamController.answers.get(index).getAnswer()) {
            case "A":
                ansA.setSelected(true);
                break;
            case "B":
                ansB.setSelected(true);
                break;
            case "C":
                ansC.setSelected(true);
                break;
            case "D":
                ansD.setSelected(true);
                break;
        }

        HBox ansAHbox = new HBox(20);
        ansAHbox.getChildren().addAll(ansA, answerA);
        HBox ansBHbox = new HBox(20);
        ansBHbox.getChildren().addAll(ansB, answerB);
        HBox ansCHbox = new HBox(20);
        ansCHbox.getChildren().addAll(ansC, answerC);
        HBox ansDHbox = new HBox(20);
        ansDHbox.getChildren().addAll(ansD, answerD);

        vbox.getChildren().addAll(title, ansAHbox, ansBHbox, ansCHbox, ansDHbox);
    }

    public void invalidCode() {

    }

    public void invalidStudent() {

    }

    public void executeExam(Exam exam) {
        ProgressIndicator pi = new ProgressIndicator(-1f);
        VBox vbox = new VBox(20);
        Button btn = new Button("Execute");
        btn.setDisable(true);
        TextField code = new TextField();
        code.setPromptText("Code ...");
        code.textProperty().addListener( (item, oldValue, newValue) -> {
            if (code.getText().length() > 4)
                code.setText(oldValue);
            if (code.getText().length() == 4)
                btn.setDisable(false);
            if (code.getText().length() < 4)
                btn.setDisable(true);
        });

        vbox.getChildren().addAll(code, btn);
        btn.setOnAction(e -> {
            if (exam instanceof RegularExam){
                vbox.getChildren().add(pi);
                examController.saveExecutableExam(new ExecutableExam(code.getText(), (RegularExam) exam, (Teacher) App.user));
            }
            if (exam instanceof DocumentExam){
                vbox.getChildren().add(pi);
                examController.saveExecutableExam(new ExecutableExam(code.getText(), (DocumentExam) exam, (Teacher) App.user));
            }
        });

        pane.setContent(vbox);
    }

    // TODO: implement
    public void getExecutableExamAnswer(boolean answer) {
        if (answer)
            showExamsDrawer();
        else {
            Stage stage = new Stage();

            Text text = new Text("Code Already Exists");
            Button closeBtn = new Button("Close");
            closeBtn.setOnAction(e -> {
                stage.close();
                executeExam(ExamBoundary.executeExam);
            });

            VBox vbox = new VBox(20);
            vbox.getChildren().addAll(text, closeBtn);

            Scene scene = new Scene(vbox, 200, 200);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void editRegularExam(RegularExam exam) {
        Button savebtn = new Button("Save");

        TextField duration = new TextField("" + exam.getDuration());
        TextField notesForTeacher = new TextField(exam.getNotesForTeacher());
        TextField notesForStudent = new TextField(exam.getNotesForStudent());

        Text durationHint = new Text("Duration: ");
        Text notesForTeacherHint = new Text("Notes For Teacher: ");
        Text notesForStudentHint = new Text("Notes For Student: ");

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("regular", "rocument");
        choiceBox.setValue("regular");

        VBox attributes = new VBox(20);

        HBox durationHbox = new HBox(10);
        durationHbox.getChildren().addAll(durationHint, duration);
        HBox notesForTeacherHbox = new HBox(10);
        notesForTeacherHbox.getChildren().addAll(notesForTeacherHint, notesForTeacher);
        HBox notesForStudentHbox = new HBox(10);
        notesForStudentHbox.getChildren().addAll(notesForStudentHint, notesForStudent);

        Button btn = new Button("Add Questions");
        btn.setOnAction(e -> {
            editingRegularExam = exam;
            questionBoundary.selectQuestionsForNewExam();
        });

        attributes.getChildren().addAll(savebtn, durationHbox, notesForTeacherHbox, notesForStudentHbox, btn);
        savebtn.setOnAction(e -> examController.checkExam(choiceBox.getSelectionModel().getSelectedItem(),
                duration.getText(), notesForTeacher.getText(), notesForStudent.getText()));

        duration.textProperty().addListener( (item, oldValue, newValue) -> {
            // xor
            savebtn.setDisable(newValue.equals(""));

            if (!newValue.equals("")) {
                if (!(newValue.matches("\\d{0,100}?"))) {
                    duration.setText(oldValue);
                }
            }
        });

        pane.setContent(attributes);
    }

    private void deleteQuestionFromExam(GradedQuestion question, RegularExam exam) {
        exam.getGradedQuestions().remove(question);
        editRegularExam(exam);
    }

    public void enterExamScreen() {
        VBox vbox = new VBox();
        TextField code = new TextField();
        Text title = new Text("Please Enter Exam Code:");
        Button enterButton = new Button("Enter Exam");
        enterButton.setOnAction(e -> examController.getExamByCode(code.getText()));
        vbox.getChildren().addAll(title, code, enterButton);
        vbox.setAlignment(Pos.CENTER);
        App.mainScreen.setCenter(vbox);
    }

    public void examsCopies() {
        ScrollPane scrollPane = new ScrollPane();
        ProgressBar progressBar = new ProgressBar(-0.1f);
        scrollPane.setContent(progressBar);
        App.mainScreen.setCenter(scrollPane);
        Message message = new Message(Message.getCoursesForExamsCopies);
        message.setUser(App.user);
        App.client.sendMessageToServer(message);
    }

    public void receiveCoursesForExamsCopies(List<Course> courses) {
        ExamBoundary.courses = courses;
        VBox vbox = new VBox(50);
        selectorPane = new HBox();
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
        searchButton.setOnAction(e -> examController.getCopies(choiceBox.getSelectionModel().getSelectedItem()));

        selectorPane.getChildren().addAll(choiceBox, searchButton);
        vbox.getChildren().addAll(selectorPane, pane);
        App.mainScreen.setCenter(vbox);
    }

    public void checkExams() {
        ScrollPane scrollPane = new ScrollPane();
        ProgressBar progressBar = new ProgressBar(-0.1f);
        scrollPane.setContent(progressBar);
        App.mainScreen.setCenter(scrollPane);
        Message message = new Message(Message.getCoursesForExamsCheck);
        message.setUser(App.user);
        App.client.sendMessageToServer(message);
    }

    public void receiveCoursesForExamsCheck(List<Course> courses) {
        ExamBoundary.courses = courses;
        VBox vbox = new VBox(50);
        selectorPane = new HBox();
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
        searchButton.setOnAction(e -> examController.getExamsToCheck(choiceBox.getSelectionModel().getSelectedItem()));

        selectorPane.getChildren().addAll(choiceBox, searchButton);
        vbox.getChildren().addAll(selectorPane, pane);
        App.mainScreen.setCenter(vbox);
    }

    public void receiveExamsToCheck(List<ExamCopy> exams) {
        VBox vbox = new VBox(20);
        System.out.println(exams.size());
        for (ExamCopy exam : exams) {
            Text examId = new Text("Exam Id: " + exam.getId());
            Text student = new Text("Student: " + exam.getStudent().getId());
            Button checkbtn = new Button("Check");
            checkbtn.setOnAction(e -> showExamToCheck(exam));
            HBox hBox = new HBox(30);
            hBox.getChildren().addAll(examId, student, checkbtn);
            vbox.getChildren().add(hBox);
        }
        pane.setContent(vbox);
    }

    public void showExamToCheck(ExamCopy exam) {
        VBox vbox = new VBox(20);
        if (exam.getExecutableExam().getType().equals("regular")) {
            for (Answer answer : exam.getAnswers()) {
                Text title = new Text(answer.getQuestion().getTitle());
                Text ansA = new Text("A.  " + answer.getQuestion().getAnswerA());
                Text ansB = new Text("B.  " + answer.getQuestion().getAnswerA());
                Text ansC = new Text("C.  " + answer.getQuestion().getAnswerA());
                Text ansD = new Text("D.  " + answer.getQuestion().getAnswerA());
                Text correctAns = new Text("Correct Answer: " + answer.getQuestion().getCorrectAns());
                Text studentAns = new Text("Student Answer: " + answer.getAnswer());

                VBox temp = new VBox(10);
                temp.getChildren().addAll(title, ansA, ansB, ansC, ansD, correctAns, studentAns);

                vbox.getChildren().add(temp);
            }
            Text grade = new Text(exam.getGrade() + " / " + exam.getExecutableExam().getRegularExam().getMaxGrade());
            vbox.getChildren().add(grade);
        } else {
            Button questionnaire = new Button("Download Questionnaire");
            questionnaire.setOnAction(e -> examController.downloadExam(exam.getExecutableExam().getDocumentExam().getFile()));
            Button answer = new Button("Download Answer");
            answer.setOnAction(e -> examController.downloadExam(exam.getAnswerDoc()));
            HBox hbox = new HBox(20);
            hbox.getChildren().addAll(questionnaire, answer);
            vbox.getChildren().add(hbox);
            Text grade = new Text(exam.getGrade() + " / " + exam.getExecutableExam().getDocumentExam().getMaxGrade());
            vbox.getChildren().add(grade);
        }


        Text newGrade = new Text("New Grade: ");
        Text notes = new Text("Notes For Grade Change: ");

        TextField gradeChange = new TextField();
        TextField notesForGradeChange = new TextField();

        HBox gradeHBox = new HBox(20);
        HBox notesHBox = new HBox(20);

        gradeHBox.getChildren().addAll(newGrade, gradeChange);
        notesHBox.getChildren().addAll(notes, notesForGradeChange);

        Button savebtn = new Button("Save");
        savebtn.setOnAction(e -> {
            if (!gradeChange.getText().equals(""))
                exam.setGrade(Integer.parseInt(gradeChange.getText()));
            if (!notesForGradeChange.getText().equals(""))
                exam.setNotesForGradeChange(notesForGradeChange.getText());
            exam.setChecked(true);
            Message message = new Message(Message.finalCopy, exam);
            App.client.sendMessageToServer(message);
        });

        vbox.getChildren().addAll(gradeHBox, notesHBox, savebtn);

        gradeChange.textProperty().addListener( (item, oldValue, newValue) -> {
            // xor
            savebtn.setDisable((gradeChange.getText().equals("") && !notesForGradeChange.getText().equals("")) ||
                    (!gradeChange.getText().equals("") && notesForGradeChange.getText().equals("")));

            if (!newValue.equals("")) {
                if (!(newValue.matches("\\d{0,100}?"))) {
                    savebtn.setDisable(true);
                    gradeChange.setText(oldValue);
                } else if (Integer.parseInt(newValue) > exam.getExecutableExam().getRegularExam().getMaxGrade())
                    gradeChange.setText(oldValue);
            }
        });

        notesForGradeChange.textProperty().addListener( (item, oldValue, newValue) -> {
            savebtn.setDisable((gradeChange.getText().equals("") && !notesForGradeChange.getText().equals("")) ||
                    (!gradeChange.getText().equals("") && notesForGradeChange.getText().equals("")));
        });

        pane.setContent(vbox);
    }



}
