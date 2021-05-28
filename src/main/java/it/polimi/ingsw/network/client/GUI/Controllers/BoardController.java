package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.ClientModel.*;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.messages.commands.*;
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

import java.util.*;

public class BoardController extends ControllerGUI {
    Map<Resource, Integer> strongbox;
    List<String> leaderID = new ArrayList<>();
    Boolean clickedBox = false;
    Boolean clickedMatrix = false;
    Boolean clickedMarket = false;
    ResourceType movedRes;
    Integer row;
    Integer column;
    Character line;
    Integer pos;
    Boolean setup = true;
    Integer lCard;
    Image img;

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
    @FXML private ImageView green1;
    @FXML private ImageView green2;
    @FXML private ImageView green3;
    @FXML private ImageView blue1;
    @FXML private ImageView blue2;
    @FXML private ImageView blue3;
    @FXML private ImageView yellow1;
    @FXML private ImageView yellow2;
    @FXML private ImageView yellow3;
    @FXML private ImageView purple1;
    @FXML private ImageView purple2;
    @FXML private ImageView purple3;
    @FXML private Label player1Name;
    @FXML private Label player2Name;
    @FXML private Label player3Name;
    @FXML private ImageView marble00;
    @FXML private ImageView marble01;
    @FXML private ImageView marble02;
    @FXML private ImageView marble03;
    @FXML private ImageView marble10;
    @FXML private ImageView marble11;
    @FXML private ImageView marble12;
    @FXML private ImageView marble13;
    @FXML private ImageView marble20;
    @FXML private ImageView marble21;
    @FXML private ImageView marble22;
    @FXML private ImageView marble23;
    @FXML private ImageView pendingMarble;

