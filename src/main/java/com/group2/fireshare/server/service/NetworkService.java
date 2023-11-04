package com.group2.fireshare.server.service;

import com.group2.fireshare.server.model.CommandProcessingException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class NetworkService {
    private static NetworkService instance;


    public static synchronized NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }

    public void sendPingPacket(DataOutputStream dos, String hostName) throws CommandProcessingException {
        long timeMilisec = Calendar.getInstance().getTimeInMillis();
        try {
            dos.writeUTF("CSFS PING " + "\""+hostName + "||"+ timeMilisec+"\"");
        } catch (IOException e) {
            throw new CommandProcessingException(e.getMessage());
        }
    }

}
