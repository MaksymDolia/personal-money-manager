<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="../layouts/taglib.jsp" %>
<c:if test="${not empty message or not empty param.message}">
    <div class="row">
        <div class="col-md-12">
            <div class="alert alert-info alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <c:choose>
                    <c:when test="${param.message eq 'logout_success'}">
                        <spring:message code="Logout.index.message"/>
                    </c:when>
                    <c:when test="${message eq 'delete_profile_success'}">
                        <spring:message code="DeleteProfileSuccess.index.message"/>
                    </c:when>
                </c:choose>
            </div>
        </div>
    </div>
</c:if>

<div class="jumbotron">
    <div class="row">
        <div class="col-md-8">
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
    </div>
</div>