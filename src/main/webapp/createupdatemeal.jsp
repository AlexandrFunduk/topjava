<jsp:useBean id="formType" scope="request" type="java.lang.String"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="ru">
<style type="text/css">
    form {
        padding: 10px;
        width: 400px;
        border: 1px solid black
    }

    input {
        width: 250px;
        float: right;
    }
</style>
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="${pageContext.request.contextPath}/index.html">Home</a></h3>
<hr>
<h2>${formType} meal</h2>


<form action="meals" method="post">
    <input name="id" value="${requestScope.meal.id == null ? "" : requestScope.meal.id}"
           style="visibility: hidden; width: 0; height: 0">
    <p><label>DateTime <input type="datetime-local" name="dateTime"
                              value="${requestScope.meal.dateTime == null ? "" : requestScope.meal.dateTime}" required></label>
    </p>
    <p><label>Description <input type="text" name="description"
                                 value="${requestScope.meal.description == null ? "" : requestScope.meal.description}"
                                 required></label></p>
    <p><label>Calories <input type="number" name="calories"
                              value="${requestScope.meal.calories == null ? "" : requestScope.meal.calories}" min="0"
                              max="2147483647" required></label>
    </p>
    <p>
        <button type="submit" name="save">Save</button>
        <button type="button" onclick="window.location.href='meals'" name="cancel">Cancel</button>
    </p>
</form>
</body>
</html>