package ea.pguia.service.handlers.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ea.pguia.model.Enrollement;
import ea.pguia.service.ServletMethod;
import ea.pguia.service.handlers.Handler;
import ea.pguia.service.handlers.HandlerException;

public class QueryEnrollement extends Handler {

	@Override
	public void process(HttpServletResponse response, HttpServletRequest request) throws HandlerException {
		Connection connection = null;
		Statement statement = null;
		
		// Getting parameters
		String name = (String) request.getSession().getAttribute("username");
		String password = request.getParameter("password");
		if(name == null || password == null)
			throw new HandlerException(400, "Missing parameter in " + this.getClass().getSimpleName());
		
		ConfirmPasswordResult confirmPassResult;		
		try {
			connection = dataSource.getConnection();			
			statement = connection.createStatement();
			
			confirmPassResult = confirmPassword(name, password, connection, statement);
			if(!confirmPassResult.getSucceed())
				throw new HandlerException(401, "Auth needed.");
			
			String result = Enrollement.fromDB(name, connection).toJSON();

			// Sending JSON result
			ServletMethod.sendResult(result, request, response);
			
		} catch (SQLException e1) {
			throw new HandlerException(400, "Database error: " + e1.getMessage());
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
	}	
}
