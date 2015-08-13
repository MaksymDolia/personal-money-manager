<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../layouts/taglib.jsp"%>
<tilesx:useAttribute name="action" ignore="true" />
	<div class="row">
		<div class="col-md-8">
			<div class="panel panel-default">
				<div class="panel-heading">
					<a class="btn btn-success btn-sm"
						href='<spring:url value="/app/accounts" />' role="button">Add
						account</a>
					<p class="text-warning">${message}</p>
				</div>

				<table class="table table-hover">
					<thead>
						<tr>
							<th>Balance:</th>
							<th class="text-success"><strong><fmt:formatNumber
										type="currency" value="${balance}" currencyCode="UAH" /></strong></th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${accounts}" var="account">
							<tr>
								<td><a
									href='<spring:url value="/app/accounts/${account.id}/edit" />'><c:out
											value="${account.name}" /></a></td>
								<td class="income"><fmt:formatNumber type="currency"
										value="${account.amount}" currencyCode="${account.currency}" /></td>
								<td class="hover-btn"><a
									href='<spring:url value="/app/accounts/${account.id}/edit" />'>
										<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
								</a></td>
								<td><a class="trigger-remove"
									href='<spring:url value="/app/accounts/${account.id}/remove"/>'
									data-toggle="remove-modal" data-target="#remove-modal"><span
										class="glyphicon glyphicon-remove red" aria-hidden="true"></span></a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="col-md-4 panel panel-default">
			<div class="panel-body">
				<tiles:insertAttribute name="form" />
			</div>
		</div>
	</div>

<!-- Remove Confirmation Modal -->
<tiles:insertAttribute name="modal-account-remove" />