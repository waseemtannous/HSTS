package com.theDreamTeam.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class DocumentExam extends Exam implements Serializable {

    @Column(length = 100000000) // 100MB
    private byte[] file;

    public DocumentExam() {
    }

    public DocumentExam(Exam exam, byte[] file) {
        super(exam);
        this.file = file;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
