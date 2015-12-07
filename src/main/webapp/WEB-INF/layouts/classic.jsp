<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="../layouts/taglib.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><tiles:getAsString name="title"/></title>
    <link rel="shortcut icon" type="image/x-icon"
          href='<spring:url value="/favicon.ico" />'/>
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

    <script>
        (function (i, s, o, g, r, a, m) {
            i['GoogleAnalyticsObject'] = r;
            i[r] = i[r] || function () {
                        (i[r].q = i[r].q || []).push(arguments)
                    }, i[r].l = 1 * new Date();
            a = s.createElement(o),
                    m = s.getElementsByTagName(o)[0];
            a.async = 1;
            a.src = g;
            m.parentNode.insertBefore(a, m)
        })(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');

        ga('create', 'UA-62270039-4', 'auto');
        ga('send', 'pageview');

    </script>

</head>
<body>

<tilesx:useAttribute name="current"/>

<!-- Static navbar -->
<nav class="navbar navbar-default">
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
            <security:authorize access="isAuthenticated()">
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
            </security:authorize>
            <ul class="nav navbar-nav navbar-right">
                <security:authorize access="isAuthenticated()">
                    <li><a><security:authentication
                            property="principal.username"/></a></li>
                    <li class="${current == 'profile' ? 'active' : ''}"><a
                            href='<spring:url value="/profile" />'>My Profile</a></li>
                    <li>
                        <form:form class="navbar-form navbar-right" method="POST" action="/logout">
                            <button type="submit" class="btn btn-link">Logout</button>
                        </form:form>
                    </li>
                </security:authorize>
                <security:authorize access="!isAuthenticated()">
                    <li class="${current == 'login' ? 'active' : ''}"><a
                            href='<spring:url value="/login" />'><spring:message
                            code="Login.index.menu"/></a></li>
                    <li class="${current == 'signin' ? 'active' : ''}"><a
                            href='<spring:url value="/signin" />'><spring:message
                            code="Signin.index.menu"/></a></li>
                </security:authorize>
            </ul>
        </div>
    </div>
    <!--/.nav-collapse --> </nav>

<div class="container">
    <tiles:insertAttribute name="body"/>
    <tiles:insertAttribute name="footer"/>
</div>

<!-- CUSTOM JS -->
<spring:url value="/js/script.js" var="mainJs"/>
<script src="${mainJs}"></script>
<tiles:insertAttribute name="include"/>
</body>
</html>