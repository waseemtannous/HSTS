package com.theDreamTeam.client;

import com.theDreamTeam.entities.Course;
import com.theDreamTeam.entities.Message;
import com.theDreamTeam.entities.Statistics;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class StatisticsBoundary {

    public static List<Course> courses;

    public static HBox selectorPane;

    public static ScrollPane pane;

    public void getStatistics() {
        ScrollPane scrollPane = new ScrollPane();
        ProgressBar progressBar = new ProgressBar(-0.1f);
        scrollPane.setContent(progressBar);
        App.mainScreen.setCenter(scrollPane);
        Message message = new Message(Message.getCoursesForStats);
        message.setUser(App.user);
        App.client.sendMessageToServer(message);
    }

    public void receiveCourses(List<Course> courses) {
        StatisticsBoundary.courses = courses;
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
        searchButton.setOnAction(e -> {
            Course selectedCourse = null;
            for (Course course : courses) {
                if (course.getName().equals(choiceBox.getSelectionModel().getSelectedItem()))
                    selectedCourse = course;
            }
            assert selectedCourse != null;
            Message message = new Message(Message.getStats, selectedCourse.getId());
            App.client.sendMessageToServer(message);
        });

        selectorPane.getChildren().addAll(choiceBox, searchButton);
        vbox.getChildren().addAll(selectorPane, pane);
        App.mainScreen.setCenter(vbox);
    }

    public void receiveStats(List<Statistics> statistics) {
        ChoiceBox<String> chooseCourse = new ChoiceBox <>();
        List<String> courseNames = new ArrayList<>();
        VBox vBox = new VBox();
        for(int i =0; i < statistics.size(); i++) {
            int index = i;
            Statistics exam = statistics.get(i);
            HBox hbox = new HBox(10);
            Text examId = new Text("Exam Id:  " + statistics.get(i).getExecutableExam().getId());
            Text teacher = new Text("Teacher Id:  " + statistics.get(i).getExecutableExam().getExecTeacher().getId());
            Button showButton = new Button("Show");
            showButton.setOnAction(e -> showStatistics(statistics.get(index)));
            hbox.getChildren().addAll(teacher, examId, showButton);
            vBox.getChildren().add(hbox);
        }
        pane.setContent(vBox);
    }

    public void showStatistics(Statistics statistics) {
        VBox layout = new VBox(10);
        HBox rawad = new HBox(10);
        NumberAxis grade = new NumberAxis();
        grade.setLabel("Number Of Students");
        CategoryAxis range = new CategoryAxis();
        range.setLabel("Range");
        BorderPane bord = new BorderPane();
        BarChart decimal_Distribution = new BarChart(range, grade);
        Text median = new Text("Median: " + statistics.getMedian());
        Text average = new Text("Average: " + statistics.getAverage());

        XYChart.Series grades= new XYChart.Series();

        // SelectExam.getValue() == the exam that I want to see his statistics
        grades.getData().removeAll();

        int [] ranges = new int[10];
        for (int i = 0; i < 100; i++) {
            ranges[i/10] += statistics.getDecimal_Distribution()[i];
        }

        ranges[9] += statistics.getDecimal_Distribution()[100];
        grades.getData().add(new XYChart.Data("0-10",ranges[0]));
        grades.getData().add(new XYChart.Data("11-20", ranges[1]));
        grades.getData().add(new XYChart.Data("21-30", ranges[2]));
        grades.getData().add(new XYChart.Data("31-40", ranges[3]));
        grades.getData().add(new XYChart.Data("41-50", ranges[4]));
        grades.getData().add(new XYChart.Data("51-60", ranges[5]));
        grades.getData().add(new XYChart.Data("61-70", ranges[6]));
        grades.getData().add(new XYChart.Data("71-80", ranges[7]));
        grades.getData().add(new XYChart.Data("81-90", ranges[8]));
        grades.getData().add(new XYChart.Data("91-100", ranges[9]));
        decimal_Distribution.getData().addAll(grades);
        bord.setCenter(decimal_Distribution);

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Finished: " + statistics.getNumOfFinished(), statistics.getNumOfFinished()),
                        new PieChart.Data("Not finished: " + (statistics.getNumOfGrades() - statistics.getNumOfFinished()), (statistics.getNumOfGrades() - statistics.getNumOfFinished())));
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Number Of Students Who Finished The Exam");
        layout.getChildren().addAll(chart, median, average);



        bord.setLeft(layout);
        bord.setBottom(rawad);
        pane.setContent(bord);
    }
}
