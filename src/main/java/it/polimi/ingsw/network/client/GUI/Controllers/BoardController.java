package it.polimi.ingsw.network.client.GUI.Controllers;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.util.Map;

public class BoardController extends ControllerGUI {
    Boolean clickedBox = false;
    Boolean clickedMatrix = false;
    Boolean clickedMarket = false;
    //Strongbox s = new Strongbox();
    Integer coins = 0;
    Integer servants = 0;
    Integer shields = 0;
    Integer stones = 0;

    public BoardController(){
    }

    @FXML private AnchorPane hiddenPanel;
    @FXML private Label pickedCoins;
    @FXML private Label pickedServants;
    @FXML private Label pickedShields;
    @FXML private Label pickedStones;
    @FXML private Label strongboxCoins;
    @FXML private Label strongboxServants;
    @FXML private Label strongboxShields;
    @FXML private Label strongboxStones;
    @FXML private Rectangle leader1;
    @FXML private Rectangle leader2;
    @FXML private ImageView leaderCard1;
    @FXML private ImageView leaderCard2;
    @FXML private AnchorPane hiddenCardMatrix;
    @FXML private AnchorPane hiddenResMarket;
    @FXML private ImageView resSlot1;
    @FXML private ImageView resSlot21;
    @FXML private ImageView resSlot22;
    @FXML private ImageView resSlot31;
    @FXML private ImageView resSlot32;
    @FXML private ImageView resSlot33;
    @FXML private ImageView pendingCoin;
    @FXML private ImageView pendingServant;
    @FXML private ImageView pendingShield;
    @FXML private ImageView pendingStone;

    //hides and shows the strongbox
    public void openStrongbox() {
        //updateDeposits();
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.7), hiddenPanel);
        if(!clickedBox){
            tt.setFromX(0);
            tt.setToX(273);
            clickedBox = true;
        }
        else{
            tt.setFromX(273);
            tt.setToX(0);
            clickedBox = false;
        }
        tt.play();
    }

