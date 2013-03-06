package ea.pguia.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.google.gson.Gson;


public class Course {
	private int id;
	private Subject subject;
	private String date;
	private int mark;
	private boolean enabled;
		
	public Course(int id, Subject subject, String date, boolean enabled) {
		super();
		this.id = id;
		this.subject = subject;
		this.date = date;
		this.mark = -1;
		this.enabled = enabled;		
	}
	
	public void setMark(int mark) {
		this.mark = mark;
	}
	
	public String getMark() {
		if(!enabled)
			return Integer.toString(mark);
		else 
			return "Unqualified";
	}
	public int getId() {
		return id;
	}
	public Subject getSubject() {
		return subject;
	}
	public String getDate() {
		return date;
	}
	public boolean isEnabled() {
		return enabled;
	}

	public String toJSON() {
		return "{\"id\":" + id + ",\"subject\":" + subject.toJSON() + "," +
				"\"date\":\"" + date + "\",\"mark\":" + mark + ",\"enabled\":" + enabled + "}";
	}
	
	public static Course fromJSON(String json) {
		return new Gson().fromJson(json, Course.class);
	}
	
	public static String toJSONVector(Course[] events) {
		return new Gson().toJson(events);
	}
	
	public static Course[] fromJSONVector(String json) {
		return new Gson().fromJson(json, Course[].class);
	}
	
	public static Course[] fromDB(ResultSet resultSet) throws SQLException {
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
			courses.add(course);
		}			
		return courses.toArray(new Course[courses.size()]);
	}
	
	@Override
	public String toString() {
		return "Course [id=" + id + ", subject=" + subject + ", date=" + date
				+ ", mark=" + getMark() + ", enabled=" + enabled + "]";
	}

	public static void main(String[] args) {
		Course c = new Course(0, new Subject(0, "Mates", 6), "1992-10-2", false);
		c.setMark(9);
		// Testing toJSON()
		System.out.println(c.toJSON());
		System.out.println();
		
		// Testing fromJSON()
		Course c2 = Course.fromJSON(c.toJSON());
		System.out.println(c2.toString());
		System.out.println();
		
		// Testing fromDB		
		Connection connect = null;
		Statement statement = null;
		try {
			// This will load the MySQL driver	
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "mimara");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();			
			statement.execute("use pguia");	
			
			Course[] courses = Course.fromDB(statement.executeQuery("select * from subject,course where subject.id=course.subject;"));	
			for(Course course: courses)
				System.out.println(course);
			
			System.out.println(Course.toJSONVector(courses));
			
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(connect != null)
					connect.close();
				if(statement != null)
					statement.close();	
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}