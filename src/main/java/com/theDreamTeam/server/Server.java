package com.theDreamTeam.server;

import com.theDreamTeam.ocsf.server.*;
import com.theDreamTeam.entities.*;
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

            case Message.saveQuestion: // SAVES THE QUESTION
                System.out.println("saveQuestion received");
                App.database.saveQuestion((Question) message.getObject(), ((Question) message.getObject()).getCourse());
                System.out.println("saveQuestion done");
                List<Question> questions = App.database.getAllQuestions(((Question)message.getObject()).getCourse().getId());
                try {
                    client.sendToClient(new Message(Message.receiveQuestionsDrawer, questions));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.deleteQuestion: // NOT NEEDED
                System.out.println("deleteQuestion received");
                App.database.deleteQuestionById((Integer) message.getObject());
                System.out.println("deleteQuestion done");
                break;

            case Message.requestAllExam: // RETURNS ALL THE EXAMS IN COURSE
                System.out.println("requestAllExam received");
                try {
                    client.sendToClient(
                            new Message(Message.requestAllExam, App.database.getAllExams((Integer) message.getObject())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("requestAllExam done");
                break;

            case Message.saveNewRegularExam: // SAVES THE EXAM
                System.out.println("saveNewExam received");
                App.database.saveRegularExam((RegularExam) message.getObject(), ((Exam) message.getObject()).getCourse().getId());
                System.out.println("saveNewExam done");
                List<Exam> exams2 = App.database.getExamsByCourseId(((RegularExam) message.getObject()).getCourse().getId());
                System.out.println("morsy2");
                try {
                    client.sendToClient(new Message(Message.receiveExamsDrawer, exams2));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.saveNewDocumentExam :
                System.out.println("saveNewExam received");
                App.database.saveDocumentExam((DocumentExam) message.getObject(), ((Exam) message.getObject()).getCourse().getId());
                System.out.println("saveNewExam done");
                List<Exam> exams9 = App.database.getExamsByCourseId(((DocumentExam) message.getObject()).getCourse().getId());
                System.out.println("morsy2");
                try {
                    client.sendToClient(new Message(Message.receiveExamsDrawer, exams9));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.deleteExam: // NOT NEEDED
                App.database.deleteExamById((Integer) message.getObject());
                break;

            case Message.saveExecutableExam: // The teacher starts the exam
                App.database.saveExecutableExam((ExecutableExam) message.getObject());
                break;

            case Message.saveExamCopy: // Checks the exam and sends it to the teacher
                System.out.println("saveExamCopy");
                checkExam((ExamCopy) message.getObject());
                break;

            case Message.receiveExamCopy: // Returns the examcopy
                try {
                    client.sendToClient(new Message(Message.receiveExamCopy,
                            App.database.receiveExamCopy(((User) client.getInfo("user")).getUsername())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.getExamByCode: // sends the exam to the client
                String code = (String) message.getObject();
                ExecutableExam execExam = App.database.getExecutableExamByCode(code);
                Student student = (Student) client.getInfo("user");
                if (execExam != null) {
                    String type = execExam.getType();
                    for (Course course : student.getCourses())
                        if (type.equals("regular")) {
                            if (course.getId() == execExam.getRegularExam().getCourse().getId()) {
                                try {
                                    System.out.println("send regular exam");
                                    client.sendToClient(new Message(Message.receiveExamByCode, execExam));
                                    DataBase.session.getTransaction().commit();
                                    DataBase.session.close();
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (course.getId() == execExam.getDocumentExam().getCourse().getId()) {
                            try {
                                System.out.println("send document exam");
                                client.sendToClient(new Message(Message.receiveExamByCode, execExam));
                                DataBase.session.getTransaction().commit();
                                DataBase.session.close();
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                } else {
                    try {
                        System.out.println("invalid code");
                        client.sendToClient(new Message(Message.invalidExamCode, null));
                        DataBase.session.getTransaction().commit();
                        DataBase.session.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    System.out.println("invalid student");
                    client.sendToClient(new Message(Message.invalidStudent, null));
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.logIn:
                User user = (User) message.getObject();
                User validUser = App.database.getUser(user);
                if (validUser != null) {
                    if (validUser.isConnected()) {
                        try {
                            client.sendToClient(new Message(Message.userAlreadyConnected));
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("2");
                    validUser.setConnected(true);
                    DataBase.session.update(validUser);
                    System.out.println("update");
                    try {
                        client.sendToClient(new Message(Message.logInSuccessful, validUser));
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
                        client.sendToClient(new Message(Message.logInFailed));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Message.logout:
                App.database.logOut((User) client.getInfo("user"));
                break;

            case Message.finalCopy: // Saves/publishs the examcopy
                App.database.sendExamCopy((ExamCopy) message.getObject());
                if (((ExamCopy) message.getObject()).getExecutableExam().getType().equals("regular")) {
                    try {
                        client.sendToClient(new Message(Message.receiveExamsToCheck,
                                App.database.getUncheckedExams((Teacher) client.getInfo("user"), ((ExamCopy) message.getObject()).getExecutableExam().getRegularExam().getCourse().getId())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Message.receiveExamsToCheck,
                                App.database.getUncheckedExams((Teacher) client.getInfo("user"), ((ExamCopy) message.getObject()).getExecutableExam().getDocumentExam().getCourse().getId())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Message.extraTime:
                boolean ans = App.database.extraTimeRequest((Pair<ExtendTimeRequest, String>) message.getObject(), (Teacher) client.getInfo("user"));
                Message message9 = new Message(Message.extraTimeRequestFromTeacher, ans);
                try {
                    client.sendToClient(message9);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.sendExtraTime:
                // NEED TO CHECK WHAT THE MANAGER RESPONDED
                if (((ExtendTimeRequest) message.getObject()).isAnswer()) {
                    sendToAllClients(new Message(Message.extraTime, (ExtendTimeRequest) message.getObject()));
                    App.database.deleteTimeRequest((ExtendTimeRequest) message.getObject());
                }

                break;

            case Message.getExamsCopies:
                System.out.println("getExamsCopies");
                List<Pair<ExamCopy,List<Answer>>> list = new ArrayList<>();
                List<ExamCopy> exams;
                if (client.getInfo("user") instanceof Student)
                    exams = App.database.getExamsCopy((Student) client.getInfo("user"));
                else if (client.getInfo("user") instanceof Teacher)
                    exams = App.database.getExamsCopy((Teacher) client.getInfo("user"));
                else
                    exams = App.database.getAllExamsCopy();

                if (!exams.isEmpty())
                    System.out.println("here");
                for (ExamCopy exam : exams) {
                    System.out.println("for");
                    if (exam.getExecutableExam().getType().equals("regular")) {
                        System.out.println("if");
                        if ((int) message.getObject() == App.database.getCourseIdForExamCopy(exam.getId()) && exam.isChecked()) {
                            System.out.println("still here");
                            List<Answer> answers = new ArrayList<>();
                            for (Answer answer : DataBase.session.get(ExamCopy.class, exam.getId()).getAnswers()) {
                                answers.add(DataBase.session.get(Answer.class, answer.getId()));
                            }
                            System.out.println("after");
                            list.add(new Pair<>(exam,answers));
                        }
                    } else if ((int) message.getObject() == exam.getExecutableExam().getDocumentExam()
                            .getCourse().getId() && exam.isChecked())
                        list.add(new Pair<>(exam,null));
                }
                try {
                    System.out.println("SENT !");
                    client.sendToClient(new Message(Message.receiveExamsCopies, list));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.getCoursesForExamsCopies: // return courses for the user {receive}
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
                        client.sendToClient(new Message(Message.receiveCoursesForExamsCopies, courses2));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Message.receiveCoursesForExamsCopies, courses3));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Message.getExtendTimeRequests: // send all Time req To manager
                try {
                    client.sendToClient(new Message(Message.receiveExtendTimeRequests, App.database.getAllExtendTimeRequest()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.getCoursesForQuestionsDrawer: // return all courses for the teacher OR MANAGER {return receive}
                List<Course> courses = App.database.getAllCourses();
                System.out.println(courses.size());
                System.out.println("morsy1");
                List<Course> courses4 = new ArrayList<>();
                if (client.getInfo("user") instanceof Teacher) {
                    for (Course course : courses) {
                        System.out.println("for");
                        System.out.println(course.getTeachers().size());
                        System.out.println(((Teacher) client.getInfo("user")).getId());
                        for (Teacher teacher : course.getTeachers()) {
                            if (teacher.getId() == ((Teacher) client.getInfo("user")).getId())
                                courses4.add(course);
                        }
                    }

                    try {
                        System.out.println("morsy2");
                        client.sendToClient(new Message(Message.receiveCoursesForQuestionsDrawer, courses4));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        client.sendToClient(new Message(Message.receiveCoursesForQuestionsDrawer, courses));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Message.getQuestionsDrawer :
                List<Question> questions1 = App.database.getAllQuestions(((Course)message.getObject()).getId());
                try {
                    client.sendToClient(new Message(Message.receiveQuestionsDrawer, questions1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.getCoursesForExamsDrawer: // return all courses for the teacher OR MANAGER {return receive}
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
                        System.out.println(courses5.size());
                        client.sendToClient(new Message(Message.receiveCoursesForExamsDrawer, courses5));
                        System.out.println("sent");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Message.receiveCoursesForExamsDrawer, courses1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;


            case Message.getExamsToCheck: // return ExamCopy in teacher {receive}
                try {
                    client.sendToClient(new Message(Message.receiveExamsToCheck,
                            App.database.getUncheckedExams((Teacher) client.getInfo("user"), (int) message.getObject())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.getExamsDrawer :
                System.out.println("morsy1");
                List<Exam> exams3 = App.database.getExamsByCourseId((int) message.getObject());
                System.out.println(exams3.size());
                System.out.println("morsy2");
                try {
                    client.sendToClient(new Message(Message.receiveExamsDrawer, exams3));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Message.getCoursesForExamsCheck :
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
                        System.out.println(courses9.size());
                        client.sendToClient(new Message(Message.receiveCoursesForExamsCheck, courses9));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Message.getCoursesForStats :
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
                        System.out.println(courses7.size());
                        client.sendToClient(new Message(Message.receiveCoursesForStats, courses7));
                        System.out.println("sent");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        client.sendToClient(new Message(Message.receiveCoursesForStats, courses6));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Message.getStats:
                System.out.println("morsy6");
                List<Statistics> stats = App.database.getStats((User) client.getInfo("user"), (int) message.getObject());
                System.out.println("morsy9");
                Message message1 = new Message(Message.receiveStats, stats);
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
        System.out.println("checking exam");
        int grade = 0;
        for (Answer answer : copy.getAnswers()) {
            System.out.println("for");
            if (answer.getAnswer().equals(answer.getQuestion().getCorrectAns())) {
                grade += answer.getQuestion().getGrade();
                answer.setPoints(answer.getQuestion().getGrade());
            } else
                answer.setPoints(0);
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
