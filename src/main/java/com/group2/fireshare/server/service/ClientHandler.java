package com.group2.fireshare.server.service;


import com.group2.fireshare.server.controller.HomeController;
import com.group2.fireshare.server.model.*;
import com.group2.fireshare.utils.Utils;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
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
            if (method.equalsIgnoreCase("listen_port")) {
                processListenPortPacket(data);
            } else if (method.equalsIgnoreCase("publish")) {
                processPublishPacket(data);
            } else if (method.equalsIgnoreCase("fetch")) {
                processFetchPacket(data);
            }  else {
                responseBadRequest();
            }
            return;
        }


        // Process response packet
        // We have 4 types of response packet that server is able to receive for now.
        // Those are "204 CONTAIN" "205 EMPTY" "200 PING_OK" "400 BAD_REQUEST"

        // Format "204 CONTAIN": CSFS 204 CONTAIN "${hostname}||${lname}--${pname}||${duration}"
        // ${lname}--${pname}, this part can be repeated multiple times.

        // Format "205 EMPTY": CSFS 205 EMPTY "${hostname}||${durationTime}"
        // Format "200 PING_OK": CSFS 200 PING_OK "${hostname}||${durationTime}"
        // Format "400 BAD_REQUEST": CSFS 400 BAD_REQUEST ""

        Pattern responsePattern = Pattern.compile("^[Cc][Ss][Ff][Ss]\\s+(\\d+)\\s+([a-zA-Z_]+)(?:\\s+\\\"(.+)\\\")?$");
        Matcher responseMatcher = responsePattern.matcher(csfsPacket);
        if (responseMatcher.matches()) {
            String statusCode = responseMatcher.group(1).toLowerCase();
            String statusMessage = responseMatcher.group(2);
            String responseData = responseMatcher.group(3);

            // We need to care about the status code, status message is not too important for now.
            if (statusCode.equals("204")) {
                processStatusCode204(responseData);
            } else if (statusCode.equals("205")) {
                processStatusCode205(responseData);
            } else if (statusCode.equals("200")) {
                processStatusCode200(responseData);
            } else if (statusCode.equals("400")) {
                processStatusCode400();
            }
            return;

        }

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

    public void processStatusCode204(String responseData) {
        boolean isConsoleViewVisible = Settings.getInstance().isConsoleViewVisible();

        // E.g. CSFS 204 CONTAIN "LAPTOP-42KF98B0||config.txt--C:\\User\\Admin\\config.txt||40"
        // We parse the text to get the hostname, the lists of local file and the duration time in the last.

        String[] parts = responseData.split("\\|\\|");
        String hostName = parts[0];
        String timeReply = parts[parts.length - 1];

        // Count the number of files, divide two because
        // the first is the hostname and the last are the duration time.
        int filesCount = parts.length - 2;
        String fileOrFiles = (filesCount == 1) ? "file" : "files";

        // Users is working on ConsoleView so we add the text to it.
        if (isConsoleViewVisible) {
            ServerConsole.getInstance().addText(hostName + " contains " + filesCount + " local " + fileOrFiles +"! Reply in " + timeReply + " ms.");
            for (int i = 1 ; i < parts.length - 1 ; i++) {
                String part = parts[i];
                String[] arr = part.split("--");
                String lname = arr[0];
                String pname = arr[1];
                ServerConsole.getInstance().addText(i + ") " + "Fname: " + pname + "  |  Lname: " + lname);
            }
            ServerConsole.getInstance().addText("-------------------------");
            return;
        }

        Platform.runLater(() -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(hostName + " contains " + filesCount + " local " + fileOrFiles +"! Reply in " + timeReply + " ms.\n");
            stringBuilder.append("--------------------\n");

            for (int i = 1 ; i < parts.length - 1 ; i++) {
                String part = parts[i];
                String[] arr = part.split("--");
                String lname = arr[0];
                String pname = arr[1];
                stringBuilder.append( "Fname: " + pname + "\nLname: " + lname+ "\n");

                if (i < parts.length - 2) {
                    stringBuilder.append("--------------------\n");
                }
            }

            Utils.showAlert(Alert.AlertType.INFORMATION , "DISCOVER " + hostName ,  stringBuilder.toString());
        });

    }

    public void processStatusCode205(String responseData) {
        boolean isConsoleViewVisible = Settings.getInstance().isConsoleViewVisible();

        // E.g. CSFS 205 EMPTY "LATOP--42KF98B0||40"
        // We parse the text to get the hostname and the duration time.

        String[] parts = responseData.split("\\|\\|");
        String hostName = parts[0];
        String timeReply = parts[1];

        // Users is working on ConsoleView so we add the text to it.
        if (isConsoleViewVisible) {
            ServerConsole.getInstance().addText(hostName +" contains 0 local file! Reply in " + timeReply +" ms.");
            ServerConsole.getInstance().addText("-------------------------");
            return;
        }

        Platform.runLater(() -> {
            Utils.showAlert(Alert.AlertType.INFORMATION , "DISCOVER " + hostName ,  hostName +" contains 0 local file! Reply in " + timeReply +" ms.");
        });

    }
    public void processStatusCode200(String responseData) throws IOException {
        boolean isConsoleViewVisible = Settings.getInstance().isConsoleViewVisible();

        // E.g. CSFS 205 PING_OK "LATOP--42KF98B0||40"
        // We parse the text to get the hostname and the duration time.

        String[] parts = responseData.split("\\|\\|");
        String hostName = parts[0];
        String timeReply = parts[1];

        // Users is working on ConsoleView so we add the text to it.
        if (isConsoleViewVisible) {
            ServerConsole.getInstance().addText(hostName +" is connecting with the server! Reply in " + timeReply +" ms.");
            ServerConsole.getInstance().addText("-------------------------");
            return;
        }

        Platform.runLater(() -> {
            Utils.showAlert(Alert.AlertType.INFORMATION , "Ping " + hostName ,  hostName +" is connecting with server! Reply in " + timeReply +" ms.");
        });
    }

    public void processStatusCode400() {
        // This response packet doesn't contain body
        boolean isConsoleViewVisible = Settings.getInstance().isConsoleViewVisible();

        // Users is working on ConsoleView so we add the text to it.
        if (isConsoleViewVisible) {
            ServerConsole.getInstance().addText("Bad request, please check the format.\n");
            return;
        }

        Platform.runLater(() -> {
            Utils.showAlert(Alert.AlertType.ERROR , "Bad Request"  ,  "Bad request, please check the format.");
        });
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
