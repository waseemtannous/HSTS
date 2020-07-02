package com.theDreamTeam.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ExtendTimeRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Teacher teacher;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ExecutableExam executableExam;
    
    private String type = "";

    private int addedTime;

    private String explanation;

    private boolean answer;

    public ExtendTimeRequest() {}

    public ExtendTimeRequest(Teacher teacher, ExecutableExam executableExam, int addedTime, String explanation) {
        this.teacher = teacher;
        this.executableExam = executableExam;
        this.addedTime = addedTime;
        this.explanation = explanation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public ExecutableExam getExecutableExam() {
        return executableExam;
    }

    public void setExecutableExam(ExecutableExam executableExam) {
        this.executableExam = executableExam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(int addedTime) {
        this.addedTime = addedTime;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}
