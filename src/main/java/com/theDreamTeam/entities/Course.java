package com.theDreamTeam.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course implements Serializable {

    @Id
    private int id;

    private int questionId;

    private int ewxamId;

    private String name;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RegularExam> regularExams = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocumentExam> documentExams = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "courses")
    private List<Teacher> teachers = new ArrayList<>();

    public Course() {
    } // ctor for hibernate

    public Course(String name, List<Teacher> teachers) {
        questionId = 0;
        ewxamId = 0;
        this.name = name;
        for (Teacher teacher : teachers)
            addTeacher(teacher);
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getEwxamId() {
        return ewxamId;
    }

    public void setEwxamId(int ewxamId) {
        this.ewxamId = ewxamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
        for (Student student : students) {
        	student.addCourse(this);
		}
    }

    public List<RegularExam> getRegularExams() {
        return regularExams;
    }

    public void setRegularExams(List<RegularExam> regularExams) {
        this.regularExams = regularExams;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<DocumentExam> getDocumentExams() {
        return documentExams;
    }

    public void setDocumentExams(List<DocumentExam> documentExams) {
        this.documentExams = documentExams;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
    	this.teachers=teachers;
    	for (Teacher teacher : teachers) {
			teacher.addCourse(this);
		}
    }

    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        for (RegularExam reg : getRegularExams()) {
            System.out.println("reg");
            exams.add(reg);
        }
        for (DocumentExam doc : getDocumentExams()) {
            System.out.println("doc");
            exams.add(doc);
        }
        return exams;
    }

    public void addStudent(Student...students) {
    	for(Student student: students) {
    		this.getStudents().add(student);
    		student.getCourses().add(this);
    	}
    }

    public void addTeacher(Teacher...teachers) {
    	for(Teacher teacher: teachers) {
    		this.getTeachers().add(teacher);
    		teacher.getCourses().add(this);
    	}
    }

    public void addDocumentExam(DocumentExam...documentExams) {
    	for(DocumentExam documentExam: documentExams) {
    		this.getDocumentExams().add(documentExam);
    		documentExam.setCourse(this);
    	}
    }

    public void addRegularExam(RegularExam...regularExams) {
        for(RegularExam regularExam:regularExams) {
            this.getRegularExams().add(regularExam);
            regularExam.setCourse(this);
        }
    }

    public void addQuestion(Question...questions) {
    	for(Question question:questions) {
    		this.getQuestions().add(question);
    		question.setCourse(this);
    	}
    }

}
