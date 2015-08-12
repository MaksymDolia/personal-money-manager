<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../layouts/taglib.jsp"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<tilesx:useAttribute name="current" />
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">

<title><tiles:getAsString name="title" /></title>
<link rel="shortcut icon" type="image/x-icon" href='<spring:url value="/favicon.ico" />'/>
<link rel="stylesheet"
	href='<spring:url value="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" />'>
<script
	src='<spring:url value="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" />'></script>
<script
	src='<spring:url value="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js" />'></script>
<script
	src='<spring:url value="http://cdn.jsdelivr.net/jquery.validation/1.14.0/jquery.validate.min.js" />'></script>

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
      <![endif]-->
<!-- CUSTOM CSS -->
<spring:url value="/css/style.css" var="mainCss"/>
<link rel="stylesheet" href="${mainCss}">
</head>
<body>

	<div class="container">

		<!-- Static navbar -->
		<nav class="navbar navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed"
						data-toggle="collapse" data-target="#navbar" aria-expanded="false"
						aria-controls="navbar">
						<span class="sr-only">Toggle navigation</span> <span
							class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href='<spring:url value="/" />'>My
						finances</a>
				</div>
				<div id="navbar" class="navbar-collapse collapse">
					<ul class="nav navbar-nav navbar-right">

						<li class="${current == 'login' ? 'active' : ''}"><a
							href='<spring:url value="/login" />'><spring:message
									code="Login.index.menu" /></a></li>
						<li class="${current == 'signin' ? 'active' : ''}"><a
							href='<spring:url value="/signin" />'><spring:message
									code="Signin.index.menu" /></a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
			<!--/.container-fluid -->
		</nav>

		<c:if test="${not empty message or not empty param.message}">
			<div class="row">
				<div class="alert alert-info alert-dismissible" role="alert">
					<button type="button" class="close" data-dismiss="alert"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<c:choose>
						<c:when test="${param.message eq 'logout_success'}">
							<spring:message code="Logout.index.message" />
						</c:when>
						<c:when test="${message eq 'delete_profile_success'}">
							<spring:message code="DeleteProfileSuccess.index.message" />
						</c:when>
					</c:choose>
				</div>
			</div>
		</c:if>
		<div class="row">

			<tiles:insertAttribute name="body" />
		</div>

		<tiles:insertAttribute name="footer" />

	</div>


	<!-- /container -->
	<!-- CUSTOM JS -->
	<spring:url value="/js/script.js" var="mainJs" />
	<script src="${mainJs}"></script>
</body>
</html>