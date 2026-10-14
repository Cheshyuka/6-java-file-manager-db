package com.example.filemanager.account;

import com.example.filemanager.dbService.DBException;
import com.example.filemanager.dbService.DBService;
import com.example.filemanager.dbService.dataSets.UsersDataSet;
import com.example.filemanager.model.UserProfile;

public class AccountService {
    private final DBService dbService;

    public AccountService(DBService dbService) {
        this.dbService = dbService;
    }

    public void addNewUser(UserProfile userProfile) {
        try {
            dbService.addUser(
                    userProfile.getLogin(),
                    userProfile.getPassword(),
                    userProfile.getEmail());
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public UserProfile getUserByLogin(String login) {
        try {
            UsersDataSet dataSet = dbService.getUserByLogin(login);
            if (dataSet == null) return null;
            return new UserProfile(
                    dataSet.getLogin(),
                    dataSet.getPassword(),
                    dataSet.getEmail()
            );
        } catch (DBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        dbService.close();
    }
}