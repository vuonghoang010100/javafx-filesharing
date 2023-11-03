package com.group2.fireshare.client.controller;

import com.group2.fireshare.client.Client;
import com.group2.fireshare.client.model.FetchItem;
import com.group2.fireshare.client.model.FetchList;
import com.group2.fireshare.client.model.Repository;
import com.group2.fireshare.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FetchController implements Initializable {
    @FXML
    TableView<FetchItem> fetchFileTable;
    @FXML
    TableColumn<FetchItem, String> filenameCol;
    @FXML
    TableColumn<FetchItem, String> statusCol;
    @FXML
    TextField fetchFilenameTF;

    @FXML
    protected void startFetchFile() {
        if (fetchFilenameTF.getText().isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "File name Error!", "Please input file name!");
            return;
        }
        String fetchFilename = fetchFilenameTF.getText();
        if (!Utils.isValidFileName(fetchFilename)) {
            Utils.showAlert(Alert.AlertType.ERROR, "File name Error!", "File name is not valid!");
            return;
        }

        if (Repository.getInstance().hasFile(fetchFilename)) {
            Utils.showAlert(Alert.AlertType.WARNING, "Fetch Warning!", "File already exist in local repository! Stop Fetching!");
            return;
        }

        if (FetchList.getInstance().hasFetchItemWithStatus(fetchFilename,"Fetching")) {
            Utils.showAlert(Alert.AlertType.WARNING, "Fetch Warning!", "File is fetching! Stop new fetching!");
            return;
        }

        if (FetchList.getInstance().hasFetchItemWithStatus(fetchFilename,"Downloading...")) {
            Utils.showAlert(Alert.AlertType.WARNING, "Fetch Warning!", "File is downloading! Stop new fetching!");
            return;
        }

        if (FetchList.getInstance().hasFetchItemWithStatus(fetchFilename,"Download Complete!")) {
            Utils.showAlert(Alert.AlertType.WARNING, "Fetch Warning!", "File already downloaded! Stop new fetching!");
            return;
        }

        try {
            Client.getInstance().sendFetchPacket(fetchFilename);
        } catch (IOException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Fetch Error!", "Failed to send fetch packet to server!");
        }

        FetchList.getInstance().addFetchItem(new FetchItem(fetchFilename, "Fetching"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind data
        filenameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Set data to table view
        fetchFileTable.setItems(FetchList.getInstance().getFetchList());
    }
}
