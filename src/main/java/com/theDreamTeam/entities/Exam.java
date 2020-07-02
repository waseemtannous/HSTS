package com.theDreamTeam.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Exam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Teacher teacher;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Course course;

    private String notesForTeacher;

    private String notesForStudent;

    private int duration;

    private int maxGrade;

    public Exam() { }   // ctor for hibernate

    public Exam(Exam exam) {
        this.notesForTeacher = exam.getNotesForTeacher();
        this.notesForStudent = exam.getNotesForStudent();
        this.teacher = exam.getTeacher();
        this.course = exam.getCourse();
        this.duration = exam.getDuration();
        this.maxGrade = exam.getMaxGrade();
    }

    public Exam(String notesForTeacher, String notesForStudent,
                Teacher teacher, Course course, int duration, int maxGrade) {
        this.notesForTeacher = notesForTeacher;
        this.notesForStudent = notesForStudent;
        this.teacher = teacher;
        this.course = course;
        this.duration = duration;
        this.maxGrade = maxGrade;
    }

    public int getId() {
        return id;
    }

    public String getNotesForTeacher() {
        return notesForTeacher;
    }

    public void setNotesForTeacher(String notesForTeacher) {
        this.notesForTeacher = notesForTeacher;
    }

    public String getNotesForStudent() {
        return notesForStudent;
    }

    public void setNotesForStudent(String notesForStudent) {
        this.notesForStudent = notesForStudent;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(int maxGrade) {
        this.maxGrade = maxGrade;
    }
}
