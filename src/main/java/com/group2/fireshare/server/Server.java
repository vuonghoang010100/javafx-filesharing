package com.group2.fireshare.server;

import com.group2.fireshare.server.service.ServerHandler;
import com.group2.fireshare.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

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

    public void startServer(int port) {
        try {
            // Start Socket server
            Thread serverHandler = new Thread(new ServerHandler(port));
            serverHandler.setDaemon(true);
            serverHandler.start();

            // Switch to home View
            FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("fxml/server-home.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeOutputLog(String content) {
        String log = "[" + LocalDateTime.now().format(Utils.getTimeFormatter())
                + "] Send request to server: " + content;
        System.out.println(log);
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
