<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="../taglib.jsp" %>
<script type="text/javascript">
  $(document).ready(function () {
    $(".collapse").collapse();
    <c:choose>
    <c:when test="${transaction.operation eq 'EXPENSE'}">
    $("label#operation-expense").addClass("active");
    $("label#operation-transfer").removeClass("active");
    $("label#operation-income").removeClass("active");
    $("#operation-expense").click(showExpenseFormTab());
    </c:when>
    <c:when test="${transaction.operation eq 'TRANSFER'}">
    $("#operation-transfer").click(showTransferFormTab());
    $("label#operation-expense").removeClass("active");
    $("label#operation-transfer").addClass("active");
    $("label#operation-income").removeClass("active");
    </c:when>
    <c:when test="${transaction.operation eq 'INCOME'}">
    $("label#operation-expense").removeClass("active");
    $("label#operation-transfer").removeClass("active");
    $("label#operation-income").addClass("active");
    $("#operation-income").click(showIncomeFormTab());
    </c:when>
    </c:choose>
  });
</script>