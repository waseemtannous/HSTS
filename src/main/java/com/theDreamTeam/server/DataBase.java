package com.theDreamTeam.server;


import com.theDreamTeam.entities.*;

import javafx.util.Pair;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.print.Doc;
import javax.swing.plaf.IconUIResource;
import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBase { // class that has all database related functions for easier and readable
						// programming

	public static Session session;

	public static SessionFactory sessionFactory;

	String url = "jdbc:mysql://localhost/filler";
	String username = "root";
	String password = "wTannous123";

	private static SessionFactory getSessionFactory() throws HibernateException {
		Configuration configuration = new Configuration();
		// Add ALL of your entities here. You can also try adding a whole package.
		configuration.addAnnotatedClass(Question.class);
		configuration.addAnnotatedClass(Exam.class);
		configuration.addAnnotatedClass(Answer.class);
		configuration.addAnnotatedClass(Course.class);
		configuration.addAnnotatedClass(ExamCopy.class);
		configuration.addAnnotatedClass(RegularExam.class);
		configuration.addAnnotatedClass(DocumentExam.class);
		configuration.addAnnotatedClass(ExecutableExam.class);
		configuration.addAnnotatedClass(ExtendTimeRequest.class);
		configuration.addAnnotatedClass(GradedQuestion.class);
		configuration.addAnnotatedClass(Manager.class);
		configuration.addAnnotatedClass(Student.class);
		configuration.addAnnotatedClass(Teacher.class);
		configuration.addAnnotatedClass(User.class);
		configuration.addAnnotatedClass(Statistics.class);

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();

		return configuration.buildSessionFactory(serviceRegistry);
	}

	public static void connect() {
		try {
			Logger.getLogger("org.hibernate").setLevel(Level.SEVERE); // no need to print the connection text
			sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();

			initializeData();

		} catch (Exception exception) {
			if (session != null) {
				System.out.println("rollback");
				session.getTransaction().rollback();
			}
			System.err.println("An error occurred, changes have been rolled back.");
			exception.printStackTrace();
			closeConnection();
		} finally {
			if (session != null)
				session.close();
		}
	}

	public List<Question> getAllQuestions(int courseID) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Course> query = builder.createQuery(Course.class);
		query.from(Course.class);
		List<Course> courses = session.createQuery(query).getResultList();
		List<Question> questions = new ArrayList<>();
		for (Course course : courses) {
			if (course.getId() == courseID)
				questions = course.getQuestions();
		}
		return questions;
	}

	public List<Exam> getAllExams(int courseID) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Exam> query = builder.createQuery(Exam.class);
		query.from(Exam.class);
		List<Exam> exams = session.createQuery(query).getResultList();
		List<Exam> exams2 = new ArrayList<>();
		for (Exam exam : exams) {
			if (exam.getCourse().getId() == courseID)
				exams2.add(exam);
		}

		if (!exams2.isEmpty())
			return exams2;

		return null;
	}

	public void saveQuestion(Question question, Course course) {
		System.out.println("0");
		course.addQuestion(question);
		System.out.println("1");
		System.out.println("2");
		session.save(question);
		System.out.println("3");
		session.update(course);
		System.out.println("4");
		System.out.println("5");
	}

	public void saveRegularExam(RegularExam exam, int courseId) {
		Course course = session.get(Course.class, courseId);
		Teacher teacher = session.get(Teacher.class, exam.getTeacher().getId());
		System.out.println(exam.getTeacher().getId());
		System.out.println("waseem1");
		System.out.println("waseem2");
		exam.setTeacher(teacher);
		exam.setCourse(course);
		course.getRegularExams().add(exam);
		System.out.println("waseem3");
		System.out.println("waseem4");
		for (GradedQuestion question : exam.getGradedQuestions()) {
			question.setCourse(course);
			session.save(question);
		}
		session.save(exam);
		System.out.println("here1");
		System.out.println("here1.5");
		System.out.println("here2");
		session.update(teacher);
		System.out.println("waseem8");
		session.update(course);
		System.out.println("waseem9");
	}

	public void saveDocumentExam(DocumentExam exam, int courseId) {
		Course course = session.get(Course.class, courseId);
		Teacher teacher = session.get(Teacher.class, exam.getTeacher().getId());
		System.out.println(exam.getTeacher().getId());
		System.out.println("waseem1");
		System.out.println("waseem2");
		exam.setTeacher(teacher);
		exam.setCourse(course);
		course.getDocumentExams().add(exam);
		System.out.println("waseem3");
		System.out.println("waseem4");
		session.save(exam);
		System.out.println("here1");
		System.out.println("here1.5");
		System.out.println("here2");
		session.update(teacher);
		System.out.println("waseem8");
		session.update(course);
		System.out.println("waseem9");
	}

	public void saveExamCopy(ExamCopy exam) {
		session.save(exam);
	}

	public void saveExecutableExam(ExecutableExam exam) {
		session.save(exam);
		Statistics stats = new Statistics(exam);
		session.save(stats);
	}

	public synchronized void deleteQuestionById(int id) {
		try (Connection connection = DriverManager.getConnection(url, username, password)) {

			String sql = "DELETE FROM question WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, ((Integer) id).toString());
			int rows = statement.executeUpdate();
			System.out.println(rows + " record(s) deleted.");
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	public synchronized void deleteExamById(int id) {
		try (Connection connection = DriverManager.getConnection(url, username, password)) {

			String sql = "DELETE FROM exam WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, ((Integer) id).toString());
			int rows = statement.executeUpdate();
			System.out.println(rows + " record(s) deleted.");
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 //Gets copys by USER
	public List<ExamCopy> getAllCopys(User user) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<User> query = builder.createQuery(User.class);
		query.from(User.class);
		List<User> users = session.createQuery(query).getResultList();
		List<User> users2 = new ArrayList<>();
		for (User user1 : users) {
			if ((user1.getUsername().equals(user.getUsername())))
				users2.add(user1);
		}

		if (!users2.isEmpty())
			return ((Student) users2.get(0)).getExams();

		return null;
	}

		//Encrypts the password using "SHA256".
	public static synchronized String SHA256(String pass) {

		byte[] input = pass.getBytes();
		MessageDigest SHA256 = null;
		try {
			SHA256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		assert SHA256 != null;
		SHA256.update(input);
		byte[] digest = SHA256.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			String hex = Integer.toHexString(0xff & digest[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();

	}

	// Check if the User is valid

	public synchronized static int checkUser(User user) {// If 0 all good If 1 Username is not valid else 2 Password is
															// not
		// valid
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<User> query = builder.createQuery(User.class);
		query.from(User.class);

		List<User> users = session.createQuery(query).getResultList();
		List<User> users2 = new ArrayList<>();
		System.out.println(users.size());
		for (User user1 : users) {
			System.out.println(user1.getUsername());

			if ((user1.getUsername()).equals(user.getUsername())) {
				System.out.println(user.getUsername());
				users2.add(user1);
				System.out.println(user.getUsername());
				break;

			}
		}

		// if user name is not valid
		if (users2.isEmpty())
			return 1;

		// if the password is not valid

		if (!(users2.get(0).getPassword()).equals(user.getPassword()))
			return 2;
		// Every thing is OK!!
		return 0;

	}

	//Changes the connected status.
	public synchronized void logOut(User user) {
		user.setConnected(false);
		session.update(user);
		System.out.println("logout");
	}

	public static void closeConnection() {
		session.close();
		System.out.println("session closed");
	}

	public <T> List<T> getAllCourses(Class<T> object) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
		Root<T> rootEntry = criteriaQuery.from(object);
		CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

		TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
		return allQuery.getResultList();
	}

	public List<ExamCopy> getExamsCopy(Teacher teacher) {
		System.out.println("waseem1");
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamCopy> query = builder.createQuery(ExamCopy.class);
		query.from(ExamCopy.class);
		System.out.println("2");
		List<ExamCopy> exams = session.createQuery(query).getResultList();
		System.out.println("2.5");
		List<ExamCopy> copies = new ArrayList<>();
		System.out.println("3");
		for (ExamCopy exam : exams){
			System.out.println("for");
			if (exam.getExecutableExam().getType().equals("regular")) {
				System.out.println("if");
				if (exam.getExecutableExam().getExecTeacher().getId() == teacher.getId()
						|| exam.getExecutableExam().getRegularExam().getTeacher().getId() == teacher.getId()) {
					copies.add(exam);
				}
			} else if (exam.getExecutableExam().getExecTeacher().getId() == teacher.getId()
					|| exam.getExecutableExam().getDocumentExam().getTeacher().getId() == teacher.getId()) {
				copies.add(exam);
			}}
		System.out.println("waseem2");
		return copies;
	}

	public List<ExamCopy> getExamsCopy(Student student) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamCopy> query = builder.createQuery(ExamCopy.class);
		query.from(ExamCopy.class);
		List<ExamCopy> exams = session.createQuery(query).getResultList();
		List<ExamCopy> copies = new ArrayList<>();
		for (ExamCopy exam : exams)
			if(exam.getStudent().getId() == student.getId())
				copies.add(exam);
		return copies;
	}

	public static List<Answer> getExamAnswers(int examid) {
		ExamCopy exam = session.get(ExamCopy.class, examid);
		List<Answer> temp = new ArrayList<>();
		for (Answer answer : exam.getAnswers()) {
			temp.add(session.get(Answer.class, answer.getId()));
		}
		return temp;
	}

	public int getCourseIdForExamCopy(int examId) {
		ExamCopy exam = session.get(ExamCopy.class, examId);
		if (exam.getType().equals("regular"))
			return exam.getExecutableExam().getRegularExam().getCourse().getId();
		else
			return exam.getExecutableExam().getRegularExam().getCourse().getId();
	}

	public List<ExamCopy> getAllExamsCopy() {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamCopy> query = builder.createQuery(ExamCopy.class);
		query.from(ExamCopy.class);
		List<ExamCopy> exams = session.createQuery(query).getResultList();
		return exams;
	}

	public List<ExtendTimeRequest> getAllExtendTimeRequest() {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExtendTimeRequest> query = builder.createQuery(ExtendTimeRequest.class);
		query.from(ExtendTimeRequest.class);
		return session.createQuery(query).getResultList();

	}

	public List<Course> getAllCourses() {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Course> query = builder.createQuery(Course.class);
		query.from(Course.class);
		List<Course> courses = session.createQuery(query).getResultList();
		return courses;
	}

	public List<ExamCopy> getUncheckedExams(Teacher teacher, int courseId) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamCopy> query = builder.createQuery(ExamCopy.class);
		query.from(ExamCopy.class);
		List<ExamCopy> exams = session.createQuery(query).getResultList();
		List<ExamCopy> temp = new ArrayList<>();
		for (int i = 0; i < exams.size(); i++) {
			if (exams.get(i).getExecutableExam().getType().equals("regular")) {
				if ((exams.get(i).getExecutableExam().getExecTeacher().getId() == teacher.getId()) &&
						!exams.get(i).isChecked() &&
						exams.get(i).getExecutableExam().getRegularExam().getCourse().getId() == courseId) {
					System.out.println("if");
					List<Answer> answers = new ArrayList<>();
					for (int j = 0; j < exams.get(i).getAnswers().size(); j++) {
						answers.add(session.get(Answer.class, exams.get(i).getAnswers().get(j).getId()));
					}
					exams.get(i).setAnswers(answers);
					temp.add(session.get(ExamCopy.class, exams.get(i).getId()));
				}
			} else {
				if ((exams.get(i).getExecutableExam().getExecTeacher().getId() == teacher.getId()) &&
						!exams.get(i).isChecked() &&
						exams.get(i).getExecutableExam().getDocumentExam().getCourse().getId() == courseId) {
					System.out.println("if");
					temp.add(session.get(ExamCopy.class, exams.get(i).getId()));
				}
			}
		}
		return temp;
	}

	public List<ExamCopy> receiveExamCopy(String userName) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamCopy> query = builder.createQuery(ExamCopy.class);
		query.from(ExamCopy.class);
		List<ExamCopy> copys = session.createQuery(query).getResultList();
		List<ExamCopy> copys1  = new ArrayList<>();
		for (ExamCopy copy : copys) {
			if ((copy.getStudent().getUsername().equals(userName)))
				copys1.add(copy);
		}

		if (!copys1.isEmpty())
			return copys1;

		return null;
	}

	public void sendExamCopy(ExamCopy copy) {
		Student student = session.get(Student.class, copy.getStudent().getId());
		ExamCopy examCopy = session.get(ExamCopy.class, copy.getId());
		examCopy.setGrade(copy.getGrade());
		examCopy.setNotesForGradeChange(copy.getNotesForGradeChange());
		examCopy.setChecked(true);
		student.getExams().add(examCopy);
		session.update(examCopy);
		session.update(student);

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Statistics> query = builder.createQuery(Statistics.class);
		query.from(Statistics.class);
		List<Statistics> stats = session.createQuery(query).getResultList();

		for (Statistics stat : stats) {
			if (stat.getExecutableExam().getId() == copy.getExecutableExam().getId()) {
				stat.addGrade(copy.getGrade(), copy.isFinished());
				session.update(stat);
				break;
			}
		}
	}

	// Get by ExecutableExam ID
	public ExecutableExam getExecutableExamById(int examID) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExecutableExam> query = builder.createQuery(ExecutableExam.class);
		query.from(ExecutableExam.class);
		List<ExecutableExam> exams = session.createQuery(query).getResultList();
		for (ExecutableExam exam : exams) {
			if (exam.getId() == examID)
				return exam;
		}

		return null;
	}

	// Get by ExecutableExam Code
	public ExecutableExam getExecutableExamByCode(String ExamCode) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExecutableExam> query = builder.createQuery(ExecutableExam.class);
		query.from(ExecutableExam.class);
		List<ExecutableExam> exams = session.createQuery(query).getResultList();
		for (ExecutableExam exam : exams) {
			if (exam.getCode().equals(ExamCode))
				return exam;
		}
		return null;
	}



	// Returns user(student,teacher,manager)
	public User getUser(User user) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Student> query1 = builder.createQuery(Student.class);
		query1.from(Student.class);

		List<Student> students = session.createQuery(query1).getResultList();
		for (Student temp : students){
			if (temp.getUsername().equals(user.getUsername()) && temp.getPassword().equals(SHA256(user.getPassword()))) {
				return temp;
			}
		}


		CriteriaQuery<Teacher> query2 = builder.createQuery(Teacher.class);
		query2.from(Teacher.class);

		List<Teacher> teachers = session.createQuery(query2).getResultList();
		for (Teacher temp : teachers) {
			if (temp.getUsername().equals(user.getUsername()) && temp.getPassword().equals(SHA256(user.getPassword()))) {
				return temp;
			}
		}

		CriteriaQuery<Manager> query3 = builder.createQuery(Manager.class);
		query3.from(Manager.class);

		List<Manager> managers = session.createQuery(query3).getResultList();
		for (Manager temp : managers) {
			if (temp.getUsername().equals(user.getUsername()) && temp.getPassword().equals(SHA256(user.getPassword()))) {
				return temp;
			}
		}

		return null;

	}

	public void addExamCopy(ExamCopy copy) {
		session.save(copy);
	}


	public boolean extraTimeRequest(Pair<ExtendTimeRequest, String> pair, Teacher teacher) {
		ExtendTimeRequest request = pair.getKey();
		String code = pair.getValue();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExecutableExam> query = builder.createQuery(ExecutableExam.class);
		query.from(ExecutableExam.class);
		List<ExecutableExam> exams = session.createQuery(query).getResultList();

		boolean successful = false;

		for (ExecutableExam exam : exams) {
			if (exam.getExecTeacher().getId() == teacher.getId() && exam.getCode().equals(code)) {
				request.setExecutableExam(exam);
				successful = true;
				break;
			}
		}
		if (successful) {
			session.save(request);
		}
		return successful;
	}

	public void deleteTimeRequest(ExtendTimeRequest time) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<User> query = builder.createQuery(User.class);
		query.from(User.class);
		List<User> users = session.createQuery(query).getResultList();
		User user = null;

		for (User user1 : users) {
			if ((user1 instanceof Manager))
				user = user1;
		}

		((Manager) user).getExtendTimeRequests().add(time);
		session.update(user);
	}

	public List<Exam> getExamsByCourseId(int id) {
		Course course = session.get(Course.class, id);
		for (RegularExam exam : course.getRegularExams()) {
			List<GradedQuestion> questions = new ArrayList<>();
			for (GradedQuestion question : exam.getGradedQuestions()) {
				questions.add(session.get(GradedQuestion.class, question.getId()));
			}
			exam.setGradedQuestions(questions);
		}
		return course.getAllExams();
	}

	public List<Statistics> getStats(User user, int courseId) {
		System.out.println("1");
		List<Statistics> stats = new ArrayList<>();
		System.out.println("2");
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Statistics> query = builder.createQuery(Statistics.class);
		System.out.println("3");
		query.from(Statistics.class);
		System.out.println("4");
		List<Statistics> allStats = session.createQuery(query).getResultList();
		System.out.println("5");
		if (user instanceof Teacher) {
			for (Statistics stat : allStats) {
				if (stat.getExecutableExam().getExam().getCourse().getId() == courseId) {
					System.out.println("6");
					if (stat.getExecutableExam().getExecTeacher().getId() == user.getId() ||
							stat.getExecutableExam().getExam().getTeacher().getId() == user.getId()) {
						System.out.println("7");
						stats.add(stat);
					}
				}
			}

		} else {
			for (Statistics stat : allStats) {
				if (stat.getExecutableExam().getExam().getCourse().getId() == courseId)
					stats.add(stat);
			}
		}

		return stats;
	}


	private static void initializeData() throws Exception {
		Teacher oren = new Teacher("2", SHA256("2"));
		Teacher robby = new Teacher("Robby Goman", SHA256("robby69"));
		Teacher antonio = new Teacher("Antonio Suli", SHA256("antonio69"));
		Teacher saji = new Teacher("Saji Assi", SHA256("saji69"));
		Teacher rawad = new Teacher("Rawad Khalaily", SHA256("rawad69"));


//		session.flush();

		Student waseem = new Student("1", SHA256("1"));
		Student faroq = new Student("Faroq Krayem", SHA256("faroq69"));
		Student lionel = new Student("Lionel Messi", SHA256("lionel69"));
		Student paul = new Student("Paul Pogba", SHA256("paul69"));
		Student alexis = new Student("Alexis Texas", SHA256("alexis69"));


//		session.flush();

		Manager aboIbrahim = new Manager("3", SHA256("3"));

		List<Teacher> list1 = new ArrayList<>();
		list1.add(oren);
		Course course1 = new Course("Data Structures", list1);
		list1.add(saji);
		Course course2 = new Course("Operating Systems", list1);
		list1.remove(oren);
		list1.add(antonio);
		Course course3 = new Course("Calculus", list1);
		list1.add(rawad);
		Course course4 = new Course("Algebra", list1);
		list1.add(robby);
		Course course5 = new Course("Introduction to CS", list1);

		course1.addStudent(waseem,alexis);
		course2.addStudent(lionel,paul);
		course3.addStudent(waseem,paul);
		course4.addStudent(faroq,alexis);
		course5.addStudent(faroq,waseem);

//		session.flush();

		Question ques1 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "A", course1);
		Question ques2 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "A", course1);
		Question ques3 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "A", course1);
		Question ques4 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "A", course1);
		Question ques5 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "A", course1);

		Question ques6 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "B", course2);
		Question ques7 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "B", course2);
		Question ques8 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "B", course2);
		Question ques9 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "B", course2);
		Question ques10 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "B", course2);

		Question ques11 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "C", course3);
		Question ques12 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "C", course3);
		Question ques13 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "C", course3);
		Question ques14 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "C", course3);
		Question ques15 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "C", course3);

		Question ques16 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "D", course4);
		Question ques17 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "D", course4);
		Question ques18 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "D", course4);
		Question ques19 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "D", course4);
		Question ques20 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "D", course4);

		Question ques21 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "A", course5);
		Question ques22 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "B", course5);
		Question ques23 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "C", course5);
		Question ques24 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "D", course5);
		Question ques25 = new Question("Question", "Ans1", "Ans2", "Ans3", "Ans4", "A", course5);



