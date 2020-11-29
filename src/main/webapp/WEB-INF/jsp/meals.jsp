<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<html>
<jsp:include page="fragments/headTag.jsp"/>
<body>
<script type="text/javascript" src="resources/js/topjava.common.js" defer></script>
<script type="text/javascript" src="resources/js/topjava.meals.js" defer></script>

<jsp:include page="fragments/bodyHeader.jsp"/>

<section>
    <div class="content">
        <form id="filterForm">
            <div class="form-row">
                <div class="offset-1 col-2">
                    <div class="form-group">
                        <label for="startDate" class="col-form-label"><spring:message code="meal.startDate"/>:</label>
                        <input class="form-control" type="date" id="startDate" name="startDate"
                               value="${param.startDate}">
                    </div>
                </div>
                <div class="col-2">
                    <div class="form-group">
                        <label for="endDate" class="col-form-label"><spring:message code="meal.endDate"/>:</label>
                        <input class="form-control" type="date" id="endDate" name="endDate" value="${param.endDate}">
                    </div>
                </div>
            </div>
            <div class="form-row">
                <div class="offset-1 col-2">
                    <div class="form-group">
                        <label for="startTime" class="col-form-label"><spring:message code="meal.startTime"/>:</label>
                        <input class="form-control" type="time" id="startTime" name="startTime"
                               value="${param.startTime}">
                    </div>
                </div>
                <div class="col-2">
                    <div class="form-group">
                        <label for="endTime" class="col-form-label"><spring:message code="meal.endTime"/>:</label>
                        <input class="form-control" type="time" id="endTime" name="endTime" value="${param.endTime}">
                    </div>
                </div>
            </div>
            <button class="offset-1 btn btn-danger" type="button" onclick="clearFilter()"><span class="fa fa-remove"></span><spring:message code="common.cancel"/></button>
            <button class="btn btn-primary" type="button" onclick="filter()"><span class="fa fa-filter"></span><spring:message code="meal.filter"/></button>
        </form>
    </div>
    <div class="jumbotron pt-4">
        <div class="container">
            <h3><spring:message code="meal.title"/></h3>
            <button class="btn btn-primary" onclick="add()">
                <span class="fa fa-plus"></span>
                <spring:message code="common.add"/>
            </button>
            <table class="table table-striped" id="datatable">
                <thead>
                <tr>
                    <th><spring:message code="meal.dateTime"/></th>
                    <th><spring:message code="meal.description"/></th>
                    <th><spring:message code="meal.calories"/></th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <c:forEach items="${meals}" var="meal">
                    <jsp:useBean id="meal" scope="page" type="ru.javawebinar.topjava.to.MealTo"/>
                    <tr data-mealExcess="${meal.excess}" id="${meal.id}">
                        <td>
                                <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                                <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                                <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                                ${fn:formatDateTime(meal.dateTime)}
                        </td>
                        <td>${meal.description}</td>
                        <td>${meal.calories}</td>
                        <td><a><span class="fa fa-pencil"></span></a></td>
                        <td><a class="delete"><span class="fa fa-remove"></span></a></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>


</section>

<div class="modal fade" tabindex="-1" id="editRow">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title"><spring:message code="meal.add"/></h4>
                <button type="button" class="close" data-dismiss="modal" onclick="closeNoty()">&times;</button>
            </div>
            <div class="modal-body">
                <form id="detailsForm">
                    <input type="hidden" id="id" name="id">
                    <div class="form-group">
                        <label for="dateTime" class="col-form-label"><spring:message code="meal.dateTime"/>:</label>
                        <input type="datetime-local" class="form-control" id="dateTime" name="dateTime" required>
                    </div>
                    <div class="form-group">
                        <label for="description" class="col-form-label"><spring:message
                                code="meal.description"/>:</label>
                        <input type="text" class="form-control" id="description" name="description" required  placeholder="Описание">
                    </div>
                    <div class="form-group">
                        <label for="calories" class="col-form-label"><spring:message code="meal.calories"/>:</label>
                        <input type="number" class="form-control" id="calories" name="calories" required placeholder="1000">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="closeNoty()">
                    <span class="fa fa-close"></span>
                    <spring:message code="common.cancel"/>
                </button>
                <button type="button" class="btn btn-primary" onclick="save()">
                    <span class="fa fa-check"></span>
                    <spring:message code="common.save"/>
                </button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
</body>
</html>