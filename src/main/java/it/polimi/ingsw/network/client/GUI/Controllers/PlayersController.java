package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.network.messages.PlayerNumberResponse;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class PlayersController extends ControllerGUI {
    private static final String BOARD = "board.fxml";

    public void chooseNumber(MouseEvent mouseEvent) throws IOException {
        Node node=(Node)mouseEvent.getSource();
        gui.send(new PlayerNumberResponse(Integer.parseInt(node.getId())));
    }
}
