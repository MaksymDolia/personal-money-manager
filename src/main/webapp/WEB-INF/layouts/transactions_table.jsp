<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="taglib.jsp"%>
<table class="table table-hover">
	<tbody>
		<c:forEach items="${transactions}" var="itemTransaction">
			<tr>
				<td><fmt:formatDate value="${itemTransaction.date}" type="date"
						dateStyle="medium" /></td>
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