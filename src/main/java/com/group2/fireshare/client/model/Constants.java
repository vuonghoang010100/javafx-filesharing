package com.group2.fireshare.client.model;

public class Constants {
    public static final String USER_CONFIGURATION_FILE_NAME="configs.txt";
    public static final String USER_REPO_PATH="USER_REPO_PATH";
    public static final String USER_PORT="USER_PORT";
    public static final String SERVER_IP="USER_SERVER_IP";
    public static final String SERVER_PORT="USER_SERVER_PORT";


    private Constants() {
        // Private constructor to prevent instantiation
    }

    private static class SingletonHolder {
        private static final Constants INSTANCE = new Constants();
    }

    public static Constants getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
