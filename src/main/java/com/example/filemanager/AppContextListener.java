package com.example.filemanager;

import com.example.filemanager.service.AccountService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AccountService accountService = new AccountService();
        sce.getServletContext().setAttribute("accountService", accountService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
