package com.group2.fireshare.client.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationSaver {

    public static void saveConfigurationToUserData(Properties properties) {
        String userHome = System.getProperty("user.home");
        String fileName = Constants.USER_CONFIGURATION_FILE_NAME;
        String filePath = userHome + File.separator + "AppData" + File.separator + "Roaming" + File.separator + fileName;
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileOutputStream output = new FileOutputStream(configFile)) {
            properties.store(output, "Configuration File");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { }
}
