<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="../layouts/taglib.jsp" %>
<div class="row">
    <div class="col-md-6">
        <div>

            <!-- Nav tabs -->
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active"><a href="#expenses"
                                                          aria-controls="expenses" role="tab"
                                                          data-toggle="tab">Expenses</a></li>
                <li role="presentation"><a href="#income"
                                           aria-controls="income" role="tab" data-toggle="tab">Income</a>
                </li>
            </ul>
            <br/>
            <!-- Tab panes -->
            <div class="tab-content">
                <div role="tabpanel" class="tab-pane active" id="expenses">
                    <div class="panel panel-default">
                        <table class="table">
                            <thead>
                            <tr>
                                <th><a
                                        href='<spring:url value="/app/categories" />'><c:out
                                        value="New category"/></a></th>
                            </tr>
                            </thead>
                            <tbody>

                            <c:forEach items="${categories}" var="category">
                                <c:if test="${category.operation eq 'EXPENSE'}">
                                    <tr>
                                        <td><a
                                                href='<spring:url value="/app/categories/${category.id}/edit" />'><c:out
                                                value="${category.name}"/></a></td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane" id="income">
                    <div class="panel panel-default">
                        <table class="table">
                            <thead>
                            <tr>
                                <th><a
                                        href='<spring:url value="/app/categories" />'><c:out
                                        value="New category"/></a></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${categories}" var="category">
                                <c:if test="${category.operation eq 'INCOME'}">
                                    <tr>
                                        <td><a
                                                href='<spring:url value="/app/categories/${category.id}/edit" />'><c:out
                                                value="${category.name}"/></a></td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-6 panel panel-default">
        <div class="panel-body">
            <tiles:insertAttribute name="form"/>
        </div>
    </div>
</div>

<!-- Remove Confirmation Modal -->
<tiles:insertAttribute name="modal-category-remove"/>