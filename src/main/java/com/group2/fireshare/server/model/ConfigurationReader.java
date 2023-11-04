package com.group2.fireshare.server.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationReader {

    private  static ConfigurationReader instance = new ConfigurationReader();


    private ConfigurationReader() {
        // Private constructor to prevent instantiation
    }

    public static ConfigurationReader getInstance() {
        return instance;
    }

    public static Properties readConfigurationFromUserData() {
        String userHome = System.getProperty("user.home");
        String fileName = Constants.SERVER_CONFIGURATION_FILE_NAME;
        String filePath = userHome + File.separator + "AppData" + File.separator + "Roaming" + File.separator + fileName;
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) { }

}
