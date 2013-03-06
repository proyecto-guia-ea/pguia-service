package ea.pguia.service.handlers.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ea.pguia.model.Course;
import ea.pguia.service.ServletMethod;
import ea.pguia.service.handlers.Handler;
import ea.pguia.service.handlers.HandlerException;

public class QueryCourses extends Handler {

	@Override
	public void process(HttpServletResponse response, HttpServletRequest request)
			throws HandlerException {
		Connection connection = null;
		Statement statement = null;

		// Getting parameters
		String name = (String) request.getSession()
				.getAttribute("name");
		String password = (String) request.getParameter("password");
		if (name == null || password == null)
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

			result = Course.toJSONVector(Course.fromDB(statement
					.executeQuery("Select course.*,subject.* "
							+ "FROM course,subject "
							+ "WHERE subject.id=course.subject AND "
							+ "course.enabled=true;")));

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
