package com.theDreamTeam.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ExamCopy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Student student;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ExecutableExam executableExam;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers = new ArrayList<>();

    @Column(length = 100000000)
    private byte[] answerDoc;

    private String type = "";

    private String notesForGradeChange = "";
	
    private int grade;

    private boolean finished;

    private boolean checked = false;

    public ExamCopy() { }   // ctor for hibernate

    public ExamCopy(Student student, ExecutableExam executableExam, List<Answer> answers, boolean finished) {
        this.student = student;
        this.executableExam = executableExam;
        this.answers = answers;
        this.type = "regular";
        this.finished = finished;
    }

    public ExamCopy(Student student, ExecutableExam executableExam, byte[] answerDoc, boolean finished) {
        this.student = student;
        this.executableExam = executableExam;
        this.type = "document";
        this.finished = finished;
        this.answerDoc = answerDoc;
    }

    public int getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }
	
	public void setStudent(Student student) {
        this.student = student;
        student.getExams().add(this);
    }

    public ExecutableExam getExecutableExam() {
        return executableExam;
    }

    public void setExecutableExam(ExecutableExam executableExam) {
        this.executableExam = executableExam;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotesForGradeChange() {
        return notesForGradeChange;
    }

    public void setNotesForGradeChange(String notesForGradeChange) {
        this.notesForGradeChange = notesForGradeChange;
    }

    public int getGrade() {
        return grade;
    }
    
    public Teacher getExecTeacher(ExecutableExam executableExam)
    {
    	return executableExam.getExecTeacher();
    }
    

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public byte[] getAnswerDoc() {
        return answerDoc;
    }

    public void setAnswerDoc(byte[] answerDoc) {
        this.answerDoc = answerDoc;
    }
}