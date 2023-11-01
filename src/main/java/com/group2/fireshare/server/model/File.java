package com.group2.fireshare.server.model;

public class File {
    String fileName;
    String hostname;
    int port;

    public File(String fileName, String hostname, int port) {
        this.fileName = fileName;
        this.hostname = hostname;
        this.port = port;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
