package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.model.File;
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
    private TableView<File> fileTable;
    @FXML
    private TableColumn<File,String> fileNameCol;
    @FXML
    private TableColumn<File,String> hostnameCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind data
        fileNameCol.setCellValueFactory(new PropertyValueFactory<File,String>("fileName"));
        hostnameCol.setCellValueFactory(new PropertyValueFactory<File,String>("hostname"));
        // Set data to table view
        fileTable.setItems(Repository.getInstance().getFileList());
    }
}
