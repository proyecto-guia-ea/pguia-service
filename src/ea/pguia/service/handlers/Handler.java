package ea.pguia.service.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public abstract class Handler {
	protected class ConfirmPasswordResult {
		Boolean succeed;
		String message;
		String dni;
					
		public ConfirmPasswordResult(Boolean succeed,
				String message, String dni) {
			super();
			this.succeed = succeed;
			this.message = message;
			this.dni = dni;
		}
		
		public Boolean getSucceed() {
			return succeed;
		}
		public String getMessage() {
			return message;
		}
		
		public String getDNI() {
			return dni;
		}		
	}
	
	
	protected DataSource dataSource = null;

	public Handler() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public abstract void process(HttpServletResponse response, HttpServletRequest request) throws HandlerException ;
	
	protected final ConfirmPasswordResult confirmPassword(String name, String password, Connection connection, Statement statement) throws HandlerException, SQLException {
		// Asking database
		ConfirmPasswordResult result;
		ResultSet resultSet = null;
		
		resultSet = statement.executeQuery("select password,dni from STUDENT " +
				"where name='" + name + "';");
	
		if(resultSet.next()){
			if(resultSet.getString("password").equals(password)) {						
				result = new ConfirmPasswordResult(true, null, resultSet.getString("dni"));
			} else 						
				result = new ConfirmPasswordResult(false, "Incorrect password.", null);
		} else 
			result = new ConfirmPasswordResult(false, "The account doesn't exist.", null);
			
		return result;
	}
	
	protected Boolean isAdmin(String name, Connection connection , Statement statement) throws SQLException, HandlerException {
		boolean enterprise;	
		
		ResultSet resultSet = statement.executeQuery("select admin from user " +
				"where username='" + name + "';");	
		
		if(resultSet.next())
			enterprise = resultSet.getBoolean("admin");
		else 
			throw new HandlerException(401, "Auth needed.");	
		
		resultSet.close();
		
		return enterprise;		
	}
}
