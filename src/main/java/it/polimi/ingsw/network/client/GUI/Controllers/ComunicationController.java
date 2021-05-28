package it.polimi.ingsw.network.client.GUI.Controllers;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ComunicationController {

    public static void showError(Scene scene, String error){
        Stage stg = (Stage) scene.getWindow();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(stg);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.getDialogPane().setHeaderText(error);
        alert.showAndWait();
    }

    public static void showInfo(Scene scene, String info){
        Stage stg = (Stage) scene.getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stg);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.getDialogPane().setHeaderText(info);
        alert.showAndWait();
    }
}
