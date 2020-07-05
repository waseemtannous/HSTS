package com.theDreamTeam.server;


import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.theDreamTeam.entities.*;
import javafx.util.Pair;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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

	String url = "jdbc:mysql://localhost/hsts";
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

			initializeData();	// method that initializes the database with users and courses

		} catch (Exception exception) {
			if (session != null) {
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
		course.addQuestion(question);
		question.setId((course.getId() * 100) + course.getQuestionId());
		session.save(question);
		course.setQuestionId(course.getQuestionId() + 1);
		session.update(course);
	}

	public void saveRegularExam(RegularExam exam, int courseId) {
		Course course = session.get(Course.class, courseId);
		Teacher teacher = session.get(Teacher.class, exam.getTeacher().getId());
		exam.setTeacher(teacher);
		exam.setCourse(course);
		course.getRegularExams().add(exam);
		for (GradedQuestion question : exam.getGradedQuestions()) {
			question.setCourse(course);
			session.save(question);
		}
		exam.setId((courseId * 100) + course.getEwxamId());
		session.save(exam);
		session.update(teacher);
		course.setEwxamId(course.getEwxamId() + 1);
		session.update(course);
	}

	public void saveDocumentExam(DocumentExam exam, int courseId) {
		Course course = session.get(Course.class, courseId);
		Teacher teacher = session.get(Teacher.class, exam.getTeacher().getId());
		exam.setTeacher(teacher);
		exam.setCourse(course);
		course.getDocumentExams().add(exam);
		exam.setId((courseId * 100) + course.getEwxamId());
		session.save(exam);
		session.update(teacher);
		course.setEwxamId(course.getEwxamId() + 1);
		session.update(course);
	}

	public boolean saveExecutableExam(ExecutableExam exam) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExecutableExam> query = builder.createQuery(ExecutableExam.class);
		query.from(ExecutableExam.class);
		List<ExecutableExam> executableExams = session.createQuery(query).getResultList();
		for (ExecutableExam executableExam : executableExams) {
			if (executableExam.getCode().equals(exam.getCode())) {
				return false;
			}
		}
		if (exam.getType().equals("regular"))
			exam.setRegularExam(session.get(RegularExam.class, exam.getRegularExam().getId()));
		else
			exam.setDocumentExam(session.get(DocumentExam.class, exam.getDocumentExam().getId()));
		exam.setExecTeacher(session.get(Teacher.class, exam.getExecTeacher().getId()));
		session.save(exam);
		Statistics stats = new Statistics(exam);
		session.save(stats);
		return true;
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

	//Changes the connected status.
	public synchronized void logOut(User user) {
		user.setConnected(false);
		session.update(user);
	}

	public static void closeConnection() {
		session.close();
	}

	public List<ExamCopy> getExamsCopy(Teacher teacher) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamCopy> query = builder.createQuery(ExamCopy.class);
		query.from(ExamCopy.class);
		List<ExamCopy> exams = session.createQuery(query).getResultList();
		List<ExamCopy> copies = new ArrayList<>();
		for (ExamCopy exam : exams){
			if (exam.getExecutableExam().getType().equals("regular")) {
				if (exam.getExecutableExam().getExecTeacher().getId() == teacher.getId()
						|| exam.getExecutableExam().getRegularExam().getTeacher().getId() == teacher.getId()) {
					copies.add(exam);
				}
			} else if (exam.getExecutableExam().getExecTeacher().getId() == teacher.getId()
					|| exam.getExecutableExam().getDocumentExam().getTeacher().getId() == teacher.getId()) {
				copies.add(exam);
			}}
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

	public void deleteTimeRequest(ExtendTimeRequest time) {
		try (Connection connection = DriverManager.getConnection(url, username, password)) {
			String sql = "DELETE FROM extendtimerequest WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, ((Integer) time.getId()).toString());
			int rows = statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		return session.createQuery(query).getResultList();
	}

	public List<ExtendTimeRequest> getAllExtendTimeRequest() {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExtendTimeRequest> query = builder.createQuery(ExtendTimeRequest.class);
		query.from(ExtendTimeRequest.class);
		List<ExtendTimeRequest> requests = session.createQuery(query).getResultList();
		for (ExtendTimeRequest request : requests) {
			ExecutableExam executableExam = session.get(ExecutableExam.class, request.getExecutableExam().getId());
			Teacher teacher = session.get(Teacher.class, executableExam.getExecTeacher().getId());
			executableExam.setExecTeacher(teacher);
			if (executableExam.getType().equals("regular"))
				executableExam.setRegularExam(session.get(RegularExam.class, executableExam.getRegularExam().getId()));
			else
				executableExam.setDocumentExam(session.get(DocumentExam.class, executableExam.getDocumentExam().getId()));

			executableExam.getExam().setCourse(session.get(Course.class, executableExam.getExam().getCourse().getId()));
			request.setExecutableExam(executableExam);
		}
		return requests;

	}

	public List<Course> getAllCourses() {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Course> query = builder.createQuery(Course.class);
		query.from(Course.class);
		return session.createQuery(query).getResultList();
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

	// Get by ExecutableExam Code
	public ExecutableExam getExecutableExamByCode(String ExamCode) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExecutableExam> query = builder.createQuery(ExecutableExam.class);
		query.from(ExecutableExam.class);
		List<ExecutableExam> exams = session.createQuery(query).getResultList();
		for (ExecutableExam exam : exams) {
			if (exam.getCode().equals(ExamCode)) {
				if (exam.getType().equals("regular")) {
					RegularExam reg = session.get(RegularExam.class, exam.getRegularExam().getId());
					List<GradedQuestion> questions = new ArrayList<>();
					for (GradedQuestion gradedQuestion : reg.getGradedQuestions()) {
						questions.add(session.get(GradedQuestion.class, gradedQuestion.getId()));
					}
					reg.setGradedQuestions(questions);
					exam.setRegularExam(reg);
				}
				else {

					exam.setDocumentExam(session.get(DocumentExam.class, exam.getDocumentExam().getId()));
				}
				return exam;
			}
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
				List<Course> courses = new ArrayList<>();
				for (Course course : temp.getCourses()) {
					courses.add(session.get(Course.class, course.getId()));
				}
				temp.setCourses(courses);
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
		List<Statistics> stats = new ArrayList<>();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Statistics> query = builder.createQuery(Statistics.class);
		query.from(Statistics.class);
		List<Statistics> allStats = session.createQuery(query).getResultList();
		if (user instanceof Teacher) {
			for (Statistics stat : allStats) {
				if (stat.getExecutableExam().getExam().getCourse().getId() == courseId) {
					if (stat.getExecutableExam().getExecTeacher().getId() == user.getId() ||
							stat.getExecutableExam().getExam().getTeacher().getId() == user.getId()) {
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


	private static void initializeData() {
		Teacher oren = new Teacher("oren", SHA256("oren"));
		oren.setId(1234);

		Teacher malki = new Teacher("malki", SHA256("malki"));
		malki.setId(5678);

		Student waseem = new Student("waseem", SHA256("waseem"));
		waseem.setId(123456789);
		Student faroq = new Student("faroq", SHA256("faroq"));
		faroq.setId(987654321);


		Manager dani = new Manager("dani", SHA256("dani"));
		dani.setId(789);

		List<Teacher> list1 = new ArrayList<>();
		List<Teacher> list2 = new ArrayList<>();
		list1.add(oren);
		Course course1 = new Course("Data Structures", list1);
		course1.setId(10);
		list2.add(malki);
		Course course2 = new Course("Software Engineering", list2);
		course2.setId(11);

		course1.addStudent(waseem);
		course2.addStudent(faroq);

		session.save(oren);
		session.save(malki);

		session.save(waseem);
		session.save(faroq);

		session.save(dani);

		session.save(course1);
		session.save(course2);

		session.getTransaction().commit();
	}



}
