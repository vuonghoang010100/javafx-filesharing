package com.group2.fireshare.server.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.chart.PieChart;

import java.io.DataOutputStream;

public class User {
    private String hostname;
    private String ip;
    private int port;
    private DataOutputStream dos;

    public User(String hostname, String ip, int port, DataOutputStream dos) {
        this.hostname = hostname;
        this.ip = ip;
        this.port = port;
        this.dos = dos;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }
}
