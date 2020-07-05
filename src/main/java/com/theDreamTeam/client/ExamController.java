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
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamController {

    private static final ExamBoundary examBoundary = new ExamBoundary();

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


    public void getExamByCode(String code) {
        Message message = new Message(Query.getExamByCode, code);
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
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        Month month = localDate.getMonth();
        int day = localDate.getDayOfMonth();

        int monthInt = 0;

        switch (month) {
            case JANUARY:
                monthInt = 1;
                break;

            case FEBRUARY:
                monthInt = 2;
                break;

            case MARCH:
                monthInt = 3;
                break;

            case APRIL:
                monthInt = 4;
                break;

            case MAY:
                monthInt = 5;
                break;

            case JUNE:
                monthInt = 6;
                break;

            case JULY:
                monthInt = 7;
                break;

            case AUGUST:
                monthInt = 8;
                break;

            case SEPTEMBER:
                monthInt = 9;
                break;

            case OCTOBER:
                monthInt = 10;
                break;

            case NOVEMBER:
                monthInt = 11;
                break;

            case DECEMBER:
                monthInt = 12;
                break;
        }

        if (!finished)
            ActivityMain.errorHandle("Time Ran Out.");
        if (executableExam.getType().equals("regular")) {
            ExamCopy examCopy = new ExamCopy((Student) App.user, executableExam, answers, finished);
            examCopy.setDay(day);
            examCopy.setMonth(monthInt);
            examCopy.setYear(year);
            long duration = ExamBoundary.start.getTime() - ((new Date()).getTime());
            duration = duration / 60000;
            examCopy.setDuration(duration);
            Message message = new Message(Query.saveExamCopy, examCopy);
            App.client.sendMessageToServer(message);
        } else {
            ExamCopy examCopy = new ExamCopy((Student) App.user, executableExam, answerDoc, finished);
            examCopy.setDay(day);
            examCopy.setMonth(monthInt);
            examCopy.setYear(year);
            Message message = new Message(Query.saveExamCopy, examCopy);
            App.client.sendMessageToServer(message);
        }
    }

    public void extendTime(ExtendTimeRequest extendTimeRequest) {
        if (inExam && (extendTimeRequest.getExecutableExam().getId() == executableExam.getId())) {
            examTime += extendTimeRequest.getAddedTime();
            long curTimeInMs = ExamBoundary.end.getTime();
            ExamBoundary.end = new Date(curTimeInMs + (long)(extendTimeRequest.getAddedTime() * 60000));
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
                Exam exam = new Exam(notesForTeacher, notesForStudent, (Teacher) App.user, ExamBoundary.selectedCourse,
                        Integer.parseInt(duration), grade);
                newRegularExam = new RegularExam(exam, questionsForNewExam);
                Message message = new Message(Query.saveNewRegularExam, newRegularExam);
                App.client.sendMessageToServer(message);
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
                Message message = new Message(Query.saveNewDocumentExam, newDocumentExam);
                App.client.sendMessageToServer(message);
            }
        }
    }

    public void saveExecutableExam(ExecutableExam executableExam) {
        Message message = new Message(Query.saveExecutableExam, executableExam);
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
        byte[] bytes = null;
        try {
            if (file == null)
                return null;
            Path path = file.toPath();
            bytes = Files.readAllBytes(path);
        } catch (IOException exception) {
            ActivityMain.errorHandle("An Error Has Occurred.");
        }
        return bytes;
    }

    public void getCopies(String name) {
        Course selectedCourse = null;
        for (Course course : ExamBoundary.courses) {
            if (course.getName().equals(name))
                selectedCourse = course;
        }
        assert selectedCourse != null;
        Message message = new Message(Query.getExamsCopies, selectedCourse.getId());
        App.client.sendMessageToServer(message);
    }

    public void getExamsToCheck(String name) {
        Course selectedCourse = null;
        for (Course course : ExamBoundary.courses) {
            if (course.getName().equals(name))
                selectedCourse = course;
        }
        assert selectedCourse != null;
        Message message = new Message(Query.getExamsToCheck, selectedCourse.getId());
        App.client.sendMessageToServer(message);
    }
}
