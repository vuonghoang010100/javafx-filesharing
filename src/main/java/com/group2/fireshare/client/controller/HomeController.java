package com.group2.fireshare.client.controller;

import com.group2.fireshare.client.Client;
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
    private Parent repoView;
    private Parent fetchView;
    private Parent consoleView;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private void switchRepoView(ActionEvent e) {
        mainBorderPane.setCenter(repoView);
    }

    @FXML
    private void switchFetchView(ActionEvent e) {
        mainBorderPane.setCenter(fetchView);
    }

    @FXML
    private void switchConsoleView(ActionEvent e) {
        mainBorderPane.setCenter(consoleView);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader loader1 = new FXMLLoader(Client.getInstance().getClass().getResource("fxml/repository.fxml"));
            repoView = (Parent) (loader1.load());

            FXMLLoader loader2 = new FXMLLoader(Client.getInstance().getClass().getResource("fxml/fetch.fxml"));
            fetchView = (Parent) (loader2.load());

            FXMLLoader loader3 = new FXMLLoader(Client.getInstance().getClass().getResource("fxml/console.fxml"));
            consoleView = (Parent) (loader3.load());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
