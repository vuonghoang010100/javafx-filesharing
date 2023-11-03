package com.group2.fireshare.utils;

import javafx.scene.control.Alert;

import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    // Make class can't create new instance
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private Utils() {};

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        // https://www.javaguides.net/2022/11/javafx-login-form-validation-example.html
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static boolean isValidPortNumber(String port) {
        if (port.isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Port Error", "Please input port number!");
            return false;
        }
        int portNo;
        try {
            portNo = Integer.parseInt(port);
        }
        catch (IllegalArgumentException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Port Error", "Please input a valid port number!");
            return false;
        }

        if (!(portNo >= 1024 && portNo <= 49151)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Port Error", "Port number must in registered range [1024,49151]!");
            return false;
        }
        return true;
    }

    public static boolean isValidFileName(String filename) {
        Pattern pattern = Pattern.compile("^[^/\\\\?:\\\"<>|]+$");
        Matcher matcher = pattern.matcher(filename);
        return matcher.matches();
    }

    public static DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }
}
