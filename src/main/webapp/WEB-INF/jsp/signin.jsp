<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="../layouts/taglib.jsp" %>
<div class="row">
    <div class="col-md-4 col-md-offset-4">
        <c:if test="${success eq true}">
            <div class="alert alert-success" role="alert">
                <p>Registration is successful!</p>
            </div>
        </c:if>
        <form:form modelAttribute="user" cssClass="registration-form">
            <h2>Please sign in</h2>
            <div class="form-group">
                <form:label path="email" for="email" class="sr-only">Email address</form:label>
                <form:input type="text" path="email" id="email"
                            cssClass="form-control" placeholder="Email address"
                            autofocus="autofocus"/>
                <form:errors path="email" cssClass="text-warning"/>
            </div>
            <div class="form-group">
                <form:label path="password" for="password" class="sr-only">Password</form:label>
                <form:password path="password" id="password" cssClass="form-control"
                               placeholder="Password"/>
                <form:errors path="password" cssClass="text-warning"/>
            </div>
            <div class="form-group">
                <form:label path="password" for="password_again"
                            class="sr-only">Password again</form:label>
                <input type="password" name="password_again" id="password_again"
                       class="form-control" placeholder="Password again"/>
            </div>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Sign
                in
            </button>
        </form:form>
    </div>
</div>