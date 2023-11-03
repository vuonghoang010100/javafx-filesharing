package com.group2.fireshare.client.model;

import javafx.beans.property.SimpleStringProperty;

public class ClientConsole {
    private static ClientConsole instance;
    private SimpleStringProperty text = new SimpleStringProperty("\r");

    private ClientConsole(){}

    public static ClientConsole getInstance() {
        if (instance == null){
            synchronized (ClientConsole.class){
                if (instance == null){
                    instance = new ClientConsole();
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
