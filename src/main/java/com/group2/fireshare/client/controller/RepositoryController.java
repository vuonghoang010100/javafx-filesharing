package com.group2.fireshare.client.controller;

import com.group2.fireshare.client.Client;
import com.group2.fireshare.client.model.FileItem;
import com.group2.fireshare.client.model.Repository;
import com.group2.fireshare.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RepositoryController implements Initializable {
    @FXML
    private TableView<FileItem> repoTable;
    @FXML
    private TableColumn<FileItem, String> pnameCol;
    @FXML
    private TableColumn<FileItem, String> lnameCol;
    @FXML
    private TextField lnameTextField;
    @FXML
    private TextField pnameTextField;

    @FXML
    protected void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file!");
        File selectedFile = fileChooser.showOpenDialog(Client.getInstance().getStage());
        if (selectedFile == null)
            return;

        lnameTextField.setText(selectedFile.getPath());
        if (pnameTextField.getText().isEmpty()) {
            pnameTextField.setText(selectedFile.getName());
        }
    }

    @FXML
    protected void publishFile() {
        repoTable.refresh();

        // Verify local name
        File file = new File(lnameTextField.getText());
        if (!file.isFile()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Local name Error!", "Please choose a valid file!");
            return;
        }
        // Verify pname
        if (pnameTextField.getText().isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Publish name Error!", "Please input publish name!");
            return;
        }

        // Check pname is valid
        String pname = pnameTextField.getText();
        if (!Utils.isValidFileName(pname)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Publish name Error!", "Publish name is not valid!");
            return;
        }

        // Check pname is not exist
        if (Repository.getInstance().hasFile(pname)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Publish name Error!", "Duplicated publish name!");
            return;
        }

        // Add File to local repositopy
        Repository.getInstance().addFile(new FileItem(file.getPath(), pname));

        // Reset the text fields
        pnameTextField.setText("");
        lnameTextField.setText("");

        // Send publish packet
        try {
            Client.getInstance().sendPublishPacket(pname);
            // Utils.showAlert(Alert.AlertType.INFORMATION, "Publish Successfully!", "Publish packet was sent to server!");
        } catch (IOException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Publish Error!", "Failed to send publish packet to server!");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind data
        pnameCol.setCellValueFactory(new PropertyValueFactory<>("pname"));
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lname"));
        // Set data to table view
        repoTable.setItems(Repository.getInstance().getFileList());
    }
}
