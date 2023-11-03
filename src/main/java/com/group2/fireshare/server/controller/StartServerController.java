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
        // Verify port
        if (!Utils.isValidPortNumber(port.getText())) {
            return;
        }
        int portNo = Integer.parseInt(port.getText());

        // Start server
        Server.getInstance().startServer(portNo);
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
