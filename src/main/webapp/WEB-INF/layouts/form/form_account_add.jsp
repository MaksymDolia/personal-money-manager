<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="../taglib.jsp" %>
<spring:url value="/app/accounts/add_account" var="form_account_add_action"/>
<form:form commandName="account" method="POST"
           cssClass="form-account-add" action="${form_account_add_action}">
    <div class="form-group">
        <label class="contol-label" for="modal-name">Account Name</label>
        <form:input type="text" path="name" cssClass="form-control"
                    id="modal-name" placeholder="Account Name" autofocus="autofocus"/>
        <form:errors path="name" cssClass="text-warning"/>
    </div>
    <div class="form-group">
        <label class="contol-label" for="modal-amount">Amount</label>
        <form:input type="text" path="amount" cssClass="form-control"
                    id="modal-amount" placeholder="Amount"/>
        <form:errors path="amount" cssClass="text-warning"/>
    </div>
    <div class="form-group">
        <label class="contol-label" for="modal-currency">Currency</label>
        <form:select path="currency" cssClass="form-control"
                     id="modal-currency">
            <form:option value="UAH"/>
            <%--  to be realized in future
            <form:option value="EUR" />
            <form:option value="USD" /> --%>

        </form:select>
        <form:errors path="currency" cssClass="text-warning"/>
    </div>
    <button type="submit" class="btn btn-success">Add account</button>
</form:form>