<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../taglib.jsp"%>
<script>
	$(document).ready(function() {
		<c:choose>
		<c:when test="${showTransactionForm.sortBy eq 'DATE'}">
		$("label#date").addClass("active");
		$("label#account").removeClass("active");
		$("label#category").removeClass("active");
		$("label#amount").removeClass("active");
		</c:when>
		<c:when test="${showTransactionForm.sortBy eq 'ACCOUNT'}">
		$("label#date").removeClass("active");
		$("label#account").addClass("active");
		$("label#category").removeClass("active");
		$("label#amount").removeClass("active");
		</c:when>
		<c:when test="${showTransactionForm.sortBy eq 'CATEGORY'}">
		$("label#date").removeClass("active");
		$("label#account").removeClass("active");
		$("label#category").addClass("active");
		$("label#amount").removeClass("active");
		</c:when>
		<c:when test="${showTransactionForm.sortBy eq 'AMOUNT'}">
		$("label#date").removeClass("active");
		$("label#account").removeClass("active");
		$("label#category").removeClass("active");
		$("label#amount").addClass("active");
		</c:when>
		</c:choose>

	});
</script>