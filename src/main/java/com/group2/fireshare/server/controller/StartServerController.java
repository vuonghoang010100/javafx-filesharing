package com.group2.fireshare.server.controller;


import com.group2.fireshare.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import com.group2.fireshare.server.Server;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;


public class StartServerController implements Initializable {

    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    protected void onStartButtonClick() {
        // TODO 1. verify port is number (0->65000 <- change this range to right range)
        // Prefer move it to Utils class and reuse Client app
        // example: Check port not empty
        if (port.getText().isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Port Error!", "Please input port number");
            return;
        }
        // TODO Check port is number

        // TODO Check port is in range Registered ports: 1024 to 49151

        Server.getInstance().startServer(Integer.parseInt(port.getText()));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get local IP and set to ip TextField
        try {
            ip.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        // Set default port
        port.setText("8080");
    }
}
