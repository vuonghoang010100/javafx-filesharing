package com.group2.fireshare.utils;

import javafx.scene.control.Alert;

public class Utils {
    // Make class can't create new instance
    private Utils() {};

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        // https://www.javaguides.net/2022/11/javafx-login-form-validation-example.html
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
