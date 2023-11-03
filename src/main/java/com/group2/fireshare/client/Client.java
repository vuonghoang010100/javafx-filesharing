package com.group2.fireshare.client;

import com.group2.fireshare.client.service.SendfileServerHandler;
import com.group2.fireshare.client.service.SocketHandler;
import com.group2.fireshare.server.service.ServerHandler;
import com.group2.fireshare.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Client extends Application {
    private static Client instance;
    private Stage stage;
    private File repoDir;
    private DataOutputStream dos;
    int listenPort;

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

    public void login(Socket socket, DataInputStream dis, DataOutputStream dos, File repoDir, int listenPort, ServerSocket serverSocket) {
        try {
            setRepoDir(repoDir);
            setDos(dos);
            setListenPort(listenPort);

            // Start send file server
            Thread sendfileServerHandler = new Thread(new SendfileServerHandler(serverSocket));
            sendfileServerHandler.setDaemon(true);
            sendfileServerHandler.start();

            // Start Socket worker
            Thread socketHandler = new Thread(new SocketHandler(socket, dis, dos));
            socketHandler.setDaemon(true);
            socketHandler.start();

            // Register Sharefile port listener (Other client connect through this port)
            sendListenPortPacket(listenPort);

            // Switch to home View
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("fxml/home.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handler send packet to server
    public void sendPublishPacket(String pname) throws IOException {
        sendRequestPacket("PUBLISH", pname);
    }

    public void sendListenPortPacket(int port) throws  IOException {
        sendRequestPacket("LISTEN_PORT", String.valueOf(port));
    }

    public void sendFetchPacket(String filename) throws  IOException {
        sendRequestPacket("FETCH", filename);
    }

    public void sendRequestPacket(String method, String data) throws IOException {
        String requestPacket = "CSFS " + method + " \"" + data + "\"";
        getDos().writeUTF(requestPacket);

        writeOutputLog(requestPacket);
    }

    public void writeOutputLog(String content) {
        String log = "[" + LocalDateTime.now().format(Utils.getTimeFormatter())
                + "] Send request to server: " + content;
        System.out.println(log);
    }


    public Client() {
        instance = this;
    }

    public Stage getStage() {
        return stage;
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

    public File getRepoDir() {
        return repoDir;
    }

    public void setRepoDir(File repoDir) {
        this.repoDir = repoDir;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }
}