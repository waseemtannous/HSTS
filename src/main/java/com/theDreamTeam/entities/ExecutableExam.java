package com.theDreamTeam.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ExecutableExam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Teacher execTeacher;
	
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DocumentExam documentExam;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RegularExam regularExam;

    private String type = "";

    private int numberOfFinished;

    private int numberOfUnfinished;

    public ExecutableExam() { } // ctor for hibernate

    public ExecutableExam(String code, DocumentExam documentExam, Teacher execTeacher) {
        this.code = code;
        this.documentExam = documentExam;
        this.execTeacher = execTeacher;
        this.type = "document";
    }

    public ExecutableExam(String code, RegularExam regularExam, Teacher execTeacher) {
        this.code = code;
        this.regularExam = regularExam;
        this.type = "regular";
        this.execTeacher = execTeacher;
    }

    public Exam getExam() {
        if (type.equals("regular"))
            return regularExam;
        else
            return documentExam;
    }
    
    public Teacher getExecTeacher() {
		return execTeacher;
	}

	public void setExecTeacher(Teacher teacher) {
		this.execTeacher = teacher;
		teacher.getWrittenExecutableExams().add(this);
	}
	

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DocumentExam getDocumentExam() {
        return documentExam;
    }

    public void setDocumentExam(DocumentExam documentExam) {
        this.documentExam = documentExam;
    }

    public RegularExam getRegularExam() {
        return regularExam;
    }

    public void setRegularExam(RegularExam regularExam) {
        this.regularExam = regularExam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumberOfFinished() {
        return numberOfFinished;
    }

    public void setNumberOfFinished(int numberOfFinished) {
        this.numberOfFinished = numberOfFinished;
    }

    public int getNumberOfUnfinished() {
        return numberOfUnfinished;
    }

    public void setNumberOfUnfinished(int numberOfUnfinished) {
        this.numberOfUnfinished = numberOfUnfinished;
    }
}
