package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.Server;
import com.group2.fireshare.server.model.CommandProcessingException;
import com.group2.fireshare.server.model.User;
import com.group2.fireshare.server.model.UserList;
import com.group2.fireshare.server.service.NetworkService;
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
import java.util.Calendar;
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
            Utils.showAlert(Alert.AlertType.WARNING , "DISCOVER" , "Please select the client you want to discover!");
            return;
        }

        String hostname = selectedUser.getHostname();
        DataOutputStream dos = selectedUser.getDos();

        try {
            NetworkService.getInstance().sendDiscoverPacket(dos, hostname);
        }catch (CommandProcessingException e) {
            Utils.showAlert(Alert.AlertType.ERROR , "DISCOVER " + hostname, "DISCOVER error: " + hostname + " is not connecting with server! So we can't discover its local files!");
            e.printStackTrace();
        }

    }

    @FXML
    protected void pingHost() {
        User selectedUser = clientTable.getSelectionModel().getSelectedItem();

        if(selectedUser == null) {
            Utils.showAlert(Alert.AlertType.WARNING , "PING" , "Please select the client you want to ping!");
            return;
        }

        String hostname = selectedUser.getHostname();
        DataOutputStream dos = selectedUser.getDos();

        try {
            NetworkService.getInstance().sendPingPacket(dos, hostname);
        }catch (CommandProcessingException e) {
            Utils.showAlert(Alert.AlertType.ERROR , "PING " + hostname, hostname + " is not connecting with server!");
            e.printStackTrace();
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
