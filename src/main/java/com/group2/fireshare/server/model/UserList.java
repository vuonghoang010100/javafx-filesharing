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

    public synchronized void addListenPort(String ip, int port, int listenPort) {
        if (userList.isEmpty())
            return;
        for (User user: userList) {
            if (user.getIp().equals(ip) && user.getPort() == port) {
                user.setListenPort(listenPort);
                return;
            }
        }
    }

    public synchronized int findListenPortOfUser(String ip, int port) {
        if (userList.isEmpty())
            return -1;
        for (User user: userList) {
            if (user.getIp().equals(ip) && user.getPort() == port) {
                return user.getListenPort();
            }
        }
        return -1;
    }

    public User findUserByHostName(String hostName) {
        for (User user : this.userList) {

            System.out.println("huy: " + user.getHostname()  + user.getHostname().equalsIgnoreCase(hostName));

            if (user.getHostname().equalsIgnoreCase(hostName)) {
                return user; // Return the first user with a matching hostName
            }
        }

        return null; // Return null if no matching user is found
    }
}
