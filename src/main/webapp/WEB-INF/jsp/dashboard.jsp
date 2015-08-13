<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../layouts/taglib.jsp"%>
<div class="row">
	<div class="col-md-4">
		<div class="panel panel-default">
			<div class="panel-body">
				<table class="table table-hover">
					<thead>
						<tr>
							<td><h3>Balance:</h3></td>
							<td><h3 class="text-success text-right">
									<strong><fmt:formatNumber type="currency"
											value="${balance}" currencySymbol="UAH" /> </strong>
								</h3></td>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${accounts}" var="account">
							<tr>
								<td><h5>
										<a href='<spring:url value="/app/accounts/${account.id}" />'><c:out
												value="${account.name}" /></a>
									</h5></td>
								<td><h5 class="text-success text-right">
										<strong> <fmt:formatNumber type="currency"
												value="${account.amount}"
												currencySymbol="${account.currency}" />
										</strong>
									</h5></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<hr>
				<button class="btn btn-info" data-toggle="modal"
					data-target="#add_account" role="button">Add account</button>
			</div>
		</div>
	</div>
	<div class="col-md-8">
		<tiles:insertAttribute name="form_transaction_add" />
	</div>

	<!-- 		TRANSACTIONS LIST -->

	<div class="col-md-8 pull-right">
		<div class="panel panel-default">
			<div class="panel-body">
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

<div class="row"></div>

<!-- End container Modal Add Account -->
<tiles:insertAttribute name="modal-account-add" />
<!-- Remove account modal -->
<tiles:insertAttribute name="modal-transaction-remove" />