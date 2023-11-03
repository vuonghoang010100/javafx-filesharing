package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.model.ServerConsole;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

public class ConsoleController implements Initializable {
    @FXML
    private TextArea consoleText;
    @FXML
    private TextField inputCommand;

    @FXML
    protected void sendCommand() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        consoleText.textProperty().bind(ServerConsole.getInstance().textProperty());
        inputCommand.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                sendCommand();
            }
        } );
    }
}
