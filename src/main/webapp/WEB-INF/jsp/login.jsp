<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../layouts/taglib.jsp"%>
<div class="col-md-4 col-md-offset-4">
	<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
		<div class="alert alert-warning alert-dismissible" role="alert">
			<button type="button" class="close" data-dismiss="alert"
				aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			Your login attempt was not successful due to
			<c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
			.
		</div>


	</c:if>
	<form:form commandName="user">
		<h2>Please log in</h2>
		<div class="form-group">
			<form:label path="email" for="email" class="sr-only">Email address</form:label>
			<form:input type="email" path="email" id="email"
				cssClass="form-control has-feedback" placeholder="Email address"
				autofocus="autofocus" />
			<form:errors path="email" />
		</div>
		<div class="form-group">
			<form:label path="password" for="password" class="sr-only">Password</form:label>
			<form:password path="password" id="password" cssClass="form-control"
				placeholder="Password" />
			<form:errors path="password" />
		</div>
		<button class="btn btn-lg btn-primary btn-block" type="submit">Log
			in</button>
	</form:form>
</div>