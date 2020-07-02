package com.theDreamTeam.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Answer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GradedQuestion question;

    private String answer;

    private int points;

    public Answer() { }

    public Answer(GradedQuestion question) {
        this.question = question;
        this.answer = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GradedQuestion getQuestion() {
        return question;
    }

    public void setQuestion(GradedQuestion question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
