package com.group2.fireshare.server.model;

public class FileItem {
    String fileName;
    String hostname;
    String ip;
    int port;

    public FileItem(String fileName, String hostname, String ip, int port) {
        this.fileName = fileName;
        this.hostname = hostname;
        this.ip = ip;
        this.port = port;
    }

    public String getFileName() {
        return fileName;
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

}
