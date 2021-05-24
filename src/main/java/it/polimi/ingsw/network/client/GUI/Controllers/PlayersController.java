package it.polimi.ingsw.network.client.GUI.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class PlayersController extends ControllerGUI {
    private static final String BOARD = "board.fxml";

    @FXML private Button onePlayer;
    @FXML private Button twoPlayers;
    @FXML private Button threePlayers;
    @FXML private Button fourPlayers;

    //just a basic first implementation, basically empty
    public void singlePlayer(ActionEvent event) throws IOException {
        beginSingleMatch();
    }

    private void beginSingleMatch() throws IOException {
        gui.changeScene(BOARD);
    }

    public void multiPlayer(ActionEvent event) throws IOException {
        createLobby();
    }

    private void createLobby() throws IOException {
        gui.changeScene("/fxml/lobby.fxml");
    }
}
