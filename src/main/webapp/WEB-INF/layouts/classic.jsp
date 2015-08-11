<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../layouts/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><tiles:getAsString name="title" /></title>
<link rel="shortcut icon" type="image/x-icon" href='<spring:url value="/favicon.ico" />'/>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href='<spring:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" />'>
<!-- CUSTOM CSS -->
<spring:url value="/css/style.css" var="mainCss"/>
<link rel="stylesheet" href="${mainCss}">
<!-- Optional theme -->
<link rel="stylesheet"
	href='<spring:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css" />'>
<script
	src='<spring:url value="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" />'></script>
<!-- Latest compiled and minified JavaScript -->
<script
	src='<spring:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js" />'></script>
<script
	src='<spring:url value="http://cdn.jsdelivr.net/jquery.validation/1.14.0/jquery.validate.min.js" />'></script>
</head>
<body>

	<tilesx:useAttribute name="current" />

	<!-- Static navbar -->
	<nav class="navbar navbar-default navbar-static-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href='<spring:url value="/" />'>My
				finance</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li class="${current == 'dashboard' ? 'active' : ''}"><a
					href='<spring:url value="/app" />'>Dashboard</a></li>
				<li class="${current == 'transactions' ? 'active' : ''}"><a
					href='<spring:url value="/app/transactions" />'>Transactions</a></li>
				<li class="${current == 'accounts' ? 'active' : ''}"><a
					href='<spring:url value="/app/accounts" />'>Accounts</a></li>
				<li class="${current == 'categories' ? 'active' : ''}"><a
					href='<spring:url value="/app/categories" />'>Categories</a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<security:authorize access="isAuthenticated()">
					<li><a>${email}</a></li>
					<li class="${current == 'profile' ? 'active' : ''}"><a
						href='<spring:url value="/profile" />'>My Profile</a></li>
					<li><a href='<spring:url value="/logout" />'>Logout</a></li>
				</security:authorize>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
	</nav>

	<tiles:insertAttribute name="body" />
	<tiles:insertAttribute name="footer" />
	<!-- CUSTOM JS -->
	<spring:url value="/js/script.js" var="mainJs" />
	<script src="${mainJs}"></script>
	<tiles:insertAttribute name="include" />
</body>
</html>