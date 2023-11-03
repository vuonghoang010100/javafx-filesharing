package com.group2.fireshare.client.service;

import com.group2.fireshare.client.model.Repository;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendfileClientHandler implements Runnable{
    private Socket clientSocket;
    DataInputStream dis;
    DataOutputStream dos;
    private final int BUFFER_SIZE = 4 * 1024;

    public SendfileClientHandler(Socket clientSocket, DataInputStream dis, DataOutputStream dos) {
        this.clientSocket = clientSocket;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        try {
            String p2pfsPacket = dis.readUTF();
            Pattern pattern = Pattern.compile("^(?:\\w+)\\s+[Gg][Ee][Tt]\\s+\\\"(.+)\\\"$");
            Matcher matcher = pattern.matcher(p2pfsPacket.trim());
            if (!matcher.matches()) {
                dos.writeUTF("P2PFS 400 BAD_REQUEST");
            }
            String filename = matcher.group(1);
            String filePath = Repository.getInstance().getFilePath(filename);
            if (filePath.isEmpty()) {
                dos.writeUTF("P2PFS 404 FILE_NOT_EXIST");
            }
            // begin transfer
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);

            long fileLength = file.length();
            dos.writeUTF("P2PFS TRANSFER_BEGIN length=" + fileLength + ",buffer_size=" + BUFFER_SIZE);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytes = 0;
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                dos.write(buffer, 0, bytes);
                dos.flush();
            }
            fileInputStream.close();

            dos.writeUTF("P2PFS TRANSFER_COMPLETE");

            dos.close();
            dis.close();

        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
