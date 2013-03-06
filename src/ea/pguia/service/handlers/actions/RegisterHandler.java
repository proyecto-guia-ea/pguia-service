package ea.pguia.service.handlers.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ea.pguia.service.ServletMethod;
import ea.pguia.service.handlers.Handler;
import ea.pguia.service.handlers.HandlerException;

public class RegisterHandler extends Handler {

	@Override
	public void process(HttpServletResponse response, HttpServletRequest request) throws HandlerException {
		Connection connection = null;
		Statement statement = null;
		
		// Getting parameters
		String username = request.getParameter("name");
		String dni = request.getParameter("dni");
		String password = request.getParameter("password");
		
		if(username == null || dni == null ||  password == null) 
			throw new HandlerException(400, "Missing parameter in " + this.getClass().getSimpleName());
		if(password.length() != 40 || dni.length() != 9)
			throw new HandlerException(400, "Bad data request into " + this.getClass().getSimpleName());
		
		// Saving user into database
		String result;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();

			statement.execute("INSERT INTO STUDENT VALUES(" +
					"null,'" + username + "','" + dni + "','" + password + "','0');");			

			result = "\"Added into database.\"";	
		} catch (SQLException e) {
			throw new HandlerException(400, "Database error: Cant add the new entry.");
		} finally {
			try {				
				if(statement != null)
					statement.close();
				
				if(connection != null)
					connection.close();						
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// Sending JSON result
		ServletMethod.sendResult(result, request, response);
	}
}
