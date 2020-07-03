package com.theDreamTeam.entities;


import java.io.Serializable;

// this class is intended for sending messages between the server and the client.
public class Message implements Serializable {

    public static final int requestAllQuestions = 1;

    public static final int saveQuestion = 2;

    public static final int deleteQuestion = 3;

    public static final int receiveQuestionsDrawer = 4;

    public static final int requestAllExam = 5;

    public static final int saveExam = 6;

    public static final int deleteExam = 7;

    public static final int receiveExam = 8;

    public static final int startExam = 9;

    public static final int checkExam = 10;

    public static final int receiveExamCopy = 11;

    public static final int checkExamCopy = 12;

    public static final int getExamByCode = 13;

    public static final int receiveExamByCode = 14;

    public static final int invalidExamCode = 15;

    public static final int invalidStudent = 16;

    public static final int getExams = 17;

    public static final int getCourses = 18;

    public static final int receiveCoursesForReports = 19;

    public static final int logIn = 20;

    public static final int getExtendTimeRequests = 21;

    public static final int extendTimeRequestAnswer = 22;

    public static final int getCoursesForDrawer = 23;

    public static final int saveNewRegularExam = 24;

    public static final int saveExecutableExam = 25;

    public static final int getExamsToCheck = 26;

    ////////////////////////////////////////////////////////
    public static final int saveExamCopy = 27;

    public static final int startExamStudent = 28;

    public static final int logInFailed = 29;

    public static final int logout = 30;

    public static final int finalCopy = 31;

    public static final int extraTime = 32;

    public static final int sendExtraTime = 33;

    public static final int logInSuccessful = 34;

    public static final int receiveExamsCopies = 35;

    public static final int getExamsCopies = 36;

    public static final int getCoursesForQuestionsDrawer = 37;

    public static final int getCoursesForExamsDrawer = 38;

    public static final int receiveExtendTimeRequests = 39;

    public static final int receiveCoursesForQuestionsDrawer = 40;

    public static final int receiveExamsDrawer = 41;

    public static final int getCoursesForExamsCopies = 42;

    public static final int receiveExamsToCheck = 43;

    public static final int receiveCoursesForExamsCopies = 44;

    public static final int userAlreadyConnected = 45;

    public static final int getQuestionsDrawer = 46;

    public static final int getExamsDrawer = 47;

    public static final int receiveCoursesForExamsDrawer = 48;

    public static final int getCoursesForExamsCheck = 49;

    public static final int receiveCoursesForExamsCheck = 50;

    public static final int saveNewDocumentExam = 51;

    public static final int getCoursesForStats = 52;

    public static final int receiveCoursesForStats = 53;

    public static final int getStats = 54;

    public static final int receiveStats = 55;

    public static final int extraTimeRequestFromTeacher = 56;

    public static final int wrongCode = 57;

    private final int msg;

    private Object object;

    private User user;

    public Message(int msg) {
        this.msg = msg;
    }

    public Message(int msg, Object object) {
        this.msg = msg;
        this.object = object;
    }

    public int getMsg() {
        return msg;
    }


    public Object getObject() {
        return object;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
