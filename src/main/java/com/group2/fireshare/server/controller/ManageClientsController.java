package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.Server;
import com.group2.fireshare.server.model.User;
import com.group2.fireshare.server.model.UserList;
import com.group2.fireshare.utils.Utils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ManageClientsController implements Initializable {
    @FXML
    TableView<User> clientTable;
    @FXML
    TableColumn<User, String> hostnameCol;
    @FXML
    TableColumn<User, String> ipCol;
    @FXML
    TableColumn<User, Integer> portCol;

    @FXML
    protected void discoverHost() {
        User selectedUser = clientTable.getSelectionModel().getSelectedItem();

        if(selectedUser == null) {
            return;
        }
        String hostname = selectedUser.getHostname();
        String ip = selectedUser.getIp();
        int port = selectedUser.getPort();
        DataOutputStream dos = selectedUser.getDos();

        try {
            dos.writeUTF("CSFS DISCOVER " + "\""+hostname+"\"");
        }catch (IOException e) {
            e.printStackTrace(); // Handle connection or IO errors here
        }

    }

    @FXML
    protected void pingHost() {
        User selectedUser = clientTable.getSelectionModel().getSelectedItem();

        if(selectedUser == null) {
            return;
        }
        String hostname = selectedUser.getHostname();
        String ip = selectedUser.getIp();
        int port = selectedUser.getPort();
        DataOutputStream dos = selectedUser.getDos();

        try {
            dos.writeUTF("CSFS PING " + "\""+hostname+"\"");
        }catch (IOException e) {
            Utils.showAlert(Alert.AlertType.INFORMATION , "PING " + hostname, hostname + " is not connecting with server!");
            e.printStackTrace(); // Handle connection or IO errors here
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind data
        hostnameCol.setCellValueFactory(new PropertyValueFactory<User,String>("hostname"));
        ipCol.setCellValueFactory(new PropertyValueFactory<User,String>("ip"));
        portCol.setCellValueFactory(new PropertyValueFactory<User,Integer>("port"));
        // Set data to table view
        clientTable.setItems(UserList.getInstance().getUserList());
    }

}
