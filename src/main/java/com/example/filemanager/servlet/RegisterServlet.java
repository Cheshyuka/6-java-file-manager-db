package com.example.filemanager.servlet;

import com.example.filemanager.model.UserProfile;
import com.example.filemanager.dbService.DBService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegisterServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserProfile user = getCurrentUser(req, resp);
        if (user != null) {
            resp.sendRedirect(req.getContextPath() + "/fileList");
            return;
        }
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        String error = validateInput(login, password, email);
        if (error != null) {
            showError(error, req, resp);
            return;
        }

        login = login.toLowerCase();
        DBService dbService = getDBService();

        try {
            if (dbService.getUserByLogin(login) != null) {
                showError("Такой пользователь уже зарегистрирован", req, resp);
                return;
            }

            createUserFolder(login);
            dbService.addUser(login, password, email);

            UserProfile user = new UserProfile(login, password, email);
            req.getSession().setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/fileList");

        } catch (Exception e) {
            showError("Ошибка регистрации", req, resp);
        }
    }

    private String validateInput(String login, String password, String email) {
        if (login == null || password == null || email == null ||
                login.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return "Все поля обязательны";
        }
        if (!email.contains("@") || !email.contains(".")) {
            return "Введите корректный email";
        }
        return null;
    }

    private void showError(String message, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    private void createUserFolder(String login) throws IOException {
        Path userFolder = Paths.get(getBasePath(), login);
        if (!Files.exists(userFolder)) {
            Files.createDirectories(userFolder);
        }
    }
}
