package com.theDreamTeam.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends User implements Serializable {

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "join_student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamCopy> exams = new ArrayList<>();

    public Student() { } // ctor for hibernate

    public Student(String username, String password) {
        super(username, password);
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
    	this.courses=courses;
    	for (Course course : courses) {
			course.getStudents().add(this); 
		}
    }

    public List<ExamCopy> getExams() {
        return exams;
    }
    
    public void addExamCopy (ExamCopy...examCopies) {
    	for(ExamCopy examCopy: examCopies) {
    		this.getExams().add(examCopy);
    		examCopy.setStudent(this);
    	}
    }

    public void setExams(List<ExamCopy> exams) {
    	this.exams=exams;
//    	for (ExamCopy exam : exams) {
//    		exam.setStudent(this);
//    	}
    }
    /////////////////////////////////////////////////////////////////////////////////////
    public void addCourse(Course... courses) {
		for (Course course : courses) {
			this.courses.add(course);
			course.getStudents().add(this); 
		}
	}
//    
//    public void addExamCopy(ExamCopy... exams) {
//		for (ExamCopy exam : exams) {
//			this.exams.add(exam);
//			exam.setStudent(this); 
//		}
//	}
   ////////////////////////////////////////////////////////////////////////////////////// 
    
}