//		session.flush();

		Exam exam1 = new Exam("This is for the teacher", "Goodluck", oren, course1, 120, 100);
		Exam exam2 = new Exam("This is for the teacher", "Goodluck", saji, course2, 120, 100);
		Exam exam3 = new Exam("This is for the teacher", "Goodluck", antonio, course3, 120, 100);
		Exam exam4 = new Exam("This is for the teacher", "Goodluck", rawad, course4, 120, 100);
		Exam exam5 = new Exam("This is for the teacher", "Goodluck", robby, course5, 120, 100);



//		session.flush();

		GradedQuestion grade1 = new GradedQuestion(ques1, 20);
		GradedQuestion grade2 = new GradedQuestion(ques2, 20);
		GradedQuestion grade3 = new GradedQuestion(ques3, 20);
		GradedQuestion grade4 = new GradedQuestion(ques4, 20);
		GradedQuestion grade5 = new GradedQuestion(ques5, 20);

		GradedQuestion grade6 = new GradedQuestion(ques6, 20);
		GradedQuestion grade7 = new GradedQuestion(ques7, 20);
		GradedQuestion grade8 = new GradedQuestion(ques8, 20);
		GradedQuestion grade9 = new GradedQuestion(ques9, 20);
		GradedQuestion grade10 = new GradedQuestion(ques10, 20);

		GradedQuestion grade11 = new GradedQuestion(ques11, 20);
		GradedQuestion grade12 = new GradedQuestion(ques12, 20);
		GradedQuestion grade13 = new GradedQuestion(ques13, 20);
		GradedQuestion grade14 = new GradedQuestion(ques14, 20);
		GradedQuestion grade15 = new GradedQuestion(ques15, 20);

		GradedQuestion grade16 = new GradedQuestion(ques16, 20);
		GradedQuestion grade17 = new GradedQuestion(ques17, 20);
		GradedQuestion grade18 = new GradedQuestion(ques18, 20);
		GradedQuestion grade19 = new GradedQuestion(ques19, 20);
		GradedQuestion grade20 = new GradedQuestion(ques20, 20);

		GradedQuestion grade21 = new GradedQuestion(ques21, 20);
		GradedQuestion grade22 = new GradedQuestion(ques22, 20);
		GradedQuestion grade23 = new GradedQuestion(ques23, 20);
		GradedQuestion grade24 = new GradedQuestion(ques24, 20);
		GradedQuestion grade25 = new GradedQuestion(ques25, 20);



