package com.theDreamTeam.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Teacher extends User implements Serializable {

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "join_teacher_course",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RegularExam> writtenRegularExams = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocumentExam> writtenDocumentExams = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExecutableExam> writtenExecutableExams = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamCopy> writtenExamsCopy = new ArrayList<>();

    public Teacher() { }    // ctor for hibernate

    public Teacher(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses=courses;
        for (Course course : courses) {
			course.getTeachers().add(this); 
		}
        
    }
    
    public void addExamCopy(ExamCopy...examCopies) {
    	for(ExamCopy examCopy: examCopies) {
    		this.getWrittenExamsCopy().add(examCopy);
    	}
    	
    }
    
    public void addExecutableExam(ExecutableExam ...executableExams) {
    	for(ExecutableExam executableExam: executableExams) {
    		this.getWrittenExecutableExams().add(executableExam);
    		executableExam.setExecTeacher(this);
    	}
    	
    }
    
    public void addDocumentExam(DocumentExam...documentExams) {
    	
    	for(DocumentExam docExam: documentExams) {
    		this.getWrittenDocumentExams().add(docExam);
    		docExam.setTeacher(this);
    		
    	}
    }
    
    public void addRegularExam(RegularExam...regularExams) {
    	for(RegularExam regularExam: regularExams) {
    		this.getWrittenRegularExams().add(regularExam);
    		regularExam.setTeacher(this);
    	}
    	
    }

    public void addCourse(Course ...courses ) {
    	for(Course course: courses) {
    	this.getCourses().add(course);
    	course.getTeachers().add(this);
    	}    	
    	
    }
    
    public List<ExecutableExam> getWrittenExecutableExams() {
		return writtenExecutableExams;
	}

	public void setWrittenExecutableExams(List<ExecutableExam> writtenExecutableExams) {
		//this.writtenExecutableExams = writtenExecutableExams;
		for(ExecutableExam exam:writtenExecutableExams)
		{
			exam.setExecTeacher(this);
		}
	}
	

	public List<RegularExam> getWrittenRegularExams() {
        return writtenRegularExams;
    }

    public void setWrittenRegularExams(List<RegularExam> writtenRegularExams) {
        this.writtenRegularExams = writtenRegularExams;
    }

    public List<DocumentExam> getWrittenDocumentExams() {
        return writtenDocumentExams;
    }

    public void setWrittenDocumentExams(List<DocumentExam> writtenDocumentExams) {
        this.writtenDocumentExams = writtenDocumentExams;
    }
    
    public List<ExamCopy> getWrittenExamsCopy() {
		return writtenExamsCopy;
	}

	public void setWrittenExamsCopy(List<ExamCopy> writtenExamsCopy) {
		this.writtenExamsCopy = writtenExamsCopy;
	}

	public List<Exam> getAllWrittenExams() {
        List<Exam> writtenExams = new ArrayList<>();
        writtenExams.addAll(writtenRegularExams);
        writtenExams.addAll(writtenDocumentExams);
        return writtenExams;
    }
    
    public void addCourse(Course course) {
        this.courses.add(course);
        course.getTeachers().add(this);
	}
}