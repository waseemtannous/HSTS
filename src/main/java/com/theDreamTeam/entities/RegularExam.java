package com.theDreamTeam.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RegularExam extends Exam implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<GradedQuestion> gradedQuestions = new ArrayList<>();

    public RegularExam() { }    // ctor for hibernate

    public RegularExam(Exam exam, List<GradedQuestion> gradedQuestions) {
        super(exam);
        this.gradedQuestions = gradedQuestions;
    }

   
    public List<GradedQuestion> getGradedQuestions() {
        return gradedQuestions;
    }

    public void setGradedQuestions(List<GradedQuestion> gradedQuestions) {
        this.gradedQuestions = gradedQuestions;
    }
}