    /**
     * hides and shows the strongbox Panel
     */
    public void openStrongbox() {
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

    /**
     * hides and shows the Card Market Panel
     */
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

    /**
     * hides and shows the Resource Market Panel
     */
    public void showResourceMarket() {
        TranslateTransition tt3 = new TranslateTransition(Duration.seconds(0.5), hiddenResMarket);
        if(!clickedMarket){
            if(clickedMatrix) showCardMarket();
            tt3.setFromY(0);
            tt3.setToY(670);
            clickedMarket = true;
        }
        else{
            tt3.setFromY(670);
            tt3.setToY(0);
            clickedMarket = false;
        }
        tt3.play();
    }

    /**
     * useful to manage the open/closed panels
     * @param mouseEvent on left mouse click calls the function that opens/closes the Resource Market
     */
    public void manageResMarket(MouseEvent mouseEvent){
        showResourceMarket();
    }

    /**
     * useful to manage the open/closed panels
     * @param mouseEvent on left mouse click calls the function that opens/closes the Card Market
     */
    public void manageCardMarket(MouseEvent mouseEvent){
        showCardMarket();
    }
    /**
     * function that updates every resource in the strongbox with it's value
     */
    public void updateDeposits() {
        strongboxCoins.setText(strongbox.get(Resource.COIN).toString());
        strongboxServants.setText(strongbox.get(Resource.SERVANT).toString());
        strongboxShields.setText(strongbox.get(Resource.SHIELD).toString());
        strongboxStones.setText(strongbox.get(Resource.STONE).toString());
    }

    /**
     * function that updates the value of the resource currently picked
     */
    public void updatePickedRes(){
        pickedCoins.setText(gui.getClientModel().getCurrentBoard().getDeposits().getHandResources().get(Resource.COIN).toString());
        pickedServants.setText(gui.getClientModel().getCurrentBoard().getDeposits().getHandResources().get(Resource.SERVANT).toString());
        pickedShields.setText(gui.getClientModel().getCurrentBoard().getDeposits().getHandResources().get(Resource.SHIELD).toString());
        pickedStones.setText(gui.getClientModel().getCurrentBoard().getDeposits().getHandResources().get(Resource.STONE).toString());
    }

    /**
     *updates the LCard zone, filling the rectangle with green if active, setting the back if discarded
     */
    public void updateLCards() {
        if (setup) {
            leaderID.add(gui.getClientModel().getMyBoard().getLeadersInHand().get(0).getName());
            leaderID.add(gui.getClientModel().getMyBoard().getLeadersInHand().get(1).getName());
            leaderCard1.setImage(new Image("/images/leader_cards/" + gui.getClientModel().getMyBoard().getLeadersInHand().get(0).getName() + ".png"));
            leaderCard2.setImage(new Image("/images/leader_cards/" + gui.getClientModel().getMyBoard().getLeadersInHand().get(1).getName() + ".png"));
            setup = false;
        }
        if(gui.getClientModel().getMyBoard().getLeadersInBoard().isEmpty() && setup){
            if(!gui.getClientModel().getMyBoard().getLeadersInHand().contains(leaderID.get(0))) leaderCard2.setImage(new Image("/images/back LCard.png"));
            if(!gui.getClientModel().getMyBoard().getLeadersInHand().contains(leaderID.get(1))) leaderCard1.setImage(new Image("/images/back LCard.png"));
        }
        else if(gui.getClientModel().getMyBoard().getLeadersInBoard().size()==1){
            if(gui.getClientModel().getMyBoard().getLeadersInBoard().get(0).getName().equals(leaderID.get(0))) leader1.setFill(Color.GREEN);
            else leader2.setFill(Color.GREEN);
        }
        else {
            leader1.setFill(Color.GREEN);
            leader1.setFill(Color.GREEN);
        }

    }

    /**
     * function that will update the result of the drag & drop event in the warehouse
     */
    public void updateWarehouse(ClientDeposits clientDeposits){
        String resType;
        if(clientDeposits.getShelf(1).getEmpty() != 0)
            resSlot1.setImage(new Image("/images/resources/"+checkResType(clientDeposits.getShelf(1))+".png"));
        if(clientDeposits.getShelf(2).getEmpty() != 0){
            resType = checkResType(clientDeposits.getShelf(2));
            resSlot21.setImage(new Image("/images/resources/"+resType+".png"));
            if(clientDeposits.getShelf(2).getEmpty() == 0)
                resSlot22.setImage(new Image("/images/resources/"+resType+".png"));
        }
        if(clientDeposits.getShelf(2).getEmpty() != 0){
            resType = checkResType(clientDeposits.getShelf(3));
            resSlot31.setImage(new Image("/images/resources/"+resType+".png"));
            if(clientDeposits.getShelf(3).getEmpty() != 0)
                resSlot32.setImage(new Image("/images/resources/"+resType+".png"));
            else {
                resSlot32.setImage(new Image("/images/resources/"+resType+".png"));
                resSlot33.setImage(new Image("/images/resources/"+resType+".png"));
            }
        }
        //extraDepots if they don't exist the result of this condition is null?
//        if(!clientDeposits.getShelf(3).getSpaces().equals(ResourceType.EMPTY)){
//            resSlot41.setImage(new Image("/images/resources/"+checkResType(clientDeposits.getShelf(3))+".png"));
//            if(getEmpty(clientDeposits.getShelf(3)) == 0) resSlot42.setImage(new Image("/images/resources/"+checkResType(clientDeposits.getShelf(3))+".png"));
//        }
//        if(!clientDeposits.getShelf(4).getSpaces().equals(ResourceType.EMPTY)){
//            resSlot51.setImage(new Image("/images/resources/"+checkResType(clientDeposits.getShelf(4))+".png"));
//            if(getEmpty(clientDeposits.getShelf(4)) == 0) resSlot52.setImage(new Image("/images/resources/"+checkResType(clientDeposits.getShelf(4))+".png"));
//        }
    }

    public String checkResType(Shelf shelf){
        if(shelf.getSpaces().equals(Resource.COIN)) return "coin";
        if(shelf.getSpaces().equals(Resource.SHIELD)) return "shield";
        if(shelf.getSpaces().equals(Resource.SERVANT)) return "servant";
        if(shelf.getSpaces().equals(Resource.STONE))return "stone";
        else return null;
    }

    /**
     * visualize the board passed like parameter
     * @param clientBoard the user board passed
     */
    public void updateBoard(ClientBoard clientBoard){}

    /**
     * shows which resources you got from the resource market
     */
    public void updatePending(List<Resource> pending){

    }

    public void updateCardMatrix(){
        Stack<DevelopmentCard>[][] cards = gui.getClientModel().getCardMatrix().getCards();
        green1.setImage(new Image("/images/development_cards/"+cards[0][0].peek().getName()+".png"));
        green2.setImage(new Image("/images/development_cards/"+cards[1][0].peek().getName()+".png"));
        green3.setImage(new Image("/images/development_cards/"+cards[2][0].peek().getName()+".png"));
        blue1.setImage(new Image("/images/development_cards/"+cards[0][1].peek().getName()+".png"));
        blue2.setImage(new Image("/images/development_cards/"+cards[1][1].peek().getName()+".png"));
        blue3.setImage(new Image("/images/development_cards/"+cards[2][1].peek().getName()+".png"));
        yellow1.setImage(new Image("/images/development_cards/"+cards[0][2].peek().getName()+".png"));
        yellow2.setImage(new Image("/images/development_cards/"+cards[1][2].peek().getName()+".png"));
        yellow3.setImage(new Image("/images/development_cards/"+cards[2][2].peek().getName()+".png"));
        purple1.setImage(new Image("/images/development_cards/"+cards[0][3].peek().getName()+".png"));
        purple2.setImage(new Image("/images/development_cards/"+cards[1][3].peek().getName()+".png"));
        purple3.setImage(new Image("/images/development_cards/"+cards[2][3].peek().getName()+".png"));
    }

    public void updateResMarket(){
        List<ResourceType> marbles = gui.getClientModel().getMarket().getMarbles();
        marble00.setImage(new Image("/images/marbles/"+setColor(marbles.get(0))+".png"));
        marble01.setImage(new Image("/images/marbles/"+setColor(marbles.get(1))+".png"));
        marble02.setImage(new Image("/images/marbles/"+setColor(marbles.get(2))+".png"));
        marble03.setImage(new Image("/images/marbles/"+setColor(marbles.get(3))+".png"));
        marble10.setImage(new Image("/images/marbles/"+setColor(marbles.get(4))+".png"));
        marble11.setImage(new Image("/images/marbles/"+setColor(marbles.get(5))+".png"));
        marble12.setImage(new Image("/images/marbles/"+setColor(marbles.get(6))+".png"));
        marble13.setImage(new Image("/images/marbles/"+setColor(marbles.get(7))+".png"));
        marble20.setImage(new Image("/images/marbles/"+setColor(marbles.get(8))+".png"));
        marble21.setImage(new Image("/images/marbles/"+setColor(marbles.get(9))+".png"));
        marble22.setImage(new Image("/images/marbles/"+setColor(marbles.get(10))+".png"));
        marble23.setImage(new Image("/images/marbles/"+setColor(marbles.get(11))+".png"));
        pendingMarble.setImage(new Image("/images/marbles/"+setColor(marbles.get(12))+".png"));
    }

    public String setColor(ResourceType resourceType){
        if(resourceType.equals(ResourceType.RED)) return "red";
        else if(resourceType.equals(ResourceType.BLUE)) return "blue";
        else if(resourceType.equals(ResourceType.YELLOW)) return "yellow";
        else if(resourceType.equals(ResourceType.VIOLET)) return "purple";
        else if(resourceType.equals(ResourceType.GREY)) return  "grey";
        else return "white";
    }

//---------------------------------out messages--------------------------------------------------------------

    /**
     * the click event on a Resource image sends a message of a picked resource
     * @param mouseEvent left mouse click on a specific resource
     */
    public void clickedRes(MouseEvent mouseEvent) {
        if(mouseEvent.getSource().toString().equals("ImageView[id=pickStone, styleClass=image-view]")) {
            gui.getOut().println(gui.getGson().toJson(new StrongboxPickUpCommand(ResourceType.GREY)));
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=pickServant, styleClass=image-view]")) {
            gui.getOut().println(gui.getGson().toJson(new StrongboxPickUpCommand(ResourceType.VIOLET)));
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=pickShield, styleClass=image-view]")) {
            gui.getOut().println(gui.getGson().toJson(new StrongboxPickUpCommand(ResourceType.BLUE)));
        }
        else {
            gui.getOut().println(gui.getGson().toJson(new StrongboxPickUpCommand(ResourceType.YELLOW)));
        }
    }

    /**
     * the button's action event sends a message to start all the toggled productions
     * @param event left mouse click on the button
     */
    public void produce(ActionEvent event) {
        gui.getOut().println(gui.getGson().toJson(new ActivateProductionsCommand()));
    }

    /**
     * the play button sends a message to play the relative sent card
     * @param event on click calls the play leader button
     */
    public void playLeader(ActionEvent event) {
        if(event.getSource().toString().equals("playL1")) lCard = 0;
        else lCard = 1;
        gui.getOut().println(gui.getGson().toJson(new PlayLeaderCommand(lCard)));
    }

    /**
     * the discard button sends a message to discard the relative sent card
     * @param event on click calls the discard leader button
     */
    public void discardLeader(ActionEvent event) {
        Integer lCard;
        if(event.getSource().toString().equals("discardL1")) lCard = 0;
        else lCard = 1;
        gui.getOut().println(gui.getGson().toJson(new DiscardLeaderCommand(lCard)));
    }

    /**
     * this method allows the warehouse to accept an image dropped in
     * @param dragEvent detect the drop event
     */
    public void acceptDrag(DragEvent dragEvent) {
        if(dragEvent.getDragboard().hasImage()){
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    /**
     * based on the drag event target, which is an empty Imageview that will be updated in future, i'll assign a value to a
     * integer slot. This slot identifies the warehouse position of the dropped pending resource.
     * @param dragEvent this event identifies the end of a drag & drop event
     */
    public void placeWarehouse(DragEvent dragEvent){
//        draggedRes = dragEvent.getDragboard().getImage();
        Integer slot;
        if(dragEvent.getTarget().toString().equals("ImageView[id=resSlot1, styleClass=image-view]")) slot = 1;
        else if(dragEvent.getTarget().toString().equals("ImageView[id=resSlot21, styleClass=image-view]")
                || dragEvent.getTarget().toString().equals("ImageView[id=resSlot22, styleClass=image-view]" )) slot = 2;
        else slot = 3;
        gui.getOut().println(gui.getGson().toJson(new DepositResourceCommand(movedRes, slot)));
    }

    /**
     * the function, based on where the drag event starts saves on the clipboard the relative image (to be later placed in the warehouse)
     * and assigns to a global variable, useful to the deposit message, the kind of resource i'm try to deposit
     * @param mouseEvent detects the drag event
     */
    //based on how we would like to structure the updateWarehouse i don't have to save the Resource(?)
    public void movePendingRes(MouseEvent mouseEvent){
        ClipboardContent cb = new ClipboardContent();
        ImageView source;
        if(mouseEvent.getSource().toString().equals("ImageView[id=pendingCoin, styleClass=image-view]")) {
            movedRes = ResourceType.YELLOW;
            source = pendingCoin;
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=pendingServant, styleClass=image-view]")) {
            movedRes = ResourceType.VIOLET;
            source = pendingServant;
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=pendingShield, styleClass=image-view]")) {
            movedRes = ResourceType.BLUE;
            source = pendingShield;
        }
        else {
            movedRes = ResourceType.GREY;
            source = pendingStone;
        }
        Dragboard db = source.startDragAndDrop(TransferMode.COPY);
        cb.putImage(source.getImage());
        db.setContent(cb);
        mouseEvent.consume();
    }

    /**
     * communicates to the server that the user wants to pass the turn
     * @param event left click of the pass button
     */
    public void pass(ActionEvent event) {
        gui.getOut().println(gui.getGson().toJson(new PassCommand()));
    }

    /**
     * function that based on the source of the dragging event assigns from which row and column the card is taken from
     * @param mouseEvent drag the card from the card matrix
     */
    //don't think the drag e drop works
    public void moveDCard(MouseEvent mouseEvent) {
        ClipboardContent cb = new ClipboardContent();
        ImageView source;
        if (mouseEvent.getSource().toString().equals("ImageView[id=green1, styleClass=image-view]")) {
            source = green1;
            row = 1;
            column = 1;
        } else if (mouseEvent.getSource().toString().equals("ImageView[id=green2, styleClass=image-view]")) {
            source = green2;
            row = 2;
            column = 1;
        } else if(mouseEvent.getSource().toString().equals("ImageView[id=green3, styleClass=image-view]")) {
            source = green3;
            row = 3;
            column = 1;
        } else if (mouseEvent.getSource().toString().equals("ImageView[id=blue1, styleClass=image-view]")) {
            source = blue1;
            row = 1;
            column = 2;
        } else if (mouseEvent.getSource().toString().equals("ImageView[id=blue2, styleClass=image-view]")) {
            source = blue2;
            row = 2;
            column = 2;
        } else if(mouseEvent.getSource().toString().equals("ImageView[id=blue3, styleClass=image-view]")) {
            source = blue3;
            row = 3;
            column = 2;
        } else if (mouseEvent.getSource().toString().equals("ImageView[id=yellow1, styleClass=image-view]")) {
            source = yellow1;
            row = 1;
            column = 3;
        } else if (mouseEvent.getSource().toString().equals("ImageView[id=yellow2, styleClass=image-view]")) {
            source = yellow2;
            row = 2;
            column = 3;
        } else if(mouseEvent.getSource().toString().equals("ImageView[id=yellow3, styleClass=image-view]")) {
            source = yellow3;
            row = 3;
            column = 3;
        } else if (mouseEvent.getSource().toString().equals("ImageView[id=purple1, styleClass=image-view]")) {
            source = purple1;
            row = 1;
            column = 4;
        } else if (mouseEvent.getSource().toString().equals("ImageView[id=purple2, styleClass=image-view]")) {
            source = purple2;
            row = 2;
            column = 4;
        } else {
            source = purple3;
            row = 3;
            column = 4;
        }
        Dragboard db = source.startDragAndDrop(TransferMode.COPY);
        cb.putImage(source.getImage());
        db.setContent(cb);
        mouseEvent.consume();
        showCardMarket();
    }

    /**
     *
     * @param dragEvent
     */
    public void placeSlot(DragEvent dragEvent) {
        Integer dCardSlot;
        if(dragEvent.getTarget().toString().equals("ImageView[id=slot11, styleClass=image-view]") ||
                dragEvent.getTarget().toString().equals("ImageView[id=slot12, styleClass=image-view]") ||
                dragEvent.getTarget().toString().equals("ImageView[id=slot13, styleClass=image-view]")) dCardSlot = 1;
        else if(dragEvent.getTarget().toString().equals("ImageView[id=slot21, styleClass=image-view]") ||
                dragEvent.getTarget().toString().equals("ImageView[id=slot22, styleClass=image-view]") ||
                dragEvent.getTarget().toString().equals("ImageView[id=slot23, styleClass=image-view]")) dCardSlot = 2;
        else dCardSlot = 3;
        gui.getOut().println(gui.getGson().toJson(new BuyCardCommand(row, column, dCardSlot)));
    }

    /**
     * based on which of the 1 to 3 player's images clicked will be called a function to show the respective player's board
     * @param mouseEvent the left mouse click on a player's image in the top left corner
     */
    public void playerBoard(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().toString().equals("ImageView[id=p1Board, styleClass=image-view]")) updateBoard(gui.getClientModel().getBoards().get(player1Name.getText()));
        else if (mouseEvent.getSource().toString().equals("ImageView[id=p2Board, styleClass=image-view]")) updateBoard(gui.getClientModel().getBoards().get(player2Name.getText()));
        else updateBoard(gui.getClientModel().getBoards().get(player3Name.getText()));
    }

    public void getResources(MouseEvent mouseEvent) {
        if(mouseEvent.getSource().toString().equals("ImageView[id=colonna1, styleClass=image-view]")){
            line = 'c';
            pos = 1;
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=colonna2, styleClass=image-view]")){
            line = 'c';
            pos = 2;
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=colonna3, styleClass=image-view]")){
            line = 'c';
            pos = 3;
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=colonna4, styleClass=image-view]")){
            line = 'c';
            pos = 4;
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=riga1, styleClass=image-view]")){
            line = 'r';
            pos = 1;
        }
        else if(mouseEvent.getSource().toString().equals("ImageView[id=riga2, styleClass=image-view]")){
            line = 'r';
            pos = 2;
        }
        else{
            line = 'r';
            pos = 3;
        }
        gui.getOut().println(gui.getGson().toJson(new BuyFromMarketCommand(line, pos)));
    }

    //because the deposit of resources is a drag e drop, it will be also with the discard
    public void discardResources(MouseEvent mouseEvent) {
//        gui.getOut().println(gui.getGson().toJson(new DiscardResourceCommand()));
    }
}
