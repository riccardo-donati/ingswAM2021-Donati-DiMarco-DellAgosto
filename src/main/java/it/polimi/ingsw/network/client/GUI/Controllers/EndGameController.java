package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.model.Result;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class EndGameController extends ControllerGUI{

    @FXML private ImageView playerResult;
    @FXML private Label pointsP2;
    @FXML private Label pointsP4;
    @FXML private Label pointsP1;
    @FXML private Label pointsP3;
    @FXML private Label resourcesP2;
    @FXML private Label resourcesP1;
    @FXML private Label resourcesP3;
    @FXML private Label resourcesP4;
    @FXML private Label p1;
    @FXML private Label p2;
    @FXML private Label p3;
    @FXML private Label p4;

    /**
     * generates a list of Labels, useful to assign later
     */
    public EndGameController(){
        List<Label> playersResources = new ArrayList<>();
        playersResources.add(resourcesP1);
        playersResources.add(resourcesP2);
        playersResources.add(resourcesP3);
        playersResources.add(resourcesP4);
        List<Label> playersPoints = new ArrayList<>();
        playersPoints.add(pointsP1);
        playersPoints.add(pointsP2);
        playersPoints.add(pointsP3);
        playersPoints.add(pointsP4);
    }

    /**
     * Shows the final game's results, each player's name their score and number of resources
     * @param result A class that contains each player's score
     */
    public void setValues(Result result) {
        if(gui.getClientModel().getPlayersInOrder().size() == 0){
            p1.setText(gui.getClientModel().getCurrentNickname());
            if(result.getWinner().contains("Lorenzo") && result.getWinner().size()==1)
                playerResult.setImage(new Image("/images/youlost.png"));
            else    playerResult.setImage(new Image("/images/youwon.png"));
            pointsP1.setText(result.getResults().get(gui.getClientModel().getNickname())[0].toString());
            resourcesP1.setText(result.getResults().get(gui.getClientModel().getNickname())[1].toString());
        }
        else{
            if(result.getWinner().contains(gui.getClientModel().getNickname()))
                playerResult.setImage(new Image("/images/youwon.png"));
            else    playerResult.setImage(new Image("/images/youlost.png"));

            pointsP1.setText(result.getResults().get(gui.getClientModel().getNickname())[0].toString());
            resourcesP1.setText(result.getResults().get(gui.getClientModel().getNickname())[1].toString());
            p1.setText(gui.getClientModel().getNickname());
            pointsP2.setText(result.getResults().get(gui.getClientModel().getPlayersInOrder().get(0))[0].toString());
            resourcesP2.setText(result.getResults().get(gui.getClientModel().getPlayersInOrder().get(0))[1].toString());
            p2.setText(gui.getClientModel().getPlayersInOrder().get(0));
            p2.setOpacity(100);
            pointsP2.setOpacity(100);
            resourcesP2.setOpacity(100);
            if(gui.getClientModel().getPlayersInOrder().size() > 1){
                pointsP3.setText(result.getResults().get(gui.getClientModel().getPlayersInOrder().get(1))[0].toString());
                resourcesP3.setText(result.getResults().get(gui.getClientModel().getPlayersInOrder().get(1))[1].toString());
                p3.setText(gui.getClientModel().getPlayersInOrder().get(1));
                p3.setOpacity(100);
                pointsP3.setOpacity(100);
                resourcesP3.setOpacity(100);
            }
            if(gui.getClientModel().getPlayersInOrder().size() == 3){
                pointsP4.setText(result.getResults().get(gui.getClientModel().getPlayersInOrder().get(2))[0].toString());
                resourcesP4.setText(result.getResults().get(gui.getClientModel().getPlayersInOrder().get(2))[1].toString());
                p4.setText(gui.getClientModel().getPlayersInOrder().get(2));
                p4.setOpacity(100);
                pointsP4.setOpacity(100);
                resourcesP4.setOpacity(100);
            }
        }
    }
}
