<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../layouts/taglib.jsp"%>
<div class="row">
	<div class="col-md-12">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button id="btn-collapse" class="btn btn-info" type="button"
					data-toggle="collapse" data-target="#form_transaction"
					aria-expanded="false" aria-controls="form_transaction">Add
					transaction</button>
				<p class="text-warning">${message}</p>
			</div>
			<div class="panel-body">
				<div class="collapse" id="form_transaction">
					<tiles:insertAttribute name="form" />
					<hr />
				</div>

				<div class="row">
					<form:form commandName="showTransactionForm" method="get"
						id="showTransactionForm" class="form-inline">
						<div class="form-group">
							<label for="fromDate">Transactions from </label>
							<form:input path="fromDate" type="date" class="form-control"
								id="fromDate" />
						</div>
						<div class="form-group">
							<label for="toDate"> to </label>
							<form:input path="toDate" type="date" class="form-control"
								id="toDate" />
						</div>
						<div class="form-group">
							<label class="radio inline control-label"> sorted by </label>
							<div class="btn-group" data-toggle="buttons">
								<label id="date" class="btn btn-default radio-inline active">
									<form:radiobutton path="sortBy" id="date" value="DATE" />Date
								</label> <label id="account" class="btn btn-default radio-inline">
									<form:radiobutton path="sortBy" id="account" value="ACCOUNT" />Category
								</label> <label id="category" class="btn btn-default radio-inline">
									<form:radiobutton path="sortBy" id="category" value="CATEGORY" />Account
								</label><label id="amount" class="btn btn-default radio-inline">
									<form:radiobutton path="sortBy" id="amount" value="AMOUNT" />Amount
								</label>
							</div>
						</div>
						<div class="form-group">
							<label class="sr-only" for="comment">Comment</label>
							<form:input path="comment" class="form-control" id="comment"
								placeholder="Comment" />
						</div>
						<button type="submit" class="btn btn-info">Show</button>
					</form:form>
					<form:errors path="showTransactionForm" />
				</div>
			</div>

			<tiles:insertAttribute name="transactions_table"/>
		</div>
	</div>
</div>

<!-- Remove Confirmation Modal -->
<tiles:insertAttribute name="modal-transaction-remove" />