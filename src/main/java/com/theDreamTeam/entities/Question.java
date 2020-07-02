package com.theDreamTeam.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)    // each class in the inheritance hierarchy
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;

    private String answerA;

    private String answerB;

    private String answerC;

    private String answerD;

    private String correctAns;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Course course;

    public Question() { }   // ctor for hibernate

    public Question(String title, String answerA, String answerB, String answerC,
                    String answerD, String correctAns, Course course) {
        this.title = title;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAns = correctAns;
        this.course = course;
        course.addQuestion(this);
    }

    public Question(Question question) {
        this.title = question.getTitle();
        this.answerA = question.getAnswerA();
        this.answerB = question.getAnswerB();
        this.answerC = question.getAnswerC();
        this.answerD = question.getAnswerD();
        this.correctAns = question.getCorrectAns();
        this.course = question.getCourse();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
