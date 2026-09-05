package com.example.filemanager.servlet;

import com.example.filemanager.model.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileListServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserProfile user = getCurrentUser(req, resp);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Корневая папка пользователя
        String userRoot = getBasePath() + user.getLogin() + "/";
        Path rootPath = Paths.get(userRoot);

        if (!Files.exists(rootPath)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "User folder not found: " + userRoot);
            return;
        }

        // Относительный путь из параметра ?path=
        String requestedPath = req.getParameter("path");
        if (requestedPath == null || requestedPath.isEmpty()) {
            requestedPath = "";
        }
        requestedPath = URLDecoder.decode(requestedPath, "UTF-8");

        // Полный путь
        Path fullPath = rootPath.resolve(requestedPath);

        if (!Files.exists(fullPath)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Path not found: " + fullPath);
            return;
        }

        // Проверка безопасности
        Path realFullPath = fullPath.toRealPath();
        Path realRootPath = rootPath.toRealPath();

        if (!realFullPath.startsWith(realRootPath)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        // Отображение пути на странице
        String displayPath = "/explorer/" + user.getLogin() + "/";
        if (!requestedPath.isEmpty()) {
            displayPath += requestedPath + "/";
        }

        if (Files.isRegularFile(realFullPath)) {
            resp.sendRedirect(req.getContextPath() + "/download?path=" +
                    URLDecoder.decode(requestedPath, "UTF-8"));
            return;
        }

        if (Files.isDirectory(realFullPath)) {
            try (Stream<Path> dirItems = Files.list(realFullPath)) {
                List<Path> items = dirItems.collect(Collectors.toList());
                Comparator<Path> comparator = createComparators();
                items.sort(comparator);

                List<Map<String, Object>> fileList = new ArrayList<>();

                for (Path item : items) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", item.getFileName().toString());
                    fileInfo.put("isDirectory", Files.isDirectory(item));
                    fileInfo.put("isRegularFile", Files.isRegularFile(item));
                    fileInfo.put("size", Files.isRegularFile(item) ? Files.size(item) : 0L);
                    fileInfo.put("lastModified", new Date(
                            Files.getLastModifiedTime(item).toMillis()));
                    fileList.add(fileInfo);
                }

                String parentPath = computeParentPath(requestedPath);

                req.setAttribute("generationTime", new Date());
                req.setAttribute("currentPath", requestedPath);
                req.setAttribute("parentPath", parentPath);
                req.setAttribute("items", fileList);
                req.setAttribute("userHome", displayPath);

                req.getRequestDispatcher("/fileList.jsp").forward(req, resp);
                return;
            } catch (IOException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Cannot read directory");
                return;
            }
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not a file or directory");
    }

    private String computeParentPath(String displayPath) {
        if (displayPath == null || displayPath.isEmpty()) {
            return null;
        }
        int lastSlash = displayPath.lastIndexOf('/');
        return lastSlash == -1 ? "" : displayPath.substring(0, lastSlash);
    }

    private Comparator<Path> createComparators() {
        Comparator<Path> byType = (a, b) -> {
            boolean aIsDir = Files.isDirectory(a, LinkOption.NOFOLLOW_LINKS);
            boolean bIsDir = Files.isDirectory(b, LinkOption.NOFOLLOW_LINKS);
            if (aIsDir && !bIsDir) return -1;
            if (!aIsDir && bIsDir) return 1;
            return 0;
        };
        Comparator<Path> byName = (a, b) ->
                a.getFileName().toString().compareToIgnoreCase(b.getFileName().toString());
        return byType.thenComparing(byName);
    }
}