package com.example.filemanager;

import com.example.filemanager.account.AccountService;
import com.example.filemanager.dbService.DBService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DBService dbService = new DBService();
        dbService.printConnectInfo();

        AccountService accountService = new AccountService(dbService);
        sce.getServletContext().setAttribute("accountService", accountService);
        System.out.println("AccountService initialized with Hibernate");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        AccountService accountService = (AccountService) sce.getServletContext()
                .getAttribute("accountService");
        if (accountService != null) {
            accountService.close();
        }
        System.out.println("AccountService closed");
    }
}
