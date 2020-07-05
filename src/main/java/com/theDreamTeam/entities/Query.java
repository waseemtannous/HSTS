package com.theDreamTeam.entities;


public enum Query {
    saveQuestion, receiveQuestionsDrawer, requestAllExam, receiveExamCopy,
    getExamByCode, receiveExamByCode, invalidExamCode, invalidStudent,
    receiveCoursesForReports, logIn, getExtendTimeRequests, extendTimeRequestAnswer,
    saveNewRegularExam, saveExecutableExam, getExamsToCheck, saveExamCopy,
    logInFailed, logout, finalCopy, extraTime, sendExtraTime,
    logInSuccessful, receiveExamsCopies, getExamsCopies, getCoursesForQuestionsDrawer,
    getCoursesForExamsDrawer, receiveExtendTimeRequests, receiveCoursesForQuestionsDrawer,
    receiveExamsDrawer, getCoursesForExamsCopies, receiveExamsToCheck, receiveCoursesForExamsCopies,
    userAlreadyConnected, getQuestionsDrawer, getExamsDrawer, receiveCoursesForExamsDrawer,
    getCoursesForExamsCheck, receiveCoursesForExamsCheck, saveNewDocumentExam, getCoursesForStats,
    receiveCoursesForStats, getStats, receiveStats, extraTimeRequestFromTeacher, wrongCode
}