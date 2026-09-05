package com.example.filemanager.service;

import com.example.filemanager.model.UserProfile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import java.util.HashMap;
import java.util.Map;

public class AccountService {
    private final Map<String, UserProfile> loginToProfile;

    public AccountService() {
        loginToProfile = new HashMap<>();
    }

    public void addNewUser(UserProfile userProfile) {
        loginToProfile.put(userProfile.getLogin(), userProfile);
    }

    public UserProfile getUserByLogin(String login) {
        return loginToProfile.get(login);
    }
}