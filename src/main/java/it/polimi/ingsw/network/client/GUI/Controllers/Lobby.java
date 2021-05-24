package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.network.client.GUI.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class Lobby extends ControllerGUI{

    public Lobby(){
    }

    @FXML private Label player1Name;
    @FXML private Label player2Name;
    @FXML private Label player3Name;
    @FXML private Label player4Name;
    @FXML private Button update;
    @FXML private Button done;

    //basically everything is wrong, will need a future implementation
    private void buttonPressed()throws IOException{}

    private void doneSetup()throws IOException{
        gui.changeScene("/fxml/loading screen.fxml");
    }

    public void updateLobby(ActionEvent event)throws Exception{
        buttonPressed();
    }

    public void waitForTurn(ActionEvent event)throws Exception{
        doneSetup();
    }
}
