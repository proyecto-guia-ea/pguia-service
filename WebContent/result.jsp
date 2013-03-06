<%@ page language="java" contentType="text/javascript; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	String output = null;
	if (request.getParameter("callback") != null)
		output = request.getParameter("callback") + "("
				+ request.getAttribute("result") + ")";
	else
		output = request.getAttribute("result").toString();	
%>

<%= output %>