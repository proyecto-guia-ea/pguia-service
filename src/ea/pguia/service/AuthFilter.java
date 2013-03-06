package ea.pguia.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ea.pguia.model.AccountProfile;
import ea.pguia.service.handlers.HandlerException;
import ea.pguia.service.handlers.HandlerFactory;
import ea.pguia.service.handlers.HandlerInfo;

/**
 * Servlet Filter implementation class AuthFilter
 */
@WebFilter("/AuthFilter")
public class AuthFilter implements Filter {

    /**
     * Default constructor. 
     */
    public AuthFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		// Getting action parameter
		String action = (String) request.getParameter("action");
		if(action == null) {
			ServletMethod.sendError("Missing parameter.", 400,
					(HttpServletRequest) request, (HttpServletResponse) response);
			return;
		}
		
		// Getting handler info
		HandlerInfo hInfo;
		try {
			hInfo = HandlerFactory.newInstance().getInfo(action);
		} catch (HandlerException e) {
			ServletMethod.sendError(e.getMessage(), e.getCode(), 
					(HttpServletRequest) request, (HttpServletResponse) response);
			return;
		}
		
		// If auth needed, check httpSessions		
		if(hInfo.needAuth()) {
			if(((HttpServletRequest) request).getSession().getAttribute("name") == null) {
				ServletMethod.sendError("Auth needed.", 401, (HttpServletRequest) request, (HttpServletResponse) response);
				return;
			}
		}
		
		// If profile needed, check in the database
		if(hInfo.needProfile()) {
			String username = (String) ((HttpServletRequest) request).getSession().getAttribute("name");
			if(username == null) {
				ServletMethod.sendError("Missing parameter.", 400, (HttpServletRequest) request, (HttpServletResponse) response);
				return;
			}
			
			Connection connection = null;	
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				connection = HandlerFactory.newInstance().getDataSource().getConnection();
				statement = connection.createStatement();
				resultSet = statement.executeQuery("select admin from STUDENT where name='" +
						username + "';");
				if(!(resultSet.next() && !(resultSet.getBoolean("admin") ^ (hInfo.getProfile() == AccountProfile.Admin)))) {
					ServletMethod.sendError("With your account profile you can't do this action.", 401, (HttpServletRequest) request, (HttpServletResponse) response);
					return;
				}	
			} catch (SQLException e) {
				ServletMethod.sendError(e.getMessage(), 400, 
						(HttpServletRequest) request, (HttpServletResponse) response);
				return;
			} finally {
				try {	
					if(resultSet != null)
						resultSet.close();
					
					if(statement != null)
						statement.close();
					
					if(connection != null)
						connection.close();						
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	
	}

}
