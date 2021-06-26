package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.network.messages.PlayerNumberResponse;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class PlayersController extends ControllerGUI {
    private static final String BOARD = "board.fxml";

    /**
     * the scene between the lobby and the waiting scene is where the player chooses the lobby's size
     * @param mouseEvent click on a number
     * @throws IOException
     */
    public void chooseNumber(MouseEvent mouseEvent) throws IOException {
        Node node=(Node)mouseEvent.getSource();
        gui.send(new PlayerNumberResponse(Integer.parseInt(node.getId())));
    }
}
