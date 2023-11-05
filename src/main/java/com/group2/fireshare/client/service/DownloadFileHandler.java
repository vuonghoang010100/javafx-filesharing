package com.group2.fireshare.client.service;


import com.group2.fireshare.client.Client;
import com.group2.fireshare.client.model.ClientConsole;
import com.group2.fireshare.client.model.FetchItem;
import com.group2.fireshare.client.model.FetchList;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadFileHandler implements Runnable{
    private final String filename;
    private final String srcIp;
    private final int srcPort;
    private FetchItem fetchItem;
    private boolean useConsole;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public DownloadFileHandler(String filename, String srcIp, int srcPort) throws IOException {
        this.filename = filename;
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        // Create new TCP connection
        socket = new Socket(srcIp, srcPort);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        FetchItem fetchItem = FetchList.getInstance().getFetchItemFetching(filename);
        this.fetchItem = fetchItem;
        this.useConsole = fetchItem.isCreatedByConsole();

        // Send request file
        try {
            dos.writeUTF("P2PFS GET \"" + filename +"\"");
            if (useConsole) {
                ClientConsole.getInstance().addText("Send GET request for file " + filename + " to ip: " + srcIp + " port: " + srcPort);
            }

        } catch (IOException e) {
            String status = "Cancel! Unable to send GET request";
            setStatusOnFetchItem(status);
            closeConnection();
        }

        // Handler receive packet
        try {
            // On response GET packet
            String responsePacket = dis.readUTF();
            Pattern pattern = Pattern.compile("^(?:\\w+)\\s+[Tt][Rr][Aa][NN][Ss][Ff][Ee][Rr]_[Bb][Ee][Gg][Ii][Nn]\\s+length=(\\d+),buffer_size=(\\d+)$");
            Matcher matcher = pattern.matcher(responsePacket);

            if (!matcher.matches()) {
                String status = "Cancel! File not found in sender host [" + srcIp + ":" + srcPort + "]!";
                setStatusOnFetchItem(status);
                closeConnection();
                return;
            }

            String status = "Start download file " + filename + "!";
            setStatusOnFetchItem(status);

            // Start download file
            int bytes = 0;
            long size = Long.parseLong(matcher.group(1));
            long fileSize = size;
            int buffer_size = Integer.parseInt(matcher.group(2));
            byte[] buffer = new byte[buffer_size];
            final long largeSize = 10000000; // 10 MB
            final long updateBound = largeSize / 2; // foreach 5 MB downloaded update UI
            long counter = 0;

            // test
            System.out.println("File size: " + size);

            String downloadFile = Client.getInstance().getRepoDir().getPath() + "/" + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(downloadFile, false);

            while (size > 0 &&
                    (bytes = dis.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
                // update gui
                counter += bytes;
                if (fileSize >= largeSize && counter >= updateBound) {
                    counter = 0;
                    double percentedDownload = Math.round(((float) (fileSize - size) / fileSize) * 10000) / 100.0;

                    this.fetchItem.setStatus("Downloading " + percentedDownload + " %");
                }
            }

            fileOutputStream.close();

            // pass packet 200 TRANSFER_COMPLETE

            status = "Download "+ filename +" completed!";
            setStatusOnFetchItem(status);

        } catch (IOException e) {
            String status = "Cancel! Lost connection to sender host [" + srcIp + ":" + srcPort + "]!";
            setStatusOnFetchItem(status);
            closeConnection();
        }


    }

    public void setStatusOnFetchItem(String status) {
        this.fetchItem.setStatus(status);
//        FetchList.getInstance().setStatusOnId(fetchId, status);
        if (useConsole) {
            ClientConsole.getInstance().addText(status);
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
