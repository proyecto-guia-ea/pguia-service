package ea.pguia.model;

import java.util.Vector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;

public class Enrollement {

	private Course[] courses;
	
	public Enrollement(Course[] courses) {
		super();
		this.courses = courses;
	}

	public String toJSON() {
		return "{\"courses\":" + Course.toJSONVector(courses) + "}";
	}
	
	public static Course fromJSON(String json) {
		return new Gson().fromJson(json, Course.class);
	}
	
	public static Enrollement fromDB(String name, Connection connection) throws SQLException {
		
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(
				"SELECT subject.*,course.*,mark " +
				"FROM subject,course,rl_co_st,student " +
				"WHERE subject.id=course.subject AND course.id=rl_co_st.course AND " +
				"rl_co_st.student=student.id AND student.name='" + name + "';");
		
		Vector<Course> courses = new Vector<>();
		while(resultSet.next()) {
			Course course = new Course(
					resultSet.getInt("course.id"), 
					new Subject(
						resultSet.getInt("subject.id"), 
						resultSet.getString("name"), 
						resultSet.getInt("credits")),
					resultSet.getString("date"),
					resultSet.getBoolean("enabled")
			);
			course.setMark(resultSet.getInt("mark"));
			courses.add(course);
			
		}	
		try {
			statement.close();			
			resultSet.close();	
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Enrollement(courses.toArray(new Course[courses.size()]));
	}
	
	public static void main(String[] args) {
		Enrollement en;
		
		// Testing fromDB		
		Connection connection;
		try {
			// This will load the MySQL driver	
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			// Setup the connection with the DB
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pguia?autoReconnect=true", "root", "mimara");

		
			
			en = Enrollement.fromDB("fernando", connection);
			for(Course course: en.courses)
				System.out.println(course);
			
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
