<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../layouts/taglib.jsp"%>
<div class="container">
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

				<table class="table table-hover">
					<tbody>
						<c:forEach items="${transactions}" var="itemTransaction">
							<tr>
								<td><fmt:formatDate value="${itemTransaction.date}"
										type="date" dateStyle="medium" /></td>
								<td><a
									href='<spring:url value="/app/accounts/${itemTransaction.account.id}" />'><c:out
											value="${itemTransaction.account.name}" /></a><span
									class="glyphicon glyphicon-arrow-${itemTransaction.operation eq 'EXPENSE' ? 'right expense' : itemTransaction.operation eq 'INCOME' ? 'left income' : 'right transfer' }"
									aria-hidden="true"></span> <c:choose>
										<c:when test="${itemTransaction.operation eq 'TRANSFER'}">
											<a
												href='<spring:url value="/app/accounts/${itemTransaction.transferAccount.id}" />'><c:out
													value="${itemTransaction.transferAccount.name}" /></a>
										</c:when>
										<c:otherwise>
											<c:out value="${itemTransaction.category.name}" />
										</c:otherwise>
									</c:choose></td>
								<td><c:out value="${itemTransaction.comment}" /></td>
								<td><fmt:formatNumber type="currency"
										value="${itemTransaction.amount}"
										currencySymbol="${itemTransaction.currency}"></fmt:formatNumber></td>
								<td><a
									href='<spring:url value="/app/transactions/${itemTransaction.id}/edit" />'>
										<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
								</a><a
									href='<spring:url value="/app/transactions/${itemTransaction.id}/remove"/>'
									class="trigger-remove" data-toggle="remove-modal"
									data-target="#remove-modal"><span
										class="glyphicon glyphicon-remove red" aria-hidden="true"></span></a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<!-- Remove Confirmation Modal -->
<tiles:insertAttribute name="modal-transaction-remove" />