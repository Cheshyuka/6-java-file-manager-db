package com.example.filemanager;

import com.example.filemanager.dbService.DBService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DBService dbService = new DBService();
        sce.getServletContext().setAttribute("dbService", dbService);
        System.out.println("DBService initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBService dbService = (DBService) sce.getServletContext().getAttribute("dbService");
        if (dbService != null) {
            dbService.close();
        }
        System.out.println("DBService closed");
    }
}
