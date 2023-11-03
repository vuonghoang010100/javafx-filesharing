package com.group2.fireshare.server.model;

import javafx.beans.property.SimpleStringProperty;

public class ServerConsole {
    private static ServerConsole instance;
    private SimpleStringProperty text = new SimpleStringProperty("\r");

    private ServerConsole(){}

    public static ServerConsole getInstance() {
        if (instance == null){
            synchronized (ServerConsole.class){
                if (instance == null){
                    instance = new ServerConsole();
                }
            }
        }
        return instance;
    }

    public SimpleStringProperty textProperty() {
        return text;
    }

    public synchronized String getText() {
        return text.get();
    }

    public synchronized void setText(String text) {
        this.text.set(text);
    }

    public synchronized void addText(String text) {
        this.text.set(this.text.get() + text + "\n");
    }
}
