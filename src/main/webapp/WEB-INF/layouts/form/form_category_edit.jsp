<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../taglib.jsp"%>
<spring:url value="/app/categories/${category.id}/edit" var="form_category_edit_action" />
<form:form commandName="category" method="POST"
	cssClass="form-account-edit"
	action="${form_category_edit_action}">
	<fieldset>

		<!-- Text input-->
		<div class="form-group">
			<form:label path="name" cssClass="control-label">Name</form:label>
			<form:input path="name" id="name" type="text" placeholder="Name"
				cssClass="form-control input-md" autofocus="autofocus" />
			<form:errors path="name" cssClass="text-warning" />
		</div>

		<!-- Multiple Radios (inline) -->
		<div class="form-group">
			<form:label path="operation" cssClass="radio-inline">
				<form:radiobutton path="operation" value="EXPENSE" checked="checked" /> Expense
			</form:label>
			<form:label path="operation" cssClass="radio-inline">
				<form:radiobutton path="operation" value="INCOME" />
				Income
			</form:label>
			<form:errors path="operation" cssClass="text-warning" />
		</div>
		<div class="form-group">
			<button type="submit" name="submit" class="btn btn-info">Save</button>
			<a class="btn btn-danger trigger-remove"
				href='<spring:url value="/app/categories/${category.id}/remove"/>'
				data-toggle="remove-modal" data-target="#remove-modal">Remove</a>
		</div>
	</fieldset>
</form:form>

<c:if test="${not empty message}">
	<p class="text-warning">${message}</p>
</c:if>
<!-- TRANSFER TRANSACTIONS FORM -->

<c:if test="${quantityOfTransactions != 0}">
	<hr>
	<form action="/app/categories/transfer" method="POST">
		<fieldset>

			<!-- Select Basic -->
			<div class="form-group">
				<label class="control-label" for="toCategory">Transfer all
					transactions ( ${quantityOfTransactions} ) to category</label>
				<div>
					<select id="toCategory" name="toCategory" class="form-control">
						<c:set value="${category.operation}" var="operation" />
						<c:forEach items="${categories}" var="itemCategory">
							<c:if test="${itemCategory.operation eq operation}">
								<option value="${itemCategory.id}">${itemCategory.name}</option>
							</c:if>
						</c:forEach>
					</select>
				</div>
			</div>
			<input type="hidden" name="fromCategory" value="${category.id}">
			<button type="submit" class="btn btn-info">Transfer</button>
		</fieldset>
	</form>
</c:if>