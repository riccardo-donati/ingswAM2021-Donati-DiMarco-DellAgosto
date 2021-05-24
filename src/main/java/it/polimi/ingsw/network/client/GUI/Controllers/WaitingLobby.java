package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.network.client.GUI.GUI;
import javafx.event.ActionEvent;

import java.io.IOException;

public class WaitingLobby extends ControllerGUI{

    public void WaitingLobby(ActionEvent event)throws IOException {
        GUI gui = new GUI();
        gui.changeScene("lobby.fxml");
    }
}
