package com.group2.fireshare.server.model;

public class Constants {

    public static final String SERVER_CONFIGURATION_FILE_NAME="serverConfigs.txt";

    public static final String SERVER_PORT="SERVER_PORT";


    private Constants() {
        // Private constructor to prevent instantiation
    }

    private static class SingletonHolder {
        private static final Constants instance = new Constants();
    }

    public static Constants getInstance() {
        return SingletonHolder.instance;
    }

}
