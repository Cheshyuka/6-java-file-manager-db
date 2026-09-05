<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
    <style>
        a {text-decoration: none; color: #0000FF;}
        a:visited {color: #0000FF;}
        a:hover {color: #FF0000;}
    </style>
</head>
<body>
<c:if test="${not empty error}">
    <div style="color: red;">${error}</div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/register">
    <label>Логин:</label>
    <input type="text" name="login" required>

    <label>Пароль:</label>
    <input type="password" name="password" required>

    <label>Email:</label>
    <input type="email" name="email" required>

    <button type="submit">Зарегистрироваться</button>
</form>

<p>Уже есть аккаунт? <a href="${pageContext.request.contextPath}/login">Войти</a></p>
</body>
</html>