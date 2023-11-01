package com.group2.fireshare.server.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserList {
    private static UserList instance;
    private ObservableList<User> userList = FXCollections.observableArrayList();

    private UserList(){}

    public static UserList getInstance() {
        if (instance == null){
            synchronized (UserList.class){
                if (instance == null){
                    instance = new UserList();
//                    instance.setUserList(FXCollections.observableArrayList());
                }
            }
        }
        return instance;
    }

    public synchronized ObservableList<User> getUserList() {
        return userList;
    }

    public synchronized void addUser(User user) {
        userList.add(user);
    }

    public synchronized void removeUser(String ip, int port) {
        if (userList.isEmpty())
            return;
        for (User user: userList) {
            if (user.getIp().equals(ip) && user.getPort() == port) {
                userList.remove(user);
                return;
            }
        }
    }
}
