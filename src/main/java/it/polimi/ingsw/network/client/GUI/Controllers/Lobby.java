package it.polimi.ingsw.network.client.GUI.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lobby extends ControllerGUI{

    List<String> nicks;
    Integer n;
    public Lobby(){
    }

    @FXML private AnchorPane anchor1;
    @FXML private AnchorPane anchor2;
    @FXML private AnchorPane anchor3;
    @FXML private AnchorPane anchor4;

    @FXML private Label player1Name;
    @FXML private Label player2Name;
    @FXML private Label player3Name;
    @FXML private Label player4Name;

    //basically everything is wrong, will need a future implementation
    private void buttonPressed()throws IOException{}

    private void doneSetup()throws IOException{
        gui.changeScene("/fxml/waiting_screen.fxml");
    }
    public void reset(){
        player1Name.setText("");
        player2Name.setText("");
        player3Name.setText("");
        player4Name.setText("");
        anchor1.setOpacity(0.32);
        anchor2.setOpacity(0.32);
        anchor3.setOpacity(0.32);
        anchor4.setOpacity(0.32);

        anchor1.setVisible(true);
        anchor2.setVisible(true);
        anchor3.setVisible(true);
        anchor4.setVisible(true);

    }
    public void setPlayers(List<String> nickList,int num){
        reset();
        nicks=nickList;
        n=num;

        if(num==1){
            anchor2.setVisible(false);
            anchor3.setVisible(false);
            anchor4.setVisible(false);
        }
        else if(num==2){
            anchor3.setVisible(false);
            anchor4.setVisible(false);
        }
        else if(num==3){
            anchor4.setVisible(false);
        }

        for(int i=1;i<=nickList.size();i++){
            if(i==1){ player1Name.setText(nickList.get(i-1));anchor1.setOpacity(1);}
            else if(i==2){ player2Name.setText(nickList.get(i-1));anchor2.setOpacity(1);}
            else if(i==3){ player3Name.setText(nickList.get(i-1));anchor3.setOpacity(1);}
            else if(i==4){ player4Name.setText(nickList.get(i-1));anchor4.setOpacity(1);}
        }
    }
    public void disconnectPlayer(String name){
        if(nicks!=null && nicks.contains(name)){
            nicks.remove(name);
            setPlayers(nicks,n);
        }
    }
    public void updateLobby(ActionEvent event)throws Exception{
        buttonPressed();
    }

    public void waitForTurn(ActionEvent event)throws Exception{
        doneSetup();
    }
}
