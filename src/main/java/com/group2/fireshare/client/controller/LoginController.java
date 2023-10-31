package com.group2.fireshare.client.controller;

import com.group2.fireshare.client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    private TextField repoFolder;

    @FXML
    protected void onChooseFolderButtonClick() {
        // TODO 1. implement choose folder function

        // TODO 2. set text for repoFolder
    }

    @FXML
    protected void onConnectButtonClick() {
        // TODO 1. verify ip field

        // TODO 2. verify port (0->65000 <- change this range to right range)

        // TODO 3. verify repoFolder, Is it a a exist folder path

        // TODO 5. Test connection to server, send all data to home scene and switch to home scene
        Client.getInstance().login(ip.getText(), port.getText(), repoFolder.getText());
    }
}
