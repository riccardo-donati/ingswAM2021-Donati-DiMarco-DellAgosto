package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.messages.commands.ChooseBonusResourceCommand;
import it.polimi.ingsw.network.messages.commands.ChooseLeadersCommand;
import it.polimi.ingsw.network.messages.commands.PassCommand;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetupController extends ControllerGUI{
    List<String> chosenL = new ArrayList<>();
    List<Rectangle> selected = new ArrayList<>();
    Boolean replace = false;
    Integer id;
    ResourceType res;
    List<String> leaderCards;

    public SetupController(){
    }

    @FXML private ImageView leaderCard1;
    @FXML private ImageView leaderCard2;
    @FXML private ImageView leaderCard3;
    @FXML private ImageView leaderCard4;
    @FXML private Button warehouse1;
    @FXML private Button warehouse2;
    @FXML private Button warehouse3;
    @FXML private Rectangle l1Cover;
    @FXML private Rectangle l2Cover;
    @FXML private Rectangle l4Cover;
    @FXML private Rectangle l3Cover;
    @FXML private ImageView selectedServant;
    @FXML private ImageView selectedCoin;
    @FXML private ImageView selectedShield;
    @FXML private ImageView selectedStone;

    public void updateLeader(){
        leaderCards = gui.getClientModel().getSetupPhaseLeaderCards();
        leaderCard1.setImage(new Image("/images/leader_cards/"+leaderCards.get(0)+".png"));
        leaderCard2.setImage(new Image("/images/leader_cards/"+leaderCards.get(1)+".png"));
        leaderCard3.setImage(new Image("/images/leader_cards/"+leaderCards.get(2)+".png"));
        leaderCard4.setImage(new Image("/images/leader_cards/"+leaderCards.get(3)+".png"));
    }

    public void colorLeader(MouseEvent mouseEvent) {
        if (selected.size()==2) replace = true;
        else replace = false;
        if(mouseEvent.getSource().toString().equals("ImageView[id=leaderCard1, styleClass=image-view]")){
            checkList(l1Cover);
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=leaderCard2, styleClass=image-view]")){
            checkList(l2Cover);
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=leaderCard3, styleClass=image-view]")){
            checkList(l3Cover);
        }
        else {
            checkList(l4Cover);
        }
        colorSelected();
    }

    public void checkList(Rectangle r){
        if(selected.contains(r)) selected.remove(r);
        else if(replace && !selected.contains(r)){
            selected.remove(0);
            selected.add(r);
        }
        else selected.add(r);
    }

    public void colorSelected(){
        if(selected.contains(l1Cover)) l1Cover.setFill(Color.GREEN);
        else l1Cover.setFill(Color.valueOf("#b0b3b5"));
        if(selected.contains(l2Cover)) l2Cover.setFill(Color.GREEN);
        else l2Cover.setFill(Color.valueOf("#b0b3b5"));
        if(selected.contains(l3Cover)) l3Cover.setFill(Color.GREEN);
        else l3Cover.setFill(Color.valueOf("#b0b3b5"));
        if(selected.contains(l4Cover)) l4Cover.setFill(Color.GREEN);
        else l4Cover.setFill(Color.valueOf("#b0b3b5"));
    }


    public void chooseLeaders(MouseEvent mouseEvent) throws IOException {
        gui.getOut().println(gui.getGson().toJson(new ChooseLeadersCommand(leaderList())));
    }

    public void passTurn(MouseEvent mouseEvent) {
        gui.getOut().println(gui.getGson().toJson(new PassCommand()));
    }

    public List<String> leaderList(){
        leaderCards = gui.getClientModel().getSetupPhaseLeaderCards();
        for(Rectangle r : selected){
            if(r.equals(l1Cover)) chosenL.add(leaderCards.get(0));
            else if(r.equals(l2Cover)) chosenL.add(leaderCards.get(1));
            else if(r.equals(l3Cover)) chosenL.add(leaderCards.get(2));
            else chosenL.add(leaderCards.get(3));
        }
        return chosenL;
    }

    public void placeResource(MouseEvent mouseEvent) throws IOException {
        if(mouseEvent.getSource().toString().equals("Button[id=warehouse1, styleClass=button]")) id = 1;
        else if(mouseEvent.getSource().toString().equals("Button[id=warehouse2, styleClass=button]")) id = 2;
        else id = 3;
        selectedCoin.setOpacity(0);
        selectedShield.setOpacity(0);
        selectedServant.setOpacity(0);
        selectedStone.setOpacity(0);
        gui.getOut().println(gui.getGson().toJson(new ChooseBonusResourceCommand(res, id)));
    }

    public void selectedRes(MouseEvent mouseEvent) {
        if(mouseEvent.getSource().toString().equals("ImageView[id=selectedCoin, styleClass=image-view]")) {
            res = ResourceType.YELLOW;
            if (selectedCoin.getOpacity() == 100) selectedCoin.setOpacity(0);
            else {
                selectedCoin.setOpacity(100);
                selectedShield.setOpacity(0);
                selectedServant.setOpacity(0);
                selectedStone.setOpacity(0);
            }
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=selectedShield, styleClass=image-view]")) {
            res = ResourceType.BLUE;
            if (selectedShield.getOpacity() == 100) selectedShield.setOpacity(0);
            else {
                selectedCoin.setOpacity(0);
                selectedShield.setOpacity(100);
                selectedServant.setOpacity(0);
                selectedStone.setOpacity(0);
            }
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=selectedServant, styleClass=image-view]")) {
            res = ResourceType.VIOLET;
            if (selectedServant.getOpacity() == 100) selectedServant.setOpacity(0);
            else {
                selectedCoin.setOpacity(0);
                selectedShield.setOpacity(0);
                selectedServant.setOpacity(100);
                selectedStone.setOpacity(0);
            }
        }
        else {
            res = ResourceType.GREY;
            if (selectedStone.getOpacity() == 100) selectedStone.setOpacity(0);
            else {
                selectedCoin.setOpacity(0);
                selectedShield.setOpacity(0);
                selectedServant.setOpacity(0);
                selectedStone.setOpacity(100);
            }
        }
        mouseEvent.consume();
    }
}
