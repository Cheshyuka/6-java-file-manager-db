<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.filemanager.FileListServlet.FileInfo" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<!DOCTYPE html>
<html>
<head>
    <title>Файловый менеджер</title>
    <meta charset="UTF-8">
</head>
<body>

    <h2>Текущая директория: <%= request.getAttribute("currentPath") %></h2>
    <p>Страница сгенерирована: <%= new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(request.getAttribute("generatedAt")) %></p>

    <hr>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <% if (request.getAttribute("parentPath") != null) { %>
        <a href="files?path=<%= URLEncoder.encode((String)request.getAttribute("parentPath"), StandardCharsets.UTF_8) %>">.. (вверх)</a>
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
            List<FileInfo> files = (List<FileInfo>) request.getAttribute("files");
            if (files != null && !files.isEmpty()) {
                for (FileInfo file : files) {
                    String encodedPath = URLEncoder.encode(file.path, StandardCharsets.UTF_8);
                    String href = file.isDirectory
                        ? "files?path=" + encodedPath
                        : "download?path=" + encodedPath;
        %>
            <tr>
                <td>
                    <a href="<%= href %>"><%= file.name %></a>
                </td>
                <td><%= file.isDirectory ? "Папка" : "Файл" %></td>
                <td><%= file.isDirectory ? "—" : String.format("%,d bytes", file.size) %></td>
                <td><%= new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(file.lastModified) %></td>
            </tr>
        <%
                }
            } else {
        %>
            <tr>
                <td colspan="4">Директория пуста</td>
            </tr>
        <%
            }
        %>
    </table>

</body>
</html>