package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.model.FileItem;
import com.group2.fireshare.server.model.Repository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ManageFilesController implements Initializable {
    @FXML
    private TableView<FileItem> fileTable;
    @FXML
    private TableColumn<FileItem,String> fileNameCol;
    @FXML
    private TableColumn<FileItem,String> hostnameCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind data
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        hostnameCol.setCellValueFactory(new PropertyValueFactory<>("hostname"));
        // Set data to table view
        fileTable.setItems(Repository.getInstance().getFileList());
    }
}
