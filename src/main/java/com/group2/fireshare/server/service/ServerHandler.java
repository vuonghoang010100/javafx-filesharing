package com.group2.fireshare.server.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private final ServerSocket serverSocket;

    public ServerHandler(int port) {
        try {
            serverSocket = new ServerSocket(port);
            //test log
            System.out.println("Create Server at ip " + InetAddress.getLocalHost() + ", port " + port);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Wait for a client
                Socket clientSocket = serverSocket.accept();
                // clientSocket.setKeepAlive(true); // I don't get it
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                // Create new thread for current client
                new Thread(new ClientHandler(clientSocket, dis, dos)).start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