//		session.flush();

		List<GradedQuestion> list2 = new ArrayList<>();
		list2.add(grade1);
		list2.add(grade2);
		list2.add(grade3);
		list2.add(grade4);
		list2.add(grade5);

		List<GradedQuestion> list3 = new ArrayList<>();
		list3.add(grade6);
		list3.add(grade7);
		list3.add(grade8);
		list3.add(grade9);
		list3.add(grade10);

		List<GradedQuestion> list4 = new ArrayList<>();
		list4.add(grade11);
		list4.add(grade12);
		list4.add(grade13);
		list4.add(grade14);
		list4.add(grade15);

		List<GradedQuestion> list5 = new ArrayList<>();
		list5.add(grade16);
		list5.add(grade17);
		list5.add(grade18);
		list5.add(grade19);
		list5.add(grade20);

		List<GradedQuestion> list6 = new ArrayList<>();
		list6.add(grade21);
		list6.add(grade22);
		list6.add(grade23);
		list6.add(grade24);
		list6.add(grade25);

		RegularExam regular1 = new RegularExam(exam1, list2);
		RegularExam regular2 = new RegularExam(exam2, list3);
		RegularExam regular3 = new RegularExam(exam3, list4);
		RegularExam regular4 = new RegularExam(exam4, list5);
		RegularExam regular5 = new RegularExam(exam5, list6);

		course1.addRegularExam(regular1);



