package com.group2.fireshare.client.controller;

import com.group2.fireshare.client.Client;
import com.group2.fireshare.client.model.*;
import com.group2.fireshare.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConsoleController implements Initializable {
    @FXML
    TextArea consoleText; // -> do not access this

    @FXML
    TextField inputCommand;

    @FXML
    protected void sendCommand() {
        if (inputCommand.getText().isEmpty()) {
            return;
        }

        ClientConsole.getInstance().addText(">" + inputCommand.getText());

        // Ex
        // publish lname fname
        // fetch fname
        String[] strArr = inputCommand.getText().trim().split(" ");
        inputCommand.setText("");

        if (strArr.length == 3 && strArr[0].toLowerCase().equals("publish")) {
            // publish
            String lname = strArr[1];
            File file = new File(lname);
            if (!file.isFile()) {
                ClientConsole.getInstance().addText("Invalid localname!");
                return;
            }
            String fname = strArr[2];
            if(!Utils.isValidFileName(fname)) {
                ClientConsole.getInstance().addText("Invalid fname!");
                return;
            }
            if (Repository.getInstance().hasFile(fname)) {
                ClientConsole.getInstance().addText("Fname already existed!");
                return;
            }

            Repository.getInstance().addFile(new FileItem(file.getPath(), fname, true));

            try {
                Client.getInstance().sendPublishPacket(fname);
                ClientConsole.getInstance().addText("Publish packet has been sent to server");
            } catch (IOException e) {
                ClientConsole.getInstance().addText("Failed to send publish packet to server");
            }
            return;
        }

        if (strArr.length == 2 && strArr[0].toLowerCase().equals("fetch")) {
            String fname = strArr[1];

            if (!Utils.isValidFileName(fname)) {
                ClientConsole.getInstance().addText("Invalid fname");
                return;
            }

            if (Repository.getInstance().hasFile(fname)) {
                ClientConsole.getInstance().addText("File already exited in local repository");
                return;
            }

            if (FetchList.getInstance().hasFetchItemStartWithStatus(fname,"Fetching")) {
                ClientConsole.getInstance().addText("File is fetching. Cancel new fetch command");
                return;
            }

            if (FetchList.getInstance().hasFetchItemStartWithStatus(fname,"Start")) {
                ClientConsole.getInstance().addText("File is fetching. Cancel new fetch command");
                return;
            }

            if (FetchList.getInstance().hasFetchItemStartWithStatus(fname,"Downloading")) {
                ClientConsole.getInstance().addText("File is downloading. Cancel new fetch command");
                return;
            }

            if (FetchList.getInstance().hasFetchItemStartWithStatus(fname,"Download")) {
                ClientConsole.getInstance().addText("File is already downloaded. Cancel new fetch command");
                return;
            }

            FetchList.getInstance().addFetchItem(new FetchItem(fname, "Fetching", true));

            try {
                Client.getInstance().sendFetchPacket(fname);
                ClientConsole.getInstance().addText("Fetch packet has been sent to server");
            } catch (IOException e) {
                ClientConsole.getInstance().addText("Failed to send fetch packet to server");
            }

            return;
        }

        ClientConsole.getInstance().addText("Invalid command!");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        consoleText.textProperty().bind(ClientConsole.getInstance().textProperty());
        inputCommand.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                sendCommand();
            }
        } );
    }
}
