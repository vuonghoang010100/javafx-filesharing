package com.group2.fireshare.server.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConsoleLog {
    private static ConsoleLog instance;
    private SimpleStringProperty text = new SimpleStringProperty("\r");

    private ConsoleLog(){}

    public static ConsoleLog getInstance() {
        if (instance == null){
            synchronized (ConsoleLog.class){
                if (instance == null){
                    instance = new ConsoleLog();
                }
            }
        }
        return instance;
    }

    public String getText() {
        return text.get();
    }

    public SimpleStringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public void addText(String text) {
        this.text.set(this.text.get() + text + "\n");
    }
}
