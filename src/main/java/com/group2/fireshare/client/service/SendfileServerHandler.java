package com.group2.fireshare.client.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SendfileServerHandler implements Runnable{
    private final ServerSocket serverSocket;

    public SendfileServerHandler(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;

    }
    @Override
    public void run() {
        while (true) {
            try {
                // Wait for a client
                Socket clientSocket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                // Create new thread for current client
                new Thread(new SendfileClientHandler(clientSocket, dis, dos)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
