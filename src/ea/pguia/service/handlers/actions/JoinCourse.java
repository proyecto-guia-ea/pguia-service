package ea.pguia.service.handlers.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 

import ea.pguia.service.ServletMethod;
import ea.pguia.service.handlers.Handler;
import ea.pguia.service.handlers.HandlerException;

public class JoinCourse extends Handler {

	@Override
	public void process(HttpServletResponse response, HttpServletRequest request)
			throws HandlerException {
		Connection connection = null;
		Statement statement = null;

		// Getting parameters
		String name = (String) request.getSession().getAttribute("name");
		String password = (String) request.getParameter("password");
		String course = (String) request.getParameter("course");
		if (name == null || password == null || course == null)
			throw new HandlerException(400, "Missing parameter in "
					+ this.getClass().getSimpleName());

		// Asking database
		ConfirmPasswordResult confirmPassResult;
		String result = "";
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();

			confirmPassResult = confirmPassword(name, password, connection,
					statement);
			if (!confirmPassResult.getSucceed())
				throw new HandlerException(401, "Auth needed.");

			if(statement.execute("INSERT INTO rl_co_st(student,course,mark) "
					+ "SELECT student.id,course.id,'0' "
					+ "FROM student,course " + "WHERE student.name='" + name + "' " 
					+ "AND course.id='" + course + "' " 
					+ "AND course.enabled=true;"))
				result = "\"Added into database.\"";
			else
				result = "\"The course doesn't exist or isn't enable\"";
			

		} catch (SQLException e) {
			throw new HandlerException(400, "Database error: " + e.getMessage());
		} finally {
			try {
				if (statement != null)
					statement.close();

				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// Sending JSON result
		ServletMethod.sendResult(result, request, response);
	}

}
