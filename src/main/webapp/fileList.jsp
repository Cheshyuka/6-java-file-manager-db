<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="com.example.filemanager.model.UserProfile" %>
<!DOCTYPE html>
<html>
<head>
    <title>Файловый менеджер</title>
    <meta charset="UTF-8">
</head>
<body>

    <div style="float: right;">
        <%
            UserProfile user = (UserProfile) session.getAttribute("user");
        %>
        Пользователь: <strong><%= user.getLogin() %></strong>
        <a href="<%= request.getContextPath() %>/logout">Выйти</a>
    </div>

    <h2>Текущая директория: <%= request.getAttribute("userHome") %></h2>
    <p>Страница сгенерирована: <%= new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(request.getAttribute("generationTime")) %></p>

    <hr>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <% if (request.getAttribute("parentPath") != null) { %>
        <a href="<%= request.getContextPath() %>/fileList?path=<%= URLEncoder.encode((String)request.getAttribute("parentPath"), StandardCharsets.UTF_8) %>">.. (вверх)</a>
        <br><br>
    <% } %>

    <table border="1" cellpadding="5">
        <tr>
            <th>Имя</th>
            <th>Тип</th>
            <th>Размер</th>
            <th>Дата изменения</th>
        </tr>
        <%
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.getAttribute("items");
            if (items != null && !items.isEmpty()) {
                for (Map<String, Object> item : items) {
                    String name = (String) item.get("name");
                    Boolean isDirectory = (Boolean) item.get("isDirectory");
                    Long size = (Long) item.get("size");
                    String lastModified = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
                            .format((java.util.Date) item.get("lastModified"));
                    String currentPath = (String) request.getAttribute("currentPath");

                    String fullPath = currentPath.isEmpty() ? name : currentPath + "/" + name;

                    String href;
                    if (isDirectory) {
                        href = request.getContextPath() + "/fileList?path="
                                + URLEncoder.encode(fullPath, StandardCharsets.UTF_8);
                    } else {
                        href = request.getContextPath() + "/download?path="
                                + URLEncoder.encode(fullPath, StandardCharsets.UTF_8);
                    }
        %>
            <tr>
                <td><a href="<%= href %>"><%= name %></a></td>
                <td><%= isDirectory ? "Папка" : "Файл" %></td>
                <td><%= isDirectory ? "—" : String.format("%,d bytes", size) %></td>
                <td><%= lastModified %></td>
            </tr>
        <%
                }
            } else {
        %>
            <tr><td colspan="4">Директория пуста</td></tr>
        <%
            }
        %>
    </table>

</body>
</html>