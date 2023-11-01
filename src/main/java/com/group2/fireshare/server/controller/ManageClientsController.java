package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.model.User;
import com.group2.fireshare.server.model.UserList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private ObservableList<User> userList;
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
