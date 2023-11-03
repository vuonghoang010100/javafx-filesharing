package com.group2.fireshare.client.controller;

import com.group2.fireshare.client.Client;
import com.group2.fireshare.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    private TextField listenPort;
    @FXML
    private TextField repoFolder;

    @FXML
    protected void onChooseFolderButtonClick() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose repository location");
        File selectedDirectory = chooser.showDialog(Client.getInstance().getStage());
        // set text for repoFolder
        repoFolder.setText(selectedDirectory.getPath());
    }

    @FXML
    protected void onConnectButtonClick() {
        // Verify ip field
        String ipAddress = ip.getText();
        if (ipAddress.isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "IP Error", "Please input a Server IP!");
            return;
        }
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            if (!inetAddress.isReachable(1000)) {
                throw new IOException();
            }
        } catch (UnknownHostException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "IP Error", "Please input a valid IP");
            return;
        } catch (IOException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "IP Error", "Host is not reachable!");
            return;
        }

        // Verify port field
        if (!Utils.isValidPortNumber(port.getText())) {
            return;
        }
        int portNo = Integer.parseInt(port.getText());

        // Verify listenPort field
        if (!Utils.isValidPortNumber(port.getText())) {
            return;
        }
        int listenPortNo = Integer.parseInt(listenPort.getText());

        // verify repoFolder, Is it a a exist folder path
        File repoDir = new File(repoFolder.getText());
        if (!repoDir.isDirectory()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Repository Folder Path Error!", "Please choose a valid folder path!");
            return;
        }

        // Start SocketServer
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(listenPortNo);
        } catch (IOException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Listen Port Error!", "Port " + listenPortNo +" in use!");
            return;
        }


        // Connect to server
        Socket socket;
        DataInputStream dis;
        DataOutputStream dos;
        try {
            socket = new Socket(ipAddress, portNo);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            // throw new RuntimeException(e);
            Utils.showAlert(Alert.AlertType.ERROR, "Connect Error!", "Unable to connect to the server!");
            return;
        }

        Client.getInstance().login(socket, dis, dos, repoDir, listenPortNo, serverSocket);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Test in my case
        ip.setText("192.168.1.211");
        port.setText("8080");
        listenPort.setText("5000");
        repoFolder.setText("D:\\a_repo");
    }
}