//		session.flush();

		Answer ans1 = new Answer(grade1);
		ans1.setAnswer("A");
		Answer ans2 = new Answer(grade2);
		ans2.setAnswer("A");
		Answer ans3 = new Answer(grade3);
		ans3.setAnswer("A");
		Answer ans4 = new Answer(grade4);
		ans4.setAnswer("A");
		Answer ans5 = new Answer(grade5);
		ans5.setAnswer("A");

		Answer ans6 = new Answer(grade6);
		ans6.setAnswer("A");
		Answer ans7 = new Answer(grade7);
		ans7.setAnswer("A");
		Answer ans8 = new Answer(grade8);
		ans8.setAnswer("A");
		Answer ans9 = new Answer(grade9);
		ans9.setAnswer("A");
		Answer ans10 = new Answer(grade10);
		ans10.setAnswer("A");

		Answer ans11 = new Answer(grade11);
		ans11.setAnswer("A");
		Answer ans12 = new Answer(grade12);
		ans12.setAnswer("A");
		Answer ans13 = new Answer(grade13);
		ans13.setAnswer("A");
		Answer ans14 = new Answer(grade14);
		ans14.setAnswer("A");
		Answer ans15 = new Answer(grade15);
		ans15.setAnswer("A");

		Answer ans16 = new Answer(grade16);
		ans16.setAnswer("A");
		Answer ans17 = new Answer(grade17);
		ans17.setAnswer("A");
		Answer ans18 = new Answer(grade18);
		ans18.setAnswer("A");
		Answer ans19 = new Answer(grade19);
		ans19.setAnswer("A");
		Answer ans20 = new Answer(grade20);
		ans20.setAnswer("A");

		Answer ans21 = new Answer(grade21);
		ans21.setAnswer("A");
		Answer ans22 = new Answer(grade22);
		ans22.setAnswer("A");
		Answer ans23 = new Answer(grade23);
		ans23.setAnswer("A");
		Answer ans24 = new Answer(grade24);
		ans24.setAnswer("A");
		Answer ans25 = new Answer(grade25);
		ans25.setAnswer("A");

		List<Answer> listA = new ArrayList<>();

		listA.add(ans1);
		listA.add(ans2);
		listA.add(ans3);
		listA.add(ans4);
		listA.add(ans5);

		List<Answer> listB = new ArrayList<>();

		listB.add(ans6);
		listB.add(ans7);
		listB.add(ans8);
		listB.add(ans9);
		listB.add(ans10);

		List<Answer> listC = new ArrayList<>();

		listC.add(ans11);
		listC.add(ans12);
		listC.add(ans13);
		listC.add(ans14);
		listC.add(ans15);

		List<Answer> listD = new ArrayList<>();

		listD.add(ans16);
		listD.add(ans17);
		listD.add(ans18);
		listD.add(ans19);
		listD.add(ans20);

		List<Answer> listF = new ArrayList<>();

		listF.add(ans21);
		listF.add(ans22);
		listF.add(ans23);
		listF.add(ans24);
		listF.add(ans25);

		ExecutableExam exec1 = new ExecutableExam("CODE1", regular1, oren);
		ExecutableExam exec2 = new ExecutableExam("CODE2", regular2, rawad);
		ExecutableExam exec3 = new ExecutableExam("CODE3", regular3, antonio);
		ExecutableExam exec4 = new ExecutableExam("CODE4", regular4, robby);
		ExecutableExam exec5 = new ExecutableExam("CODE5", regular5, saji);

		Statistics stats1 = new Statistics(exec1);
		Statistics stats2 = new Statistics(exec2);
		Statistics stats3 = new Statistics(exec3);
		Statistics stats4 = new Statistics(exec4);
		Statistics stats5 = new Statistics(exec5);



