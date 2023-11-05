package com.group2.fireshare.client.service;

import com.group2.fireshare.client.Client;
import com.group2.fireshare.client.model.*;
import com.group2.fireshare.utils.Utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketHandler implements Runnable{
    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    public SocketHandler(Socket socket, DataInputStream dis, DataOutputStream dos) {
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        // Process socket
        String csfsPacket;
        while (!socket.isClosed()) {
            try {
                csfsPacket = dis.readUTF();
                processCSFSPacket(csfsPacket.trim());
            } catch (IOException e) {
                // throw new RuntimeException(e);

                System.err.println("Error! Lost connect to server");
                // System.exit(-1);
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void processCSFSPacket(String csfsPacket) {
        // log
        writeLogOnInput(csfsPacket);

        // Explain PING packet:
        // PING packet format: CSFS PING "${hostName}||${timeStart}"
        // E.g. CSFS PING "LAPTOP-42KF98B0||1698969514368"
        // We need a time ping to calculate how long since the server receive a reply

        // Explain DISCOVER packet:
        // DISCOVER packet format: CSFS DISCOVER "${hostName}||${timeStart}"
        // E.g. CSFS DISCOVER "LAPTOP-42KF98B0||1698969514368"
        // We need a timeStart to calculate how long since the server receives a reply

        Pattern requestPattern = Pattern.compile("^[Cc][Ss][Ff][Ss]\\s+(DISCOVER|PING)\\s+\"([^\"]+)\"$");
        Matcher requestMatcher = requestPattern.matcher(csfsPacket);

        if(requestMatcher.matches()) {
            String method = requestMatcher.group(1).toLowerCase();
            String requestData = requestMatcher.group(2);

            if (method.equalsIgnoreCase("discover")) {
                processDiscoverPacket(requestData);
            } else if (method.equalsIgnoreCase("ping")) {
                processPingPacket(requestData);
            } else {
                processInvalidPacket();
            }

            return;
        }


        // process response packet
        // Ex:
        //      CSFS 206 INFORMED_LISTEN_PORT “500”
        //      CSFS 201 PUBLISH_OK “a.txt”
        //      CSFS 202 FILE_EXIST “a.txt:192.168.1.12:3350”
        //      CSFS 203 FILE_NOT_FOUND “a.txt”
        Pattern responsePattern = Pattern.compile("^[Cc][Ss][Ff][Ss]\\s+(\\d+)\\s+([a-zA-Z_]+)(?:\\s+\\\"(.+)\\\")?$");
        Matcher responseMatcher = responsePattern.matcher(csfsPacket);

        if (responseMatcher.matches()) {
            // This is a response packet
            int code = Integer.parseInt(responseMatcher.group(1));
            if (code == 201) {
                processPublishResponse(responseMatcher.group(3));
            } else if (code == 202) {
                processDownloadFile(responseMatcher.group(3));
            } else if (code == 203) {
                FetchItem item = FetchList.getInstance().getFetchItemFetching(responseMatcher.group(3));
                if (item != null && item.isCreatedByConsole()) {
                    ClientConsole.getInstance().addText("Server response file not found.");
                }
                processCancelFetching(responseMatcher.group(3), "Cancel! File not found in the network!");
            }
            // pass code 206 400
        }
    }

    public void processPublishResponse(String filename) {
        if (Repository.getInstance().isFileCreatedByConsole(filename)) {
            ClientConsole.getInstance().addText("Received publish response for file: \"" + filename + "\" from server" );
            ClientConsole.getInstance().addText("Publish file\"" + filename + "\" successfully" );
        }
    }

    public void processDownloadFile(String info) {
        // info is <filename>:<ip>:<port>
        String[] param = info.split(":");
        String filename = param[0];
        String ip = param[1];
        int port = Integer.parseInt(param[2]);

        FetchItem item = FetchList.getInstance().getFetchItemFetching(filename);
        if (item == null) {
            // client didn't request this file
            return;
        }

        if (item.isCreatedByConsole()) {
            ClientConsole.getInstance().addText("Received publish response, file " + filename + " has been found in host's ip is " + ip + ", on port " + port);
        }

        DownloadFileHandler downloadFileHandler = null;
        try {
            downloadFileHandler = new DownloadFileHandler(filename, ip, port);
        } catch (IOException e) {
            // Unable create connection
            String status = "Cancel! Unable to create connection to host: " + ip + " on port: " + port;
            FetchList.getInstance().getFetchItemFetching(filename).setStatus(status);
            // Log to console
            if (item.isCreatedByConsole()) {
                ClientConsole.getInstance().addText(status);
            }
            return;
        }

        // start download filename
        new Thread(downloadFileHandler).start();

    }

    public  void processDiscoverPacket(String requestData)  {
        // E.g. CSFS DISCOVER "LAPTOP-42KF98B0||${timeStart}"

        // Split the data to get the hostname and the timeStart
        String[] parts =  requestData.split("\\|\\|");
        String hostname = parts[0];
        String timeStart = parts[1];

        List<FileItem> files = Repository.getInstance().getFileList();
        try {

            // Files is empty so we return 205 EMPTY with the hostname and the time start.
            // We reply the time start so the server can calculate the duration by itself.
            if(files.isEmpty()) {
                this.dos.writeUTF("CSFS 205 EMPTY " + "\"" + hostname + "||" + timeStart +"\"");
                return;
            }


            // Files is not empty so we return 204 CONTAIN, the hostname,a list of local files and the time start.
            // We reply the time start so the server can calculate the duration by itself.

            // Format: CSFS 204 CONTAIN "${hostname}||${strBuilder}||${timeStart}"
            // E.g. CSFS 204 CONTAIN "LAPTOP-42KF98B0||config.txt--C:\\User\\Admin\\config.txt||40"

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(hostname);
            strBuilder.append("||");

            for (FileItem file : files) {
                strBuilder.append(file.getLname() + "--" + file.getPname());
                strBuilder.append("||");
            }

            this.dos.writeUTF("CSFS 204 CONTAIN " + "\"" + strBuilder + timeStart +"\"");
        }catch (IOException e) {
            System.out.println("Send response for DISCOVER request failed " + e);
        }
    }

    public void processPingPacket(String requestData) {
        // E.g. CSFS PING "LAPTOP-42KF98B0||1698969514368"
        // Split the data to get the hostname and the timeStart.
        // We reply the time start so the server can calculate the duration by itself.

        try {
            String[] parts =  requestData.split("\\|\\|");
            String hostname = parts[0];
            String timeStart = parts[1];

            this.dos.writeUTF("CSFS 200 PING_OK " + "\"" +hostname +"||"+ timeStart +"\"");
        }catch (IOException e) {
            System.out.println("Send response for PING request failed " + e);
        }
    }

    public void processInvalidPacket() {
        try {
            this.dos.writeUTF("CSFS 400 BAD_REQUEST");
        }catch (IOException e) {
            System.out.println("Send response for invalid request failed " + e);
        }
    }

    public void processCancelFetching(String filename, String content) {
        FetchList.getInstance().setCancelFetchItem(filename, content);
    }

    public void writeLogOnInput(String content) {
        String log = "[" + LocalDateTime.now().format(Utils.getTimeFormatter())
                + "] receive packet from server: " + content;
        System.out.println(log);
    }
}
