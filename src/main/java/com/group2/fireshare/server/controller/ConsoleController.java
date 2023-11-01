package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.model.ConsoleLog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ConsoleController implements Initializable {
    @FXML
    private TextArea consoleText;
    @FXML
    private TextField command;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        consoleText.textProperty().bind(ConsoleLog.getInstance().textProperty());
    }

    @FXML
    protected void sendCommand() {

    }
}
