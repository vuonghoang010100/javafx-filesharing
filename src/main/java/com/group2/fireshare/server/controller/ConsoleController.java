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


        // Add text to the console and reset the input field
        ServerConsole.getInstance().addText(input);
        inputCommand.setText("");

        try {
            processCommand(input);
        } catch (CommandProcessingException e) {
            ServerConsole.getInstance().addText(e.getMessage());
            ServerConsole.getInstance().addText("-------------------------");
        }
    }

    private void processCommand(String input) throws CommandProcessingException {
        if (!input.trim().startsWith("CSFS ")) {
            input = "CSFS " + input;
        }

        Pattern requestPattern = Pattern.compile("^[Cc][Ss][Ff][Ss]\\s+([a-zA-Z_]+)\\s+\\\"(.+)\\\"$");
        Matcher matcherRequest = requestPattern.matcher(input);

        // The command doesn't match the regex, it means this is a invalid command.
        // Throw the friendly error message to educate the user how to write valid commands.
        if (!matcherRequest.matches()) {
            throw new CommandProcessingException("Command invalid! Please follow these example commands:\nPING \"${client_host_name}\" \nDISCOVER \"${client_host_name}\"");
        }

        // The command is valid so we parse it to get the method and the request data.
        String method = matcherRequest.group(1).toLowerCase();
        String data = matcherRequest.group(2);

        // Process command follow the method
        if (method.equalsIgnoreCase("ping")) {
            processPingCommand(data);
        } else if (method.equalsIgnoreCase("discover")) {
            processDiscoverCommand(data);
        } else {
            // Method is not supported, so we throw the friendly error message to educate the user.
            throw new CommandProcessingException("Your method is not supported! Please follow these example commands:\nPING \"${client_host_name}\" \nDISCOVER \"${client_host_name}\"");
        }
    }

    private void processPingCommand(String hostName) throws CommandProcessingException {
        User user = UserList.getInstance().findUserByHostName(hostName);

        // Can't find user from the host name.
        // Must write a file to save all users that have connected to the server and check from there.
        // Need to update.
        if (user == null) {
            throw new CommandProcessingException("Host " + hostName + " is not connecting with server!");
        }

        // Find the user, send PING packet to her.
        DataOutputStream dos = user.getDos();
        try {
            NetworkService.getInstance().sendPingPacket(dos, hostName);
        } catch (CommandProcessingException e) {
            throw new CommandProcessingException(e.getMessage());
        }
    }

    private void processDiscoverCommand(String hostName) throws CommandProcessingException {
        User user = UserList.getInstance().findUserByHostName(hostName);


        // Can't find user from the host name.
        // Must write a file to save all users that have connected to the server and check from there.
        // Need to update.
        if (user == null) {
            throw new CommandProcessingException("DISCOVER error: " + hostName + " is not connecting with server! So we can't discover its local files!");
        }

        // Find the user, send DISCOVER packet to her.
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
