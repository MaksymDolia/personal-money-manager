<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../taglib.jsp"%>
<spring:url value="/app/transactions/add_transaction" var="form_transaction_add_action" />
<div class="panel panel-default" id="transaction_box">
	<form:form commandName="transaction" cssClass="form-horizontal" id="form-transaction"
		method="POST" action="${form_transaction_add_action}">
		<div class="panel-body">

			<spring:bind path="operation">
				<div class="btn-group" data-toggle="buttons">
					<label id="operation-expense" class="btn btn-default active">
						<form:radiobutton path="operation" id="btn-operation-expense"
							value="EXPENSE" checked="checked" />Expense
					</label> <label id="operation-transfer" class="btn btn-default"> <form:radiobutton
							id="btn-operation-transfer" path="operation" value="TRANSFER" />
						Transfer
					</label> <label id="operation-income" class="btn btn-default"> <form:radiobutton
							id="btn-operation-income" path="operation" value="INCOME" />
						Income
					</label>
				</div>
			</spring:bind>

			<div class="col-md-12" id="transaction_body">

				<div id="1" class="row">
					<div class="form-group">
						<label id="account" for="account" class="col-sm-3 control-label">From
							account</label>
						<div class="col-sm-3">

							<form:select path="account" cssClass="form-control" id="account"
								items="${accounts}" itemValue="id" itemLabel="name" />
							<form:errors path="account" cssClass="text-warning"/>
						</div>
						<label class="sr-only" for="amount">Amount </label>
						<div class="col-sm-4">
							<form:input path="amount" type="text" cssClass="form-control"
								id="amount" placeholder="Amount" />
							<form:errors path="amount" cssClass="text-warning"/>
						</div>

						<label class="sr-only" for="fromCurrency">Currency</label>
						<div class="col-sm-2">
							<form:select path="currency" id="fromCurrency"
								cssClass="form-control">
								<form:option value="UAH" />
							<%--  to be realized in future 
			<form:option value="EUR" />
			<form:option value="USD" /> --%>
							</form:select>
							<form:errors path="currency" cssClass="text-warning"/>
						</div>
					</div>
				</div>
				<div id="2-expense" class="row">
					<div class="form-group">

						<!--  -->

						<label id="expense" for="category" class="col-sm-3 control-label">Category</label>
						<div id="first-column-expense" class="col-sm-3">
							<spring:bind path="category">
								<form:select path="category" cssClass="form-control"
									id="category" disabled="">
									<form:options items="${expenseCategories}" itemLabel="name"
										itemValue="id" />
								</form:select>
								<form:errors path="category" cssClass="text-warning"/>
							</spring:bind>
						</div>

						<spring:bind path="date">
							<label for="date" class="sr-only">Date</label>
							<div class="col-sm-4">
								<form:input path="date" type="date" cssClass="form-control"
									id="date" />
								<form:errors path="date" cssClass="text-warning"/>
							</div>
						</spring:bind>
					</div>
				</div>

				<!--  -->

				<div id="2-transfer" class="row hidden">
					<div class="form-group">
						<spring:bind path="transferAccount">
							<label for="transferAccount" class="col-sm-3 control-label">To
								account</label>
							<div class="col-sm-3">
								<form:select path="transferAccount" cssClass="form-control"
									disabled="true" id="transferAccount" items="${accounts}"
									itemLabel="name" itemValue="id" />
								<form:errors path="transferAccount" cssClass="text-warning"/>
							</div>
						</spring:bind>
						<label class="sr-only" for="transferCurrency">Currency</label>
						<div class="col-sm-2">
							<form:select path="transferCurrency" id="transferCurrency"
								disabled="true" cssClass="form-control">
								<form:option value="UAH" />
								<%--  to be realized in future 
			<form:option value="EUR" />
			<form:option value="USD" /> --%>
							</form:select>
						</div>
					</div>
				</div>

				<div id="2-income" class="row hidden">
					<div class="form-group">
						<label for="category" class="col-sm-3 control-label">Category</label>
						<div class="col-sm-3">
							<spring:bind path="category">
								<form:select path="category" cssClass="form-control"
									disabled="true" id="category">
									<form:options items="${incomeCategories}" itemLabel="name"
										itemValue="id" />
								</form:select>
								<form:errors path="category" class="control-label" />
							</spring:bind>
						</div>
						<spring:bind path="date">
							<label for="date" class="sr-only">Date</label>
							<div class="col-sm-4">
								<form:input path="date" type="date" cssClass="form-control"
									id="date" disabled="true" />
								<form:errors path="date" cssClass="text-warning"/>
							</div>
						</spring:bind>
					</div>
				</div>

				<!--  -->

				<div id="3" class="row">
					<div class="form-group">
						<label for="comment" class="sr-only">Comment</label>
						<div class="col-sm-6">
							<form:input path="comment" type="text" cssClass="form-control"
								id="comment" placeholder="Comment" />
							<form:errors path="comment" cssClass="text-warning"/>
						</div>

						<div class="col-sm-2">
							<button type="submit" class="btn btn-info">Add</button>
						</div>

						<spring:bind path="date">
							<label id="date" for="date" class="sr-only hidden">Date</label>
							<div id="date" class="col-sm-4 hidden">
								<form:input path="date" type="date" cssClass="form-control"
									id="date" disabled="true" />
								<form:errors path="date" cssClass="text-warning"/>
							</div>
						</spring:bind>
					</div>
				</div>
			</div>
		</div>
	</form:form>
</div>