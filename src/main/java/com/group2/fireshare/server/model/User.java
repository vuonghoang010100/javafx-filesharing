package com.group2.fireshare.server.model;

import java.io.DataOutputStream;

public class User {
    private String hostname;
    private String ip;
    private int port;
    private DataOutputStream dos;
    private int listenPort;

    public User(String hostname, String ip, int port, DataOutputStream dos) {
        this.hostname = hostname;
        this.ip = ip;
        this.port = port;
        this.dos = dos;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }
}
