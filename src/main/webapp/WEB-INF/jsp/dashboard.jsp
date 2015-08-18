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
		<div class="row">
			<div class="col-md-12">
				<tiles:insertAttribute name="form_transaction_add" />
			</div>
			<!-- 		TRANSACTIONS LIST -->
			<div class="col-md-12">
				<div class="panel panel-default">
					<div class="panel-body">
						<tiles:insertAttribute name="transactions_table" />
					</div>
				</div>
			</div>
		</div>
	</div>


</div>

<!-- End container Modal Add Account -->
<tiles:insertAttribute name="modal-account-add" />
<!-- Remove account modal -->
<tiles:insertAttribute name="modal-transaction-remove" />