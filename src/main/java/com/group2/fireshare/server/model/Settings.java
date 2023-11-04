package com.group2.fireshare.server.model;

public class Settings {
    private static Settings instance = null;
    private String serverIP;
    private Number serverPort;

    private String currentView;

    private Settings() {
        // Private constructor to enforce singleton pattern
        this.currentView = "manageClientsView";
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String ip) {
        serverIP = ip;
    }

    public Number getServerPort() {
        return serverPort;
    }

    public void setServerPort(Number port) {
        serverPort = port;
    }

    public String getCurrentView() {
        return currentView;
    }

    public void setCurrentView(String currentView) {
        this.currentView = currentView;
    }

    public boolean isConsoleViewVisible() {
        return this.currentView.equals("consoleView");
    }
}
