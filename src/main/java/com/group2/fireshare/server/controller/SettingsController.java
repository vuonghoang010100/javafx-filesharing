package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.model.ServerConsole;
import com.group2.fireshare.server.model.Settings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private Label hostLabel;

    @FXML
    private Label portLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String serverIP = Settings.getInstance().getServerIP();
        Number serverPort = Settings.getInstance().getServerPort();

        hostLabel.setText("Server is running on host: "  + serverIP);
        portLabel.setText("Server is running on port: " + serverPort.toString());
    }
}
