package com.group2.fireshare.server.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import com.group2.fireshare.server.Server;

import java.net.URL;
import java.util.ResourceBundle;


public class StartServerController implements Initializable {

    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    protected void onStartButtonClick() {
        // TODO 1. verify port is number (0->65000 <- change this range to right range)

        Server.getInstance().startServer(port.getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO 1. get local IP and set to ip TextField

        // TODO 2. Set default port
        port.setText("8080");
    }
}
