package com.group2.fireshare.server.controller;


import com.group2.fireshare.server.model.ConfigurationReader;
import com.group2.fireshare.server.model.ConfigurationSaver;
import com.group2.fireshare.server.model.Constants;
import com.group2.fireshare.server.model.Settings;
import com.group2.fireshare.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import com.group2.fireshare.server.Server;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.ResourceBundle;


public class StartServerController implements Initializable {

    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    protected void onStartButtonClick() {
        String serverIP = ip.getText();
        String serverPort= port.getText();

        // Verify port
        if (!Utils.isValidPortNumber(serverPort)) {
            return;
        }

        // Set the IP in IPManager
        Settings.getInstance().setServerIP(ip.getText());
        Settings.getInstance().setServerPort(Integer.parseInt(serverPort));

        // Save them to the configuration file
        Properties properties = new Properties();
        properties.setProperty(Constants.SERVER_PORT , serverPort);
        ConfigurationSaver.saveConfigurationToUserData(properties);

        // Start server
        Server.getInstance().startServer(Integer.parseInt(serverPort));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Properties properties = ConfigurationReader.readConfigurationFromUserData();
        String serverPort = properties.getProperty(Constants.SERVER_PORT , "8080");

        try {
            String defaultIP =  InetAddress.getLocalHost().getHostAddress();
            ip.setText(defaultIP);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        port.setText(serverPort);
    }
}
