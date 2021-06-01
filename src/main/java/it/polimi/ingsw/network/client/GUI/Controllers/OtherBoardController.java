package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.client.CLI.enums.ClientPopeFavorState;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.*;
import it.polimi.ingsw.network.client.GUI.GUI;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OtherBoardController extends ControllerGUI{
    @FXML private ImageView faithPath0;
    @FXML private ImageView resSlot1;
    @FXML private ImageView resSlot21;
    @FXML private ImageView resSlot22;
    @FXML private ImageView resSlot31;
    @FXML private ImageView resSlot32;
    @FXML private ImageView resSlot33;
    @FXML private ImageView faithPath1;
    @FXML private ImageView faithPath2;
    @FXML private ImageView faithPath3;
    @FXML private ImageView faithPath4;
    @FXML private ImageView faithPath5;
    @FXML private ImageView faithPath6;
    @FXML private ImageView faithPath7;
    @FXML private ImageView faithPath8;
    @FXML private ImageView faithPath9;
    @FXML private ImageView faithPath10;
    @FXML private ImageView faithPath11;
    @FXML private ImageView faithPath12;
    @FXML private ImageView faithPath13;
    @FXML private ImageView faithPath14;
    @FXML private ImageView faithPath15;
    @FXML private ImageView faithPath16;
    @FXML private ImageView faithPath17;
    @FXML private ImageView faithPath18;
    @FXML private ImageView faithPath19;
    @FXML private ImageView faithPath20;
    @FXML private ImageView faithPath21;
    @FXML private ImageView faithPath22;
    @FXML private ImageView faithPath23;
    @FXML private ImageView faithPath24;

    @FXML private Rectangle leader1;
    @FXML private Rectangle leader2;
    @FXML private ImageView leaderCard1;
    @FXML private ImageView leaderCard2;

    @FXML private ImageView pope1;
    @FXML private ImageView pope2;
    @FXML private ImageView pope3;

    List<ImageView> popes=new ArrayList<>();
    List<List<ImageView>> warehouse=new ArrayList<>();
    List<ImageView> faithPath=new ArrayList<>();
    @Override
    public void initializeElements(){
        List<ImageView> slot1=new ArrayList<>();
        slot1.add(resSlot1);
        List<ImageView> slot2=new ArrayList<>();
        slot2.add(resSlot21);
        slot2.add(resSlot22);
        List<ImageView> slot3=new ArrayList<>();
        slot3.add(resSlot31);
        slot3.add(resSlot32);
        slot3.add(resSlot33);
        warehouse.add(slot1);
        warehouse.add(slot2);
        warehouse.add(slot3);

        faithPath.add(faithPath0);
        faithPath.add(faithPath1);
        faithPath.add(faithPath2);
        faithPath.add(faithPath3);
        faithPath.add(faithPath4);
        faithPath.add(faithPath5);
        faithPath.add(faithPath6);
        faithPath.add(faithPath7);
        faithPath.add(faithPath8);
        faithPath.add(faithPath9);
        faithPath.add(faithPath10);
        faithPath.add(faithPath11);
        faithPath.add(faithPath12);
        faithPath.add(faithPath13);
        faithPath.add(faithPath14);
        faithPath.add(faithPath15);
        faithPath.add(faithPath16);
        faithPath.add(faithPath17);
        faithPath.add(faithPath18);
        faithPath.add(faithPath19);
        faithPath.add(faithPath20);
        faithPath.add(faithPath21);
        faithPath.add(faithPath22);
        faithPath.add(faithPath23);
        faithPath.add(faithPath24);

        popes.add(pope1);
        popes.add(pope2);
        popes.add(pope3);
    }
    public void goBack(MouseEvent mouseEvent) {
        gui.changeScene(GUI.BOARD);
    }

    public void updateWarehouse(ClientDeposits clientDeposits){
        List<Shelf> shelves=clientDeposits.getShelves();
        for(int i=0;i<shelves.size();i++){
            List<ImageView> slotImageViews=warehouse.get(i);
            for(int j=0;j<slotImageViews.size();j++){
                if(shelves.get(i).getSpaces()[j]== Resource.EMPTY)
                    slotImageViews.get(j).setImage(null);
                else{
                    slotImageViews.get(j).setImage(new Image("/images/resources/" +shelves.get(i).getSpaces()[j].toString().toLowerCase()+ ".png"));
                }
            }
        }
    }
    public void updatePopeFavor(ClientFaithPath cfp){
        for(int i=0;i<popes.size();i++) {
            if (cfp.getPopeFavor().get(i+1).equals(ClientPopeFavorState.ACTIVE))
                popes.get(i).setImage(new Image("/images/faithpath/pope_favor1_front.png"));
            else if (cfp.getPopeFavor().get(i+1).equals(ClientPopeFavorState.DISCARDED))
                popes.get(i).setOpacity(0);
        }
    }
    public void updateLCards(ClientBoard clientBoard){
        leaderCard1.setImage(new Image("/images/back LCard.png"));
        leaderCard2.setImage(new Image("/images/back LCard.png"));
        Map<Integer,String> disc=clientBoard.getDiscardedCards();
        Map<Integer,String> played=clientBoard.getPlayedCards();
        if(disc.get(0)!=null) {
            leader1.setFill(Color.RED);
        }
        if(disc.get(1)!=null){
            leader2.setFill(Color.RED);
        }
        if(played.get(0)!=null){
            leaderCard1.setImage(new Image("/images/leader_cards/" + clientBoard.getLeadersInBoard().get(0).getName() + ".png"));
            leader1.setFill(Color.GREEN);
        }
        if(played.get(1)!=null){
            leaderCard2.setImage(new Image("/images/leader_cards/" + clientBoard.getLeadersInBoard().get(1).getName() + ".png"));
            leader2.setFill(Color.GREEN);
        }

    }
    public void updateFaithPath(ClientFaithPath clientFaithPath){
        for(ImageView i : faithPath) i.setOpacity(0);
        Integer position=clientFaithPath.getPosition();
        faithPath.get(position).setOpacity(100);
    }

    public void visualizeBoard(String nickname){
        ClientBoard cb=gui.getClientModel().getBoards().get(nickname);
        if(cb!=null){
            updateWarehouse(cb.getDeposits());
            updateFaithPath(cb.getFaithPath());
            updateLCards(cb);
            updatePopeFavor(cb.getFaithPath());
        }
    }


}
