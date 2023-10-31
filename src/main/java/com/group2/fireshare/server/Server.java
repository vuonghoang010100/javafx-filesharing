package com.group2.fireshare.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Server extends Application {
    private static Server instance;
    private Stage stage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("fxml/start-server.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Server App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void startServer(String port) {
        try {
            // TODO 1. preproccess input data

            // TODO 2. Save global data here

            // TODO 3. Start server worker (Socket)

            // Switch to home View
            FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("fxml/server-home.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server() {
        instance = this;
    }

    public static Server getInstance() {
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
