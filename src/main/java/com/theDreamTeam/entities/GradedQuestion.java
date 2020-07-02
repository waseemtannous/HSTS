package com.theDreamTeam.entities;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class GradedQuestion extends Question implements Serializable {

    private int grade;

    private int parentId;

    public GradedQuestion() { } // ctor for hibernate

    public GradedQuestion(Question question, int grade) {
        super(question);
        this.grade = grade;
        this.parentId = question.getId();
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}
