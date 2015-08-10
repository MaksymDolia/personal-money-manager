<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../taglib.jsp"%>
<spring:url value="/app/accounts/${account.id}/edit" var="form_account_edit_action" />
<form:form commandName="account" method="POST"
	cssClass="form-account-edit"
	action="${form_account_edit_action}">
	<div class="form-group">
		<label class="contol-label" for="modal-name">Account Name</label>
		<form:input type="text" path="name" cssClass="form-control"
			id="modal-name" placeholder="Account Name" autofocus="autofocus" />
		<form:errors path="name" cssClass="text-warning"/>
	</div>
	<div class="form-group">
		<label class="contol-label" for="modal-amount">Amount</label>
		<form:input type="text" path="amount" cssClass="form-control"
			id="modal-amount" placeholder="Amount" />
		<form:errors path="amount" cssClass="text-warning"/>
	</div>
	<div class="form-group">
		<label class="contol-label" for="modal-currency">Currency</label>
		<form:select path="currency" cssClass="form-control"
			id="modal-currency">
			<form:option value="UAH" />
		<%--  to be realized in future 
			<form:option value="EUR" />
			<form:option value="USD" /> --%>
		</form:select>
		<form:errors path="currency" cssClass="text-warning"/>
	</div>
	<div>
		<button type="submit" class="btn btn-success">Save</button>
		<a class="btn btn-danger trigger-remove"
			href='<spring:url value="/app/categories/${account.id}/remove"/>'
			data-toggle="remove-modal" data-target="#remove-modal">Remove</a>
	</div>
	<p class="text-warning">${message}</p>
</form:form>

<!-- TRANSFER ACCOUNT FORM -->

<c:if test="${quantityOfTransactions != 0}">
	<hr>
	<form action="/app/accounts/transfer" method="POST">
		<fieldset>

			<!-- Select Basic -->
			<div class="form-group">
				<label class="control-label" for="toAccount">Transfer all
					transactions ( ${quantityOfTransactions} ) to account</label>
				<div>
					<select id="toAccount" name="toAccount" class="form-control">
						<c:forEach items="${accounts}" var="itemAccount">
							<option value="${itemAccount.id}">${itemAccount.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<input type="hidden" name="fromAccount" value="${account.id}">
			<button type="submit" class="btn btn-info">Transfer</button>
		</fieldset>
	</form>
</c:if>
