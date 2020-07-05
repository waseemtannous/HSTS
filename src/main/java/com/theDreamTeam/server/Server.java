package com.theDreamTeam.server;

import com.theDreamTeam.entities.*;
import com.theDreamTeam.ocsf.server.AbstractServer;
import com.theDreamTeam.ocsf.server.ConnectionToClient;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server extends AbstractServer {


    public Server(int port) {
        super(port);
    }


    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        DataBase.session = DataBase.sessionFactory.openSession();
        DataBase.session.beginTransaction();
        Message message = (Message) msg;
        switch (message.getMsg()) {

            case saveQuestion: // SAVES THE QUESTION
                App.database.saveQuestion((Question) message.getObject(), ((Question) message.getObject()).getCourse());
                List<Question> questions = App.database.getAllQuestions(((Question)message.getObject()).getCourse().getId());
                try {
                    client.sendToClient(new Message(Query.receiveQuestionsDrawer, questions));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case requestAllExam: // RETURNS ALL THE EXAMS IN COURSE
                try {
                    client.sendToClient(
                            new Message(Query.requestAllExam, App.database.getAllExams((Integer) message.getObject())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case saveNewRegularExam: // SAVES THE EXAM
                App.database.saveRegularExam((RegularExam) message.getObject(), ((Exam) message.getObject()).getCourse().getId());
                List<Exam> exams2 = App.database.getExamsByCourseId(((RegularExam) message.getObject()).getCourse().getId());
                try {
                    client.sendToClient(new Message(Query.receiveExamsDrawer, exams2));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case saveNewDocumentExam :
                App.database.saveDocumentExam((DocumentExam) message.getObject(), ((Exam) message.getObject()).getCourse().getId());
                List<Exam> exams9 = App.database.getExamsByCourseId(((DocumentExam) message.getObject()).getCourse().getId());
                try {
                    client.sendToClient(new Message(Query.receiveExamsDrawer, exams9));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case saveExecutableExam: // The teacher starts the exam
                ExecutableExam exam1 = (ExecutableExam) message.getObject();
                boolean success = App.database.saveExecutableExam(exam1);
                int courseId = exam1.getType().equals("regular") ? exam1.getRegularExam().getCourse().getId() : exam1.getDocumentExam().getCourse().getId();
                try{
                    if (success){
                        Message msg1 = new Message(Query.receiveExamsDrawer, App.database.getExamsByCourseId(courseId));
                        client.sendToClient(msg1);
                    } else {
                        Message msg1 = new Message(Query.wrongCode);
                        client.sendToClient(msg1);
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                break;

            case saveExamCopy: // Checks the exam and sends it to the teacher
                checkExam((ExamCopy) message.getObject());
                break;

            case receiveExamCopy: // Returns the examcopy
                try {
                    client.sendToClient(new Message(Query.receiveExamCopy,
                            App.database.receiveExamCopy(((User) client.getInfo("user")).getUsername())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case getExamByCode: // sends the exam to the client
                String code = (String) message.getObject();
                ExecutableExam execExam = App.database.getExecutableExamByCode(code);
                Student student = (Student) client.getInfo("user");
                if (execExam != null) {
                    String type = execExam.getType();
                    for (Course course : student.getCourses()) {
                        if (type.equals("regular")) {
                            if (course.getId() == execExam.getRegularExam().getCourse().getId()) {
                                try {
                                    client.sendToClient(new Message(Query.receiveExamByCode, execExam));
                                    DataBase.session.getTransaction().commit();
                                    DataBase.session.close();
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (course.getId() == execExam.getDocumentExam().getCourse().getId()) {
                            try {
                                client.sendToClient(new Message(Query.receiveExamByCode, execExam));
                                DataBase.session.getTransaction().commit();
                                DataBase.session.close();
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Query.invalidExamCode, null));
                        DataBase.session.getTransaction().commit();
                        DataBase.session.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    client.sendToClient(new Message(Query.invalidStudent, null));
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case logIn:
                User user = (User) message.getObject();
                User validUser = App.database.getUser(user);
                if (validUser != null) {
                    if (validUser.isConnected()) {
                        try {
                            client.sendToClient(new Message(Query.userAlreadyConnected));
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    validUser.setConnected(true);
                    DataBase.session.update(validUser);
                    try {
                        client.sendToClient(new Message(Query.logInSuccessful, validUser));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (validUser instanceof Student) {
                        client.setInfo("user", (Student) validUser);
                    } else {
                        if (validUser instanceof Teacher) {
                            client.setInfo("user", (Teacher) validUser);
                        } else
                            client.setInfo("user", (Manager) validUser);
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Query.logInFailed));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case logout:
                App.database.logOut((User) client.getInfo("user"));
                break;

            case finalCopy: // Saves/publishs the examcopy
                App.database.sendExamCopy((ExamCopy) message.getObject());
                if (((ExamCopy) message.getObject()).getExecutableExam().getType().equals("regular")) {
                    try {
                        client.sendToClient(new Message(Query.receiveExamsToCheck,
                                App.database.getUncheckedExams((Teacher) client.getInfo("user"), ((ExamCopy) message.getObject()).getExecutableExam().getRegularExam().getCourse().getId())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Query.receiveExamsToCheck,
                                App.database.getUncheckedExams((Teacher) client.getInfo("user"), ((ExamCopy) message.getObject()).getExecutableExam().getDocumentExam().getCourse().getId())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case extraTime:
                boolean ans = App.database.extraTimeRequest((Pair<ExtendTimeRequest, String>) message.getObject(), (Teacher) client.getInfo("user"));
                Message message9 = new Message(Query.extraTimeRequestFromTeacher, ans);
                try {
                    client.sendToClient(message9);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case sendExtraTime:
                // NEED TO CHECK WHAT THE MANAGER RESPONDED
                ExtendTimeRequest request = (ExtendTimeRequest) message.getObject();
                if (request.isAnswer()) {
                    request.setExecutableExam(DataBase.session.get(ExecutableExam.class, request.getExecutableExam().getId()));
                    sendToAllClients(new Message(Query.extraTime, request));
                }
                App.database.deleteTimeRequest((ExtendTimeRequest) message.getObject());

                break;

            case getExamsCopies:
                List<Pair<ExamCopy,List<Answer>>> list = new ArrayList<>();
                List<ExamCopy> exams;
                if (client.getInfo("user") instanceof Student)
                    exams = App.database.getExamsCopy((Student) client.getInfo("user"));
                else if (client.getInfo("user") instanceof Teacher)
                    exams = App.database.getExamsCopy((Teacher) client.getInfo("user"));
                else
                    exams = App.database.getAllExamsCopy();

                if (!exams.isEmpty())
                for (ExamCopy exam : exams) {
                    if (exam.getExecutableExam().getType().equals("regular")) {
                        if ((int) message.getObject() == App.database.getCourseIdForExamCopy(exam.getId()) && exam.isChecked()) {
                            List<Answer> answers = new ArrayList<>();
                            for (Answer answer : DataBase.session.get(ExamCopy.class, exam.getId()).getAnswers()) {
                                answers.add(DataBase.session.get(Answer.class, answer.getId()));
                            }
                            list.add(new Pair<>(exam,answers));
                        }
                    } else if ((int) message.getObject() == exam.getExecutableExam().getDocumentExam()
                            .getCourse().getId() && exam.isChecked())
                        list.add(new Pair<>(exam,null));
                }
                try {
                    client.sendToClient(new Message(Query.receiveExamsCopies, list));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case getCoursesForExamsCopies: // return courses for the user {receive}
                List<Course> courses2 = App.database.getAllCourses();
                List<Course> courses3 = new ArrayList<>();
                if (client.getInfo("user") instanceof Teacher) {
                    for (Course course : courses2)
                        for (Teacher teacher : course.getTeachers())
                            if (teacher.getId() == ((Teacher) client.getInfo("user")).getId())
                                courses3.add(course);
                } else if (client.getInfo("user") instanceof Student) {
                    for (Course course : courses2)
                        for (Student student1 : course.getStudents())
                            if (student1.getId() == ((Student) client.getInfo("user")).getId())
                                courses3.add(course);
                }
                if (client.getInfo("user") instanceof Manager) {
                    try {
                        client.sendToClient(new Message(Query.receiveCoursesForExamsCopies, courses2));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Query.receiveCoursesForExamsCopies, courses3));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case getExtendTimeRequests: // send all Time req To manager
                try {
                    client.sendToClient(new Message(Query.receiveExtendTimeRequests, App.database.getAllExtendTimeRequest()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case getCoursesForQuestionsDrawer: // return all courses for the teacher OR MANAGER {return receive}
                List<Course> courses = App.database.getAllCourses();
                List<Course> courses4 = new ArrayList<>();
                if (client.getInfo("user") instanceof Teacher) {
                    for (Course course : courses) {
                        for (Teacher teacher : course.getTeachers()) {
                            if (teacher.getId() == ((Teacher) client.getInfo("user")).getId())
                                courses4.add(course);
                        }
                    }

                    try {
                        client.sendToClient(new Message(Query.receiveCoursesForQuestionsDrawer, courses4));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        client.sendToClient(new Message(Query.receiveCoursesForQuestionsDrawer, courses));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case getQuestionsDrawer :
                List<Question> questions1 = App.database.getAllQuestions(((Course)message.getObject()).getId());
                try {
                    client.sendToClient(new Message(Query.receiveQuestionsDrawer, questions1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case getCoursesForExamsDrawer: // return all courses for the teacher OR MANAGER {return receive}
                List<Course> courses1 = App.database.getAllCourses();
                List<Course> courses5 = new ArrayList<>();
                if (client.getInfo("user") instanceof Teacher) {
                    for (Course course : courses1) {
                        for (Teacher teacher : course.getTeachers()) {
                            if (teacher.getId() == ((Teacher) client.getInfo("user")).getId())
                                courses5.add(course);
                        }
                    }
                    try {
                        client.sendToClient(new Message(Query.receiveCoursesForExamsDrawer, courses5));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Query.receiveCoursesForExamsDrawer, courses1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;


            case getExamsToCheck: // return ExamCopy in teacher {receive}
                try {
                    client.sendToClient(new Message(Query.receiveExamsToCheck,
                            App.database.getUncheckedExams((Teacher) client.getInfo("user"), (int) message.getObject())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case getExamsDrawer :
                List<Exam> exams3 = App.database.getExamsByCourseId((int) message.getObject());
                try {
                    client.sendToClient(new Message(Query.receiveExamsDrawer, exams3));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case getCoursesForExamsCheck :
                List<Course> courses8 = App.database.getAllCourses();
                List<Course> courses9 = new ArrayList<>();
                if (client.getInfo("user") instanceof Teacher) {
                    for (Course course : courses8) {
                        for (Teacher teacher : course.getTeachers()) {
                            if (teacher.getId() == ((Teacher) client.getInfo("user")).getId())
                                courses9.add(course);
                        }
                    }
                    try {
                        client.sendToClient(new Message(Query.receiveCoursesForExamsCheck, courses9));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case getCoursesForStats :
                List<Course> courses6 = App.database.getAllCourses();
                List<Course> courses7 = new ArrayList<>();
                if (client.getInfo("user") instanceof Teacher) {
                    for (Course course : courses6) {
                        for (Teacher teacher : course.getTeachers()) {
                            if (teacher.getId() == ((Teacher) client.getInfo("user")).getId())
                                courses7.add(course);
                        }
                    }
                    try {
                        client.sendToClient(new Message(Query.receiveCoursesForStats, courses7));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Query.receiveCoursesForStats, courses6));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case getStats:
                List<Statistics> stats = App.database.getStats((User) client.getInfo("user"), (int) message.getObject());
                Message message1 = new Message(Query.receiveStats, stats);
                try {
                    client.sendToClient(message1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        DataBase.session.getTransaction().commit();
        DataBase.session.close();
    }

    void checkExam(ExamCopy copy) {
        int grade = 0;
        for (Answer answer : copy.getAnswers()) {
            if (answer.getAnswer().equals(answer.getQuestion().getCorrectAns())) {
                grade += answer.getQuestion().getGrade();
                answer.setPoints(answer.getQuestion().getGrade());
            } else {
                answer.setPoints(0);
            }
        }
        copy.setGrade(grade);
		App.database.addExamCopy(copy); // SENDS THE EXAM TO THE TEACHER
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataBase.closeConnection();
    }
}
