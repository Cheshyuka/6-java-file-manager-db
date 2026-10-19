package com.example.filemanager.servlet;


import com.example.filemanager.dbService.DBService;
import com.example.filemanager.dbService.dataSets.UsersDataSet;
import com.example.filemanager.model.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserProfile user = getCurrentUser(req, resp);
        if (user != null) {
            resp.sendRedirect(req.getContextPath() + "/fileList");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String login = req.getParameter("login");
        String password = req.getParameter("password");

        if (login == null || password == null || login.isEmpty() || password.isEmpty()) {
            showError("Логин и пароль обязательны", req, resp);
            return;
        }

        login = login.toLowerCase();
        DBService dbService = getDBService();

        try {
            UsersDataSet userData = dbService.getUserByLogin(login);

            if (userData == null || !userData.getPassword().equals(password)) {
                showError("Неверный логин или пароль", req, resp);
                return;
            }

            UserProfile user = new UserProfile(
                    userData.getLogin(),
                    userData.getPassword(),
                    userData.getEmail());

            HttpSession session = req.getSession();
            session.setAttribute("user", user);

            resp.sendRedirect(req.getContextPath() + "/fileList");

        } catch (Exception e) {
            showError("Ошибка сервера", req, resp);
        }
    }

    private void showError(String message, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }
}
