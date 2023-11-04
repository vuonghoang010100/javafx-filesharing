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

        // TODO Process request packet
        // Ex:
        //      CSFS PING "LAPTOP-42KF98B0"
        //      CSFS DISCOVER "LAPTOP-42KF98B0"

        // Update comments...
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
               // TODO Process bad request
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
            // pass code 206
        }





        // Bad request here
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
        if ( item != null && item.isCreatedByConsole()) {
            ClientConsole.getInstance().addText("Received publish response, file: " + filename + " has been found in host ip=" + ip + ",port=" + port);
            ClientConsole.getInstance().addText("Send request file: " + filename + " to ip: " + ip + " port: " + port);
        }

        Platform.runLater(() -> {
            try {
                Socket downloadSocket = new Socket(ip, port);
                DataInputStream dis = new DataInputStream(downloadSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(downloadSocket.getOutputStream());

                dos.writeUTF("P2PFS GET \"" + filename +"\"");

                String responsePacket = dis.readUTF();

                Pattern pattern = Pattern.compile("^(?:\\w+)\\s+[Tt][Rr][Aa][NN][Ss][Ff][Ee][Rr]_[Bb][Ee][Gg][Ii][Nn]\\s+length=(\\d+),buffer_size=(\\d+)$");
                Matcher matcher = pattern.matcher(responsePacket);

                if (!matcher.matches()) {
                    if (item != null && item.isCreatedByConsole()) {
                        ClientConsole.getInstance().addText("Host: Ip=" + ip + " port=" + port + " response file not found.");
                    }
                    processCancelFetching(filename, "Cancel! File not found in sender host!");
                }
                else {
                    // begin download
                    FetchItem fetchItem = FetchList.getInstance().getFetchItemFetching(filename);
                    fetchItem.setStatus("Downloading...");

                    long size = Long.parseLong(matcher.group(1));
                    int buffer_size = Integer.parseInt(matcher.group(2));

                    if (fetchItem.isCreatedByConsole()) {
                        ClientConsole.getInstance().addText("Host: Ip=" + ip + " port=" + port + " response file length is " + size + " bytes");
                        ClientConsole.getInstance().addText("Start download file: " + filename);
                    }

                    int bytes = 0;
                    byte[] buffer = new byte[buffer_size];

                    String downloadFile = Client.getInstance().getRepoDir().getPath() + "/" + filename;
                    FileOutputStream fileOutputStream = new FileOutputStream(downloadFile, false);

                    while (size > 0 &&
                            (bytes = dis.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                        fileOutputStream.write(buffer, 0, bytes);
                        size -= bytes;
                    }

                    fileOutputStream.close();

                    // auto pass packet 200 TRANSFER_COMPLETE
                    fetchItem.setStatus("Download Complete!");
                    if (fetchItem.isCreatedByConsole()) {
                        ClientConsole.getInstance().addText("Download file: " + filename + " completed!");
                    }
                    Platform.runLater(() -> {
                        fetchItem.setStatus("Download Complete!");
                    });
                }

            } catch (IOException e) {
                processCancelFetching(filename, "Cancel! Lost connect to Sender!");
            }
        });

    }

    public  void processDiscoverPacket(String requestData)  {
        // Update comments...
        String[] parts =  requestData.split("\\|\\|");
        String hostname = parts[0];

        long timeStart = Long.parseLong(parts[1]);
        long timeMilisec = Calendar.getInstance().getTimeInMillis();

        List<FileItem> files = Repository.getInstance().getFileList();
        try {
            if(files.isEmpty()) {
                this.dos.writeUTF("CSFS 205 EMPTY " + "\"" + hostname + "||" + (timeMilisec - timeStart) +"\"");
                return;
            }

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(hostname);
            strBuilder.append("||");
            for (FileItem file : files) {
                strBuilder.append(file.getLname() + "--" + file.getPname());
                strBuilder.append("||");
            }
            this.dos.writeUTF("CSFS 204 CONTAIN " + "\"" + strBuilder +(timeMilisec - timeStart) +"\"");

        }catch (IOException e) {
            System.out.println("Send response for DISCOVER request failed " + e);
        }
        // Update comments...




    }

    public void processPingPacket(String requestData) {
        // Update comments...
        try {
            String[] parts =  requestData.split("\\|\\|");
            String hostname = parts[0];
            long timeStart = Long.parseLong(parts[1]);

            long timeMilisec = Calendar.getInstance().getTimeInMillis();
            this.dos.writeUTF("CSFS 200 PING_OK " + "\"" +hostname +"||"+ (timeMilisec - timeStart) +"\"");
        }catch (IOException e) {
            System.out.println("Send response for DISCOVER request failed " + e);
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
