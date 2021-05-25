package it.polimi.ingsw.network.client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Result;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitorHandler;
import it.polimi.ingsw.network.client.GUI.Controllers.BoardController;
import it.polimi.ingsw.network.client.GUI.Controllers.ControllerGUI;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.DepositUpdate;
import it.polimi.ingsw.network.messages.updates.LorenzoUpdate;
import it.polimi.ingsw.network.messages.updates.PendingResourcesUpdate;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class GUI extends Application implements Client{
    private Stage stage;
    private Scene currentScene;
    private final Map<String, Scene> buildedScenes = new HashMap<>();
    private final Map<String, ControllerGUI> buildedControllers = new HashMap<>();
    private static final String LOGIN = "login.fxml";
    private static final String LOADING = "loading screen.fxml";
    private static final String NPLAYERS = "numberOfPlayers.fxml";
    private static final String BOARD = "board.fxml";
    private String serverIP;
    private Integer serverPortNumber;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private Gson gson;
    private final ClientVisitorHandler clientVisitorHandler;
    private final ClientModel clientModel;

    public GUI(){
        clientVisitorHandler = new ClientVisitorHandler();
        clientModel = new ClientModel();
    }

    @Override
    public void start(Stage stage)throws Exception{
        setup();
        this.stage = stage;
        run();
    }

    private void run() {
        stage.setScene(currentScene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/coin.png")));
        stage.centerOnScreen();
        stage.sizeToScene();
        stage.show();
    }

    private void setup() {
        List<String> listFxml = new ArrayList<>(Arrays.asList(LOGIN,NPLAYERS,BOARD));
        try{
            for(String path : listFxml){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + path));
                Parent content = loader.load();
                buildedScenes.put(path, new Scene(content));
                ControllerGUI controller = loader.getController();
                controller.setGUI(this);
                buildedControllers.put(path, controller);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        currentScene = buildedScenes.get(BOARD);
    }

    public void changeScene(String newScene) {
        currentScene = buildedScenes.get(newScene);
        stage.setScene(currentScene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public Socket getSocket() {
        return null;
    }

    @Override
    public ClientModel getClientModel() {
        return clientModel;
    }

    @Override
    public Gson getGson() {
        return null;
    }

    @Override
    public Scanner getIn() {
        return null;
    }

    @Override
    public PrintWriter getOut() {
        return null;
    }

    @Override
    public void visualizeDisconnectionMessage(DisconnectionMessage message) {

    }

    @Override
    public void visualizeGeneralMessage(ClientMessage message) {

    }

    @Override
    public void visualizeLobbyInfoMessage(LobbyInfoMessage message) {

    }

    @Override
    public void visualizeSlotUpdate() {

    }

    @Override
    public void visualizeStartGameUpdate() {

    }

    @Override
    public void visualizeNewTurnUpdate(GamePhase previousGamePhase) {

    }

    @Override
    public void visualizeBonusResourceMessage(BonusResourceMessage message) {

    }

    @Override
    public void visualizePendingResourceUpdate(PendingResourcesUpdate message) {

    }

    @Override
    public void visualizeDepositUpdate(DepositUpdate message) {
        BoardController bc = (BoardController) buildedControllers.get(BOARD);
        bc.updateWarehouse(getClientModel().getCurrentBoard().getDeposits());
    }

    @Override
    public void visualizeLorenzoUpdate(LorenzoUpdate message, GamePhase previousGamePhase) {

    }

    @Override
    public void visualizePopeFavorUpdate() {

    }

    @Override
    public void visualizeMoveResourceUpdate() {

    }

    @Override
    public void visualizeErrorMessage(ErrorMessage message) {

    }

    @Override
    public void visualizeLeadersInHandUpdate() {

    }

    @Override
    public void visualizeDepositsUpdate() {

    }

    @Override
    public void visualizeToggleProductionUpdate() {

    }

    @Override
    public void visualizeUnknownProductionUpdate() {

    }

    @Override
    public void visualizePickUpWarehouseUpdate() {

    }

    @Override
    public void visualizePickUpStrongboxUpdate() {

    }

    @Override
    public void visualizePlayLeaderUpdate() {

    }

    @Override
    public void visualizeToggleDiscountUpdate() {

    }

    @Override
    public void visualizeEndGameMessage() {

    }

    @Override
    public void visualizeEndGameResultUpdate(Result gameResult) {

    }
}
