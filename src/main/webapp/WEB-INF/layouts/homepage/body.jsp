<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../taglib.jsp"%>
<div class="jumbotron">
	<h1>Organize your money</h1>
	<p>This project - is a tool to manage personal money.</p>
	<p>
		<a class="btn btn-lg btn-primary"
			href='<spring:url value="/signin" />' role="button">Register</a> <a
			class="btn btn-lg btn-primary" href='<spring:url value="/login" />'
			role="button">Login</a> <a class="btn btn-lg btn-primary"
			href='<spring:url value="https://github.com/MaksymDolia/personal-money-manager" />'
			role="button">GitHub</a>
	</p>
</div>