//		session.flush();

		ExamCopy copy1 = new ExamCopy(waseem, exec1, listA, true);
		copy1.setChecked(true);
		copy1.setGrade(20);
		stats1.addGrade(copy1.getGrade(), true);

		ExamCopy copy2 = new ExamCopy(lionel, exec2, listB, false);
		copy2.setChecked(true);
		copy2.setGrade(40);
		stats2.addGrade(copy2.getGrade(), false);


		ExamCopy copy3 = new ExamCopy(paul, exec3, listC, true);
		copy3.setChecked(true);
		copy3.setGrade(60);
		stats3.addGrade(copy3.getGrade(), true);


		ExamCopy copy4 = new ExamCopy(alexis, exec4, listD, false);
		copy4.setChecked(true);
		copy4.setGrade(80);
		stats4.addGrade(copy4.getGrade(), false);


		ExamCopy copy5 = new ExamCopy(faroq, exec5, listF, false);
		copy5.setChecked(true);
		copy5.setGrade(100);
		stats5.addGrade(copy5.getGrade(), false);



		// session.save(copy1);

//		session.flush();

		List<ExtendTimeRequest> listReq = new ArrayList<>();
		ExtendTimeRequest req = new ExtendTimeRequest(rawad, exec1, 69, "OK");
		listReq.add(req);
		aboIbrahim.setExtendTimeRequests(listReq);

