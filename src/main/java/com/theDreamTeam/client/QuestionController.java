package com.theDreamTeam.client;

import com.theDreamTeam.entities.GradedQuestion;
import com.theDreamTeam.entities.Message;
import com.theDreamTeam.entities.Query;
import com.theDreamTeam.entities.Question;

public class QuestionController {

    public GradedQuestion makeGradedQuestion(Question question, int grade) {
        return (new GradedQuestion(question, grade));
    }

    public void saveQuestion(Question question) {
        Message message = new Message(Query.saveQuestion, question);
        App.client.sendMessageToServer(message);
    }


}
