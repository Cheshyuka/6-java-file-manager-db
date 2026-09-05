package com.example.filemanager.servlet;

import com.example.filemanager.model.UserProfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloadServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        UserProfile user = getCurrentUser(req, resp);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String encodedPath = req.getParameter("path");
        if (encodedPath == null || encodedPath.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Path not specified");
            return;
        }

        String relativePath = URLDecoder.decode(encodedPath, "UTF-8");

        Path rootPath = Paths.get(getBasePath() + user.getLogin());
        Path realRoot = rootPath.toRealPath();

        Path filePath = rootPath.resolve(relativePath).toRealPath();

        if (!filePath.startsWith(realRoot)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        String fileName = filePath.getFileName().toString();
        long fileSize = Files.size(filePath);

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        if (fileSize <= Integer.MAX_VALUE) {
            resp.setContentLength((int) fileSize);
        } else {
            resp.setHeader("Content-Length", String.valueOf(fileSize));
        }

        try (InputStream in = Files.newInputStream(filePath);
             OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}