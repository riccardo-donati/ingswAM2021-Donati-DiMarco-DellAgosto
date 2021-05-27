package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.network.messages.PlayerNumberResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class PlayersController extends ControllerGUI {
    private static final String BOARD = "board.fxml";

    //just a basic first implementation, basically empty
    public void singlePlayer(ActionEvent event) throws IOException {
        beginSingleMatch();
    }

    private void beginSingleMatch() throws IOException {
        gui.changeScene(BOARD);
    }

    public void chooseNumber(MouseEvent mouseEvent) throws IOException {
        Node node=(Node)mouseEvent.getSource();
        gui.send(new PlayerNumberResponse(Integer.parseInt(node.getId())));
    }
}
