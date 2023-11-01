package com.group2.fireshare.server.service;

import com.group2.fireshare.server.model.*;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
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
                // process packet
                String log = "[" + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "]: " + csfsPacket;
                System.out.println(log);
                ConsoleLog.getInstance().addText(log);
                processCSFSPacket(csfsPacket);
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
        Platform.runLater(() -> {
            String log = "[" + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "]: Connected!";
            System.out.println(log);
            ConsoleLog.getInstance().addText(log);
            UserList.getInstance().addUser(user);
        });
    }

    public void removeUser(String ip, int port) {
        // removeUser
        Platform.runLater(() -> {
            String log = "[" + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "]: Close Connection!";
            System.out.println(log);
            ConsoleLog.getInstance().addText(log);
            UserList.getInstance().removeUser(ip, port);
        });
        // removeUserFile
        Platform.runLater(() -> {
            Repository.getInstance().removeFileByHostname(clientSocket.getInetAddress().getHostName(), clientSocket.getPort());
        });
    }

    public void processCSFSPacket(String csfsPacket) {
        // TODO: check and process PUBLISH  packet, use dos.writeUTF()

        // TODO: check and process FETCH packet, use dos.writeUTF()

        // TODO: check and process BAD_REQUEST packet, use dos.writeUTF(CSFS 400...)

        // My test for add file and remove file -> regex not optimize
        Pattern fetchResponsePattern = Pattern.compile("^[Cc][Ss][Ff][Ss](?:\\s+)(.+?)(?:\\s)\\\"(.+)\\\"$");
        Matcher matcher = fetchResponsePattern.matcher(csfsPacket.trim());

        if (matcher.matches()) {
            if (matcher.group(1).toLowerCase().equals("publish")) {
                String filename = matcher.group(2);
                Repository.getInstance().addFile(new File(
                        filename,
                        clientSocket.getInetAddress().getHostName(),
                        clientSocket.getPort()
                ));
            }
        }
    }
}