//    needs to be fused with client strongbox
//
//    private void updateDeposits() {
//        for(Map.Entry<String, Integer> entry : s.getStrongbox().entrySet()){
//            if( entry.getKey().equals("Coin")) strongboxCoins.setText(entry.getValue().toString());
//            else if( entry.getKey().equals("Shield")) strongboxShields.setText(entry.getValue().toString());
//            else if( entry.getKey().equals("Stone")) strongboxStones.setText(entry.getValue().toString());
//            else strongboxServants.setText(entry.getValue().toString());
//        }
//    }
//
//    next function needs to be updated to a better version, using client-server updates
//
       public void clickedRes(MouseEvent mouseEvent) {
//        String res;
//        if(mouseEvent.getSource().toString().equals("ImageView[id=pickStone, styleClass=image-view]")) {
//            res = "Stone";
//            stones++;
//            pickedStones.setText(stones.toString());
//        }
//        else if(mouseEvent.getSource().toString().equals("ImageView[id=pickServant, styleClass=image-view]")) {
//            res = "Servant";
//            servants++;
//            pickedServants.setText(servants.toString());
//        }
//        else if(mouseEvent.getSource().toString().equals("ImageView[id=pickShield, styleClass=image-view]")) {
//            res = "Shield";
//            shields++;
//            pickedShields.setText(shields.toString());
//        }
//        else {
//            res = "Coin";
//            coins++;
//            pickedCoins.setText(coins.toString());
//        }
//        s.getStrongbox().replace(res, s.getStrongbox().get(res)-1); //decrement strongbox value, works only in the strongbox is made with strings, values
//        updateDeposits();
        }

    //obviously this is only a graphic solution that sets picked res to 0
    public void produce(ActionEvent event) {
        pickedCoins.setText("");
        coins=0;
        pickedServants.setText("");
        servants=0;
        pickedShields.setText("");
        shields=0;
        pickedStones.setText("");
        stones=0;
    }

    //next 4 functions are only graphics solution, basically don't communicate with the clientModel
    public void playLeader1(ActionEvent event) {
        if(leaderCard1.getOpacity() == 0) return;
        leader1.setFill(Color.GREEN);
        leader1.setStroke(Color.GREEN);
    }

    public void discardLeader1(ActionEvent event) {
        if(leader1.getFill().equals(Color.GREEN)) return;
        leaderCard1.setOpacity(0);
    }

    public void playerLeader2(ActionEvent event) {
        if(leaderCard2.getOpacity() == 0) return;
        leader2.setFill(Color.GREEN);
        leader2.setStroke(Color.GREEN);
    }

    public void discardLeader2(ActionEvent event) {
        if(leader2.getFill().equals(Color.GREEN)) return;
        leaderCard2.setOpacity(0);
    }

    //hides and shows the card market
    public void showCardMarket() {
        TranslateTransition tt2 = new TranslateTransition(Duration.seconds(0.5), hiddenCardMatrix);
        if(!clickedMatrix){
            if(clickedMarket) showResourceMarket();
            tt2.setFromX(0);
            tt2.setToX(-653);
            clickedMatrix = true;
        }
        else{
            tt2.setFromX(-653);
            tt2.setToX(0);
            clickedMatrix = false;
        }
        tt2.play();
    }

    //hides and shows the res market
    public void showResourceMarket() {
        TranslateTransition tt3 = new TranslateTransition(Duration.seconds(0.5), hiddenResMarket);
        if(!clickedMarket){
            if(clickedMatrix) showCardMarket();
            tt3.setFromY(0);
            tt3.setToY(679);
            clickedMarket = true;
        }
        else{
            tt3.setFromY(679);
            tt3.setToY(0);
            clickedMarket = false;
        }
        tt3.play();
    }

    public void manageResMarket(MouseEvent mouseEvent){
        showResourceMarket();
    }

    public void manageCardMarket(MouseEvent mouseEvent){
        showCardMarket();
    }

    //is the method the allows the drop event
    public void acceptRes(DragEvent dragEvent) {
        if(dragEvent.getDragboard().hasImage()){
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    //identifies the resource dropped and changes the imageView to the one dragged, it needs to communicate with the client Model for logic
    //needs also to know if something could be dropped
    public void placeSlot(DragEvent dragEvent){
        ImageView target;
        Image img = dragEvent.getDragboard().getImage();
        if(dragEvent.getTarget().toString().equals("ImageView[id=resSlot1, styleClass=image-view]")) target = resSlot1;
        else if(dragEvent.getTarget().toString().equals("ImageView[id=resSlot21, styleClass=image-view]")) target = resSlot21;
        else if(dragEvent.getTarget().toString().equals("ImageView[id=resSlot22, styleClass=image-view]")) target = resSlot22;
        else if(dragEvent.getTarget().toString().equals("ImageView[id=resSlot31, styleClass=image-view]")) target = resSlot31;
        else if(dragEvent.getTarget().toString().equals("ImageView[id=resSlot32, styleClass=image-view]")) target = resSlot32;
        else target = resSlot33;
        target.setImage(img);
    }

    //identifies what kind of resource is being dragged, its ok this way? i don't think that needs to communicate with models
    public void movePendingRes(MouseEvent mouseEvent){
        ClipboardContent cb = new ClipboardContent();
        ImageView source;
        if(mouseEvent.getSource().toString().equals("ImageView[id=pendingCoin, styleClass=image-view]")) source = pendingCoin;
        else if(mouseEvent.getSource().toString().equals("ImageView[id=pendingServant, styleClass=image-view]")) source = pendingServant;
        else if(mouseEvent.getSource().toString().equals("ImageView[id=pendingShield, styleClass=image-view]")) source = pendingShield;
        else source = pendingStone;
        Dragboard db = source.startDragAndDrop(TransferMode.COPY);
        cb.putImage(source.getImage());
        db.setContent(cb);
        mouseEvent.consume();
    }
}
