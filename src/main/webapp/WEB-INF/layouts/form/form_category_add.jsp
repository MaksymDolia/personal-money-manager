<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="../taglib.jsp" %>
<spring:url value="/app/categories/add_category" var="form_category_add_action"/>
<form:form commandName="category" method="POST"
           cssClass="form-category-add"
           action="${form_category_add_action}">
    <fieldset>

        <!-- Text input-->
        <div class="form-group">
            <form:label path="name" cssClass="control-label">Name</form:label>
            <form:input path="name" id="name" type="text" placeholder="Name"
                        cssClass="form-control input-md"/>
            <form:errors path="name" cssClass="text-warning"/>
        </div>

        <!-- Multiple Radios (inline) -->
        <div class="form-group">
            <form:label path="operation" cssClass="radio-inline">
                <form:radiobutton path="operation" value="EXPENSE" checked="checked"/> Expense
            </form:label>
            <form:label path="operation" cssClass="radio-inline">
                <form:radiobutton path="operation" value="INCOME"/>
                Income
            </form:label>
            <form:errors path="operation" cssClass="text-warning"/>
        </div>
        <div class="form-group">
            <button type="submit" name="submit" class="btn btn-info">Add</button>
        </div>

    </fieldset>
</form:form>