package com.group2.fireshare.client.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FetchItem {
    String name;
    StringProperty status;
    boolean isCreatedByConsole = false;

    public FetchItem(String name, String status) {
        this.name = name;
        this.status = new SimpleStringProperty(status);
    }

    public FetchItem(String name, String status, boolean isCreatedByConsole) {
        this.name = name;
        this.status = new SimpleStringProperty(status);
        this.isCreatedByConsole = isCreatedByConsole;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status.getValue();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public boolean isCreatedByConsole() {
        return isCreatedByConsole;
    }
}
