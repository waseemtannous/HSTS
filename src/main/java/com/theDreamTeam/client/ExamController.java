package com.theDreamTeam.client;

import com.theDreamTeam.entities.*;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamController {

    public static ActivityMain activityMain = new ActivityMain();

    private static final ExamBoundary examBoundary = new ExamBoundary();

    private static final QuestionController questionController = new QuestionController();

    public static ExecutableExam executableExam;

    public static List<Course> courses;

    public static RegularExam newRegularExam;

    public static List<GradedQuestion> questionsForNewExam = new ArrayList<>();

    public static DocumentExam newDocumentExam;

    public static int maxGradeForNewExam = 0;

    public static int examTime;

    public static int timeElapsed;

    public static Thread timer;

    public static byte[] answerDoc;

    public static List<Answer> answers;

    public static List<GradedQuestion> examQuestions;

    public static int answerIndex;

    public static boolean inExam = false;

    public static boolean finished = true;

    //    Execute Exam Methods

    public void getExamByCode(String code) {
        Message message = new Message(Message.getExamByCode, code);
        App.client.sendMessageToServer(message);
    }

    public void receiveExamByCode(ExecutableExam executableExam) {
        ExamController.executableExam = executableExam;
        startExam();
    }

    public void invalidCode() {
        ActivityMain.errorHandle("Invalid Code.");
    }

    public void invalidStudent() {
        ActivityMain.errorHandle("You Can't Enter This Exam.");
    }

    public void startExam() {
        inExam = true;
        finished = true;

        ExamController.examTime = (executableExam.getType().equals("regular") ?
                executableExam.getRegularExam().getDuration() :
                executableExam.getDocumentExam().getDuration());

        timer = new Thread(() -> {
            Instant start = Instant.now();
            Instant finish = Instant.now();

            while (Duration.between(start, finish).toMinutes() < examTime) {
                finish =Instant.now();
                timeElapsed = (int) Duration.between(start, finish).toMinutes();
            }
            Platform.runLater(() -> timeRanOut(executableExam));
        });

        examBoundary.startExam(executableExam);
        ActivityMain.studentDisableButtons();
        timer.start();
    }

    public void timeRanOut(ExecutableExam executableExam) {
        ExamController.finished = false;
        finishExam(executableExam);
    }

    public void finishExam(ExecutableExam executableExam) {
        inExam = false;
        ActivityMain.studentEnableButtons();
        App.mainScreen.setCenter(ActivityMain.welcome);
        if (!finished)
            ActivityMain.errorHandle("Time Ran Out.");
        if (executableExam.getType().equals("regular")) {
            ExamCopy examCopy = new ExamCopy((Student) App.user, executableExam, answers, finished);
            Message message = new Message(Message.saveExamCopy, examCopy);
            App.client.sendMessageToServer(message);
        } else {
            //todo document
            ExamCopy examCopy = new ExamCopy((Student) App.user, executableExam, answerDoc, finished);
            Message message = new Message(Message.saveExamCopy, examCopy);
            App.client.sendMessageToServer(message);
        }
    }

    public void extendTime(ExtendTimeRequest extendTimeRequest) {
        if ((extendTimeRequest.getExecutableExam().getId() == executableExam.getId()) && inExam) {
            examTime += extendTimeRequest.getAddedTime();
            long curTimeInMs = ExamBoundary.end.getTime();
            ExamBoundary.end = new Date(curTimeInMs + (long)(ExamController.examTime * 60000));
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            ExamBoundary.endTime.setText("Finish Time: " + formatter.format(ExamBoundary.end));
            ActivityMain.errorHandle("Time Extended.");
        }
    }

    public void receiveCoursesForReports(List<Course> courses) {
        ExamController.courses = courses;
    }

    public void checkExam(String choiceBoxSelection, String duration,
                          String notesForTeacher, String notesForStudent) {
        if (choiceBoxSelection.equals("regular")) {
            boolean isValid = true;
            int grade = 0;
            if (duration.equals(""))
                isValid = false;
            if (questionsForNewExam.size() == 0) {
                isValid = false;
            } else {
                for (GradedQuestion gradedQuestion : questionsForNewExam)
                    grade += gradedQuestion.getGrade();
                if (grade != 100)
                    isValid = false;
            }
//
            if (isValid) {
                System.out.println("is valid exam check1");
                Exam exam = new Exam(notesForTeacher, notesForStudent, (Teacher) App.user, ExamBoundary.selectedCourse,
                        Integer.parseInt(duration), grade);
                System.out.println("is valid exam check2");
                newRegularExam = new RegularExam(exam, questionsForNewExam);
                System.out.println("is valid exam check3");
                Message message = new Message(Message.saveNewRegularExam, newRegularExam);
                System.out.println("is valid exam check4");
                App.client.sendMessageToServer(message);
                System.out.println("is valid exam check5");
            } else {
                ActivityMain.errorHandle("Invalid Exam.");
            }
        } else {
            boolean isvalid = true;
            if (ExamBoundary.document == null)
                isvalid = false;
            if (duration.equals(""))
                isvalid = false;
            if (isvalid) {
                Exam exam = new Exam(notesForTeacher, notesForStudent, (Teacher) App.user, ExamBoundary.selectedCourse,
                        Integer.parseInt(duration), 100);
                newDocumentExam = new DocumentExam(exam, ExamBoundary.document);
                Message message = new Message(Message.saveNewDocumentExam, newDocumentExam);
                System.out.println("is valid exam check4");
                App.client.sendMessageToServer(message);
                System.out.println("is valid exam check5");
            }
        }
    }

//    Make Exam Methods

    public void makeExam() {

    }

    public void makeExecutableExam() {

    }

    public void editExam() {

    }

    public void getExamsDrawer(String courseName, List<Course> courses) {
        String name;
        Course course;
        List<Exam> exams;
        for (Course temp : courses) {
            course = temp;
            name = course.getName() + " " + course.getId();
            if (name.equals(courseName)) {
//                examBoundary.showExamsDrawer(course.getExams(), course);
            }
        }

    }

    public void saveExecutableExam(ExecutableExam executableExam) {
        Message message = new Message(Message.saveExecutableExam, executableExam);
        App.client.sendMessageToServer(message);
    }

    public void downloadExam(byte[] bytes) {
        Stage stage = new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Word", ".doc", ".docx"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                OutputStream os = new FileOutputStream(file);
                // Starts writing the bytes in it
                os.write(bytes);
            } catch (IOException ex) {
                ActivityMain.errorHandle("An Error Has Occurred.");
            }
        }
    }

    public byte[] uploadDocument() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Word", ".doc", ".docx"));
        File file = fileChooser.showOpenDialog(stage);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException exception) {
            ActivityMain.errorHandle("An Error Has Occurred.");
        }
        return null;
    }

    public void getCopies(String name) {
        Course selectedCourse = null;
        for (Course course : ExamBoundary.courses) {
            if (course.getName().equals(name))
                selectedCourse = course;
        }
        assert selectedCourse != null;
        Message message = new Message(Message.getExamsCopies, selectedCourse.getId());
        App.client.sendMessageToServer(message);
    }

    public void getExamsToCheck(String name) {
        Course selectedCourse = null;
        for (Course course : ExamBoundary.courses) {
            if (course.getName().equals(name))
                selectedCourse = course;
        }
        assert selectedCourse != null;
        Message message = new Message(Message.getExamsToCheck, selectedCourse.getId());
        App.client.sendMessageToServer(message);
    }
}
