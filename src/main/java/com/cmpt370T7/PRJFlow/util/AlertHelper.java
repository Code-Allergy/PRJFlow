package com.cmpt370T7.PRJFlow.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertHelper {

    /**
     * Shows an error alert with a specified title and message.
     *
     * @param title   The title of the error alert.
     * @param message The message to display in the error alert.
     */
    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    /**
     * Shows a warning alert with a specified title and message.
     *
     * @param title   The title of the warning alert.
     * @param message The message to display in the warning alert.
     */
    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    /**
     * Shows an information alert with a specified title and message.
     *
     * @param title   The title of the information alert.
     * @param message The message to display in the information alert.
     */
    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    /**
     * Shows a confirmation alert with a specified title and message.
     * Returns true if the user selects OK, otherwise false.
     *
     * @param title   The title of the confirmation alert.
     * @param message The message to display in the confirmation alert.
     * @return true if the user clicks OK, false otherwise.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    /**
     * Shows an alert of the specified type with the given title and message.
     *
     * @param alertType The type of the alert (e.g., ERROR, WARNING, INFORMATION).
     * @param title     The title of the alert.
     * @param message   The message to display in the alert.
     */
    private static void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
