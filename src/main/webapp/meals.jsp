<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib uri="http://example.com/functions" prefix="f" %>

<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="${pageContext.request.contextPath}/index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<a href="${pageContext.request.contextPath}/meals/create">Add Meal</a>
<style type="text/css">
    TABLE {
        width: 60%;
        border-collapse: collapse;
        margin-top: 20px;
    }

    TD, TH {
        padding: 3px;
        border: 1px solid black;
    }

    TH {
        background: #b0e0e6;
    }
</style>

<table class="table">
    <thead class="table-head">
    <tr>
        <th scope="col">Date</th>
        <th scope="col">Description</th>
        <th scope="col">Calories</th>
        <th scope="col"></th>
        <th scope="col"></th>
    </thead>
    <tbody id="mainTable">

    <jsp:useBean id="meals" scope="request" type="java.util.List<ru.javawebinar.topjava.model.MealTo>"/>
    <c:forEach var="meal" items="#{meals}">
        <tr style="color: ${meal.excess ? "red" : "darkgreen"}">
            <td>${f:formatLocalDateTime(meal.dateTime) }</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="${pageContext.request.contextPath}/meals/edit?id=${meal.id}">edit</a></td>
            <td><a href="${pageContext.request.contextPath}/meals/delete?id=${meal.id}">delete</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>