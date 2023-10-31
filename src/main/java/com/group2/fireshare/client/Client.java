package com.group2.fireshare.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {
    private static Client instance;
    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Client App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void login(String ip, String port, String repoPath) {
        try {
            // TODO 1. preproccess input data

            // TODO 2. Save global data here

            // TODO 3. Connect to server... bla bla create ClientWorker bla bla (change input if need)

            // Switch to home View
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("fxml/home.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client() {
        instance = this;
    }

    public static Client getInstance() {
        // TODO protected this like below (not important)
//        if(!instance) {
//            Thread.start {
//                // Have to run in a thread because launch doesn't return
//                Application.launch(MyClass.class)
//            }
//            while (!instance)
//                Thread.sleep(100);
//        }

        return instance;
    }
}