package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.model.CommandProcessingException;
import com.group2.fireshare.server.model.ServerConsole;
import com.group2.fireshare.server.model.User;
import com.group2.fireshare.server.model.UserList;
import com.group2.fireshare.server.service.ClientHandler;
import com.group2.fireshare.server.service.NetworkService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleController implements Initializable {
    @FXML
    private TextArea consoleText;
    @FXML
    private TextField inputCommand;

    @FXML
    protected void sendCommand() {
        String input = inputCommand.getText();

        // Update comments...

        ServerConsole.getInstance().addText(input);
        inputCommand.setText("");

        try {
            processCommand(input);
        } catch (CommandProcessingException e) {
            ServerConsole.getInstance().addText(e.getMessage());
        }
    }

    private void processCommand(String input) throws CommandProcessingException {
        Pattern requestPattern = Pattern.compile("^[Cc][Ss][Ff][Ss]\\s+([a-zA-Z_]+)\\s+\\\"(.+)\\\"$");
        Matcher matcherRequest = requestPattern.matcher(input);

        if (!matcherRequest.matches()) {
            throw new CommandProcessingException("Command invalid! Please follow these example commands:\nCSFS PING \"LAPTOP-42KF98B\" \nCSFS DISCOVER \"LAPTOP-42KF98B\"");
        }

        String method = matcherRequest.group(1).toLowerCase();
        String data = matcherRequest.group(2);

        if (method.equalsIgnoreCase("ping")) {
            processPingCommand(data);
        } else if (method.equalsIgnoreCase("discover")) {
            processDiscoverCommand(data);
        } else {
            throw new CommandProcessingException("Command invalid! Please follow these example commands:\nCSFS PING \"LAPTOP-42KF98B\" \nCSFS DISCOVER \"LAPTOP-42KF98B\"");
        }
    }

    private void processPingCommand(String hostName) throws CommandProcessingException {
        User user = UserList.getInstance().findUserByHostName(hostName);

        if (user == null) {
            throw new CommandProcessingException("Host " + hostName + " is not connecting with server!");
        }

        DataOutputStream dos = user.getDos();
        try {
            NetworkService.getInstance().sendPingPacket(dos, hostName);
        } catch (CommandProcessingException e) {
            throw new CommandProcessingException(e.getMessage());
        }
    }

    private void processDiscoverCommand(String hostName) throws CommandProcessingException {
        User user = UserList.getInstance().findUserByHostName(hostName);

        if (user == null) {
            throw new CommandProcessingException("DISCOVER error: " + hostName + " is not connecting with server! So we can't discover its local files!");
        }

        DataOutputStream dos = user.getDos();
        try {
            NetworkService.getInstance().sendDiscoverPacket(dos, hostName);
        } catch (CommandProcessingException e) {
            throw new CommandProcessingException(e.getMessage());
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        consoleText.textProperty().bind(ServerConsole.getInstance().textProperty());
        inputCommand.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                sendCommand();
            }
        } );
    }
}
