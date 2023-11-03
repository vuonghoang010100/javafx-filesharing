package com.group2.fireshare.client.model;

public class FetchItem {
    String name;
    String status;
    boolean isCreatedByConsole = false;

    public FetchItem(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public FetchItem(String name, String status, boolean isCreatedByConsole) {
        this.name = name;
        this.status = status;
        this.isCreatedByConsole = isCreatedByConsole;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCreatedByConsole() {
        return isCreatedByConsole;
    }
}