//		File questionnaire = new File("C:\\Users\\waseem tannous\\Desktop\\HSTS\\exam.docx");
//		File answer = new File("C:\\Users\\waseem tannous\\Desktop\\HSTS\\answer.docx");

		File questionnaire = new File("C:\\Users\\lamoo\\Desktop\\question.docx");
		File answer = new File("C:\\Users\\lamoo\\Desktop\\answer.docx");

		byte[] questionByte = Files.readAllBytes(questionnaire.toPath());
		byte[] answerByte = Files.readAllBytes(answer.toPath());

		DocumentExam doc = new DocumentExam(exam1, questionByte);

		course1.addDocumentExam(doc);

		ExecutableExam doc1 = new ExecutableExam("1234", doc, oren);

		ExamCopy doc2 = new ExamCopy(waseem, doc1, answerByte, true);
//		doc2.setChecked(true);

		session.save(oren);
		session.save(robby);
		session.save(rawad);
		session.save(saji);
		session.save(antonio);


		session.save(waseem);
		session.save(faroq);
		session.save(lionel);
		session.save(paul);
		session.save(alexis);

		session.save(aboIbrahim);


		session.save(course1);
		session.save(course2);
		session.save(course3);
		session.save(course4);
		session.save(course5);

		session.save(ques1);
		session.save(ques2);
		session.save(ques3);
		session.save(ques4);
		session.save(ques5);
		session.save(ques6);
		session.save(ques7);
		session.save(ques8);
		session.save(ques9);
		session.save(ques10);
		session.save(ques11);
		session.save(ques12);
		session.save(ques13);
		session.save(ques14);
		session.save(ques15);
		session.save(ques16);
		session.save(ques17);
		session.save(ques18);
		session.save(ques19);
		session.save(ques20);
		session.save(ques21);
		session.save(ques22);
		session.save(ques23);
		session.save(ques24);
		session.save(ques25);


		session.save(exam1);
		session.save(exam2);
		session.save(exam3);
		session.save(exam4);
		session.save(exam5);


		session.save(grade1);
		session.save(grade2);
		session.save(grade3);
		session.save(grade4);
		session.save(grade5);
		session.save(grade6);
		session.save(grade7);
		session.save(grade8);
		session.save(grade9);
		session.save(grade10);
		session.save(grade11);
		session.save(grade12);
		session.save(grade13);
		session.save(grade14);
		session.save(grade15);
		session.save(grade16);
		session.save(grade17);
		session.save(grade18);
		session.save(grade19);
		session.save(grade20);
		session.save(grade21);
		session.save(grade22);
		session.save(grade23);
		session.save(grade24);
		session.save(grade25);


		session.save(regular1);
		session.save(regular2);
		session.save(regular3);
		session.save(regular4);
		session.save(regular5);


		session.save(ans1);
		session.save(ans2);
		session.save(ans3);
		session.save(ans4);
		session.save(ans5);
		session.save(ans6);
		session.save(ans7);
		session.save(ans8);
		session.save(ans9);
		session.save(ans10);
		session.save(ans11);
		session.save(ans12);
		session.save(ans13);
		session.save(ans14);
		session.save(ans15);
		session.save(ans16);
		session.save(ans17);
		session.save(ans18);
		session.save(ans19);
		session.save(ans20);
		session.save(ans21);
		session.save(ans22);
		session.save(ans23);
		session.save(ans24);
		session.save(ans25);


		session.save(exec1);
		session.save(exec2);
		session.save(exec3);
		session.save(exec4);
		session.save(exec5);

		session.save(copy1);
		session.save(copy2);
		session.save(copy3);
		session.save(copy4);
		session.save(copy5);

		session.save(stats1);
		session.save(stats2);
		session.save(stats3);
		session.save(stats4);
		session.save(stats5);

		session.save(req);

		session.save(doc);
		session.save(doc1);
		session.save(doc2);


		session.getTransaction().commit();
	}



}
