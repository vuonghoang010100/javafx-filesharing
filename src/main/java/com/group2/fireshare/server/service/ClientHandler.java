package com.group2.fireshare.server.service;

import com.group2.fireshare.server.model.*;
import com.group2.fireshare.utils.Utils;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    DataInputStream dis;
    DataOutputStream dos;

    public ClientHandler(Socket clientSocket, DataInputStream dis, DataOutputStream dos) {
        this.clientSocket = clientSocket;
        this.dis = dis;
        this.dos = dos;
        // add user: hostname:port
        addUser(new User(
                clientSocket.getInetAddress().getHostName(),
                clientSocket.getInetAddress().getHostAddress(),
                clientSocket.getPort(),
                dos
        ));
    }

    @Override
    public void run() {
        // Process client socket
        String csfsPacket;
        while (!clientSocket.isClosed()) {
            try {
                // wait for input packet
                csfsPacket = dis.readUTF();
                // log
                writeLogOnInput(csfsPacket);
                // process packet
                processCSFSPacket(csfsPacket.trim());
            }
            catch (IOException e) {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        // On close client socket
        removeUser(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
    }

    public void addUser(User user) {
        writeLogOnInput("Connected!");
        Platform.runLater(() -> {
            // Run on other thread to avoid concurrency error
            UserList.getInstance().addUser(user);
        });
    }

    public void removeUser(String ip, int port) {
        writeLogOnInput("Close connection!");
        Platform.runLater(() -> {
            // Run on other thread to avoid concurrency error
            UserList.getInstance().removeUser(ip, port);
            Repository.getInstance().removeFileByHostname(clientSocket.getInetAddress().getHostName(), clientSocket.getPort());
        });
    }

    public void processCSFSPacket(String csfsPacket) throws IOException {
        // Process request packet
        // Ex:
        //      CSFS LISTEN_PORT "5000"
        //      CSFS PUBLISH "a.txt"
        //      CSFS FETCH "a.pdf"
        Pattern requestPattern = Pattern.compile("^[Cc][Ss][Ff][Ss]\\s+([a-zA-Z_]+)\\s+\\\"(.+)\\\"$");
        Matcher matcherRequest = requestPattern.matcher(csfsPacket);
        if (matcherRequest.matches()) {
            // This csfsPacket is a request
            String method = matcherRequest.group(1).toLowerCase();
            String data = matcherRequest.group(2);
            if (method.equals("listen_port")) {
                processListenPortPacket(data);
            } else if (method.equals("publish")) {
                processPublishPacket(data);
            } else if (method.equals("fetch")) {
                processFetchPacket(data);
            } else {
                responseBadRequest();
            }
            return;
        }

        // TODO process response packet

        responseBadRequest();
    }

    public void processListenPortPacket(String data) throws IOException {
        try {
            // Add listerPort to User data
            int listenPort = Integer.parseInt(data);
            UserList.getInstance().addListenPort(
                    clientSocket.getInetAddress().getHostAddress(),
                    clientSocket.getPort(),
                    listenPort
                    );
            sendResponsePacket(206, "INFORMED_LISTEN_PORT", data);

        }
        catch (IllegalArgumentException e) {
            responseBadRequest();
        }
    }

    public void processPublishPacket(String filename) throws IOException {
        if (!Utils.isValidFileName(filename)) {
            responseBadRequest();
            return;
        }
        // Add file to Server Repository
        Repository.getInstance().addFile(new FileItem(
                filename,
                clientSocket.getInetAddress().getHostName(),
                clientSocket.getInetAddress().getHostAddress(),
                clientSocket.getPort()
        ));
        sendResponsePacket(201, "PUBLISH_OK", filename);
    }
    
    public void processFetchPacket(String filename) throws IOException {
        if (!Utils.isValidFileName(filename)) {
            responseBadRequest();
            return;
        }
        // TODO improve in case file(filename) in at least 2 host -> ping host and return the first response

        // Code below, for simple, return first file in server repository
        // Lookup file in Server repository
        FileItem file = Repository.getInstance().getFirstFileItemByName(filename);

        if (file == null) {
            // TODO improve by discover all host in UserList, listen response to update repository, then lookup repository again
            sendResponsePacket(203, "FILE_NOT_FOUND", filename);
            return;
        }

        int listenPort = UserList.getInstance().findListenPortOfUser(file.getIp(), file.getPort());
        if (listenPort < 0) {
            // If this code reached, something wrong happened!
            sendResponsePacket(203, "FILE_NOT_FOUND", filename);
        }

        String responseData = filename + ":" + file.getIp() + ":" + listenPort;
        sendResponsePacket(202, "FILE_EXIST", responseData);

    }

    public void responseBadRequest() throws IOException {
        sendResponsePacket(404, "BAD_REQUEST", "");
    }

    public void sendResponsePacket(int code, String status, String data) throws IOException {
        String responsePacket = data.isEmpty() ? "CSFS " + code + " " + status : "CSFS " + code + " " + status + " \"" + data + "\"";
        dos.writeUTF(responsePacket);
        writeLogOnOutput(responsePacket);
    }

    public void writeLogOnInput(String content) {
        String log = "[" + LocalDateTime.now().format(Utils.getTimeFormatter())
                + "] " + "[" + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "]: " + content;
        System.out.println(log);
    }
    public void writeLogOnOutput(String content) {
        String log = "[" + LocalDateTime.now().format(Utils.getTimeFormatter())
                + "] " + "Response to [" + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "]: " + content;
        System.out.println(log);
    }
}
