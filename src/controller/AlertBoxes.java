package controller;

import javafx.scene.control.Alert;

public class AlertBoxes {
    public static void showErrorBox(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Look, an Error Dialog");
        alert.setContentText(text);

        alert.showAndWait();
    }
}
