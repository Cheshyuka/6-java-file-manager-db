package com.example.filemanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/files")
public class FileListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getParameter("path");

        if (path == null || path.isEmpty()) {
            path = "C:\\";
        }

        File currentDir = new File(path);

        if (!currentDir.exists() || !currentDir.isDirectory()) {
            request.setAttribute("error", "Директория не найдена: " + path);
            request.getRequestDispatcher("/fileList.jsp").forward(request, response);
            return;
        }

        File[] files = currentDir.listFiles();
        List<FileInfo> fileList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                FileInfo info = new FileInfo();
                info.name = file.getName();
                info.path = file.getAbsolutePath();
                info.isDirectory = file.isDirectory();
                info.size = file.isDirectory() ? -1 : file.length();
                info.lastModified = file.lastModified();
                fileList.add(info);
            }
        }

        String parentPath = currentDir.getParent();
        if (parentPath != null) {
            request.setAttribute("parentPath", parentPath);
        }

        request.setAttribute("currentPath", path);
        request.setAttribute("files", fileList);
        request.setAttribute("generatedAt", new Date());

        request.getRequestDispatcher("/fileList.jsp").forward(request, response);
    }

    public static class FileInfo {
        public String name;
        public String path;
        public boolean isDirectory;
        public long size;
        public long lastModified;
    }
}
