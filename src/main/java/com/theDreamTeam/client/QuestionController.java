package com.theDreamTeam.client;

import com.theDreamTeam.entities.*;

import java.io.IOException;

public class QuestionController {

    public static final QuestionBoundary questionBoundary = new QuestionBoundary();

    public GradedQuestion makeGradedQuestion(Question question, int grade) {
        return (new GradedQuestion(question, grade));
    }

    public void saveQuestion(Question question) {
        Message message = new Message(Message.saveQuestion, question);
        App.client.sendMessageToServer(message);
    }


}
