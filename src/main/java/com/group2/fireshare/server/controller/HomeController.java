package com.group2.fireshare.server.controller;

import com.group2.fireshare.server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private Parent clientView;
    private Parent fileView;
    private Parent consoleView;

    private Parent settingsView;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private void switchManageClientsView(ActionEvent e) {
        mainBorderPane.setCenter(clientView);
    }

    @FXML
    private void switchManageFilesView(ActionEvent e) {
        mainBorderPane.setCenter(fileView);
    }

    @FXML
    private void switchConsoleView(ActionEvent e) {
        mainBorderPane.setCenter(consoleView);
    }

    @FXML
    private void switchSettingsView(ActionEvent e) {
        mainBorderPane.setCenter(settingsView);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader loader1 = new FXMLLoader(Server.getInstance().getClass().getResource("fxml/manage-client.fxml"));
            clientView = (Parent) (loader1.load());

            FXMLLoader loader2 = new FXMLLoader(Server.getInstance().getClass().getResource("fxml/manage-file.fxml"));
            fileView = (Parent) (loader2.load());

            FXMLLoader loader3 = new FXMLLoader(Server.getInstance().getClass().getResource("fxml/console.fxml"));
            consoleView = (Parent) (loader3.load());

            FXMLLoader loader4 = new FXMLLoader(Server.getInstance().getClass().getResource("fxml/settings.fxml"));
            settingsView = (Parent) (loader4.load());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
