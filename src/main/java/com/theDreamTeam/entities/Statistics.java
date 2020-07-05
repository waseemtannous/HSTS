package com.theDreamTeam.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Statistics implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ExecutableExam executableExam;

    private int numOfGrades;

    private int median;

    private double average;

    private int numOfFinished;

    @Column(length = 1024)
    private int[] decimal_Distribution = new int[101];

    public Statistics() {}

    public Statistics(ExecutableExam executableExam) {
        this.executableExam = executableExam;
        this.average = 0;
        this.median = 0;
        this.numOfFinished = 0;
        this.numOfGrades = 0;
        for (int i = 0; i < 101; i++)
            decimal_Distribution[i] = 0;
    }

    public void addGrade(int grade, boolean finished) {
        numOfFinished =  finished ? numOfFinished + 1 : numOfFinished;
        decimal_Distribution[grade]++;
        average = average * numOfGrades + grade;
        average /= ++numOfGrades;
        int temp = 0;
        for (int i = 0; i < 101; i++) {
            temp += decimal_Distribution[i];
            if(temp >= numOfGrades/2 + 1) {
                this.median = i;
                break;
            }
        }
    }

    public int getMedian() {
        return median;
    }

    public void setMedian(int median) {
        this.median = median;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public int[] getDecimal_Distribution() {
        return decimal_Distribution;
    }

    public void setDecimal_Distribution(int[] getDecimal_distribution) {
        this.decimal_Distribution = getDecimal_distribution;
    }

    public int getNumOfGrades() {
        return numOfGrades;
    }

    public ExecutableExam getExecutableExam() {
        return executableExam;
    }

    public void setExecutableExam(ExecutableExam executableExam) {
        this.executableExam = executableExam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumOfGrades(int numOfGrades) {
        this.numOfGrades = numOfGrades;
    }

    public int getNumOfFinished() {
        return numOfFinished;
    }

    public void setNumOfFinished(int numOfFinished) {
        this.numOfFinished = numOfFinished;
    }
}
