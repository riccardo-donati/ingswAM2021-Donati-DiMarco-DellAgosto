package it.polimi.ingsw.network.client.GUI.Controllers;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ComunicationController {

    /**
     * when an error due to an illegal action is found, the GUI shows a warning pop-up with the relative illegal action of warn
     * @param scene to show the pop up the alert needs the current scene
     * @param error the string with the error's type
     */
    public static void showError(Scene scene, String error){
        Stage stg = (Stage) scene.getWindow();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(stg);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.getDialogPane().setHeaderText(error);
        Platform.runLater(new Thread(alert::showAndWait));
    }

    /**
     * when a player needs to know what to do a Info pop-up is shown
     * @param scene to show the pop up the alert needs the current scene
     * @param info the string with what the player's need to know
     */
    public static void showInfo(Scene scene, String info){
        Stage stg = (Stage) scene.getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stg);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.getDialogPane().setHeaderText(info);
        Platform.runLater(new Thread(alert::showAndWait));
    }

    /**
     * when the player passes the turn, a pop-up shows Lorenzo's actions
     * @param scene to show the pop up the alert needs the current scene
     * @param lorenzoActions a string with what Lorenzo did during his turn
     */
    public static void showLorenzo(Scene scene, String lorenzoActions){
        Stage stg = (Stage) scene.getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lorenzo's Actions");
        alert.initOwner(stg);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().setHeaderText(lorenzoActions);
        ImageView icon = new ImageView("/images/lorenzo.png");
        icon.setFitHeight(55);
        icon.setFitWidth(55);
        alert.getDialogPane().setGraphic(icon);
        Platform.runLater(new Thread(alert::showAndWait));
    }

    /**
     * when a player reaches 7 Cards or 24 on the FaithPath the Endgame is triggered and every player is notified
     * @param scene to show the pop up the alert needs the current scene
     */
    public static void endGameNotify(Scene scene){
        Stage stg = (Stage) scene.getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EndGame notify!");
        alert.initOwner(stg);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().setHeaderText("The end phase has begun! This is the last round!");
        Platform.runLater(new Thread(alert::showAndWait));
    }
}
