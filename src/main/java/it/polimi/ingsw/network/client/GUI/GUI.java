package it.polimi.ingsw.network.client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Result;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitorHandler;
import it.polimi.ingsw.network.client.GUI.Controllers.*;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.DepositUpdate;
import it.polimi.ingsw.network.messages.updates.LorenzoUpdate;
import it.polimi.ingsw.network.messages.updates.PendingResourcesUpdate;
import javafx.application.Application;
import javafx.application.Platform;
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

public class GUI extends Application implements Client {
    private Stage stage;
    private Scene currentScene;
    private final Map<String, Scene> buildedScenes = new HashMap<>();
    private final Map<String, ControllerGUI> buildedControllers = new HashMap<>();
    private static final String LOGIN = "login.fxml";
    private static final String WAITING = "waiting_screen.fxml";
    private static final String NPLAYERS = "numbers.fxml";
    private static final String BOARD = "board.fxml";
    private static final String LOBBY = "lobby.fxml";
    private static final String SETUP = "setup_phase.fxml";
    private static final String OTHERBOARD = "otherPlayersBoard.fxml";


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
        gson = Utilities.initializeGsonMessage();
    }

    @Override
    public void start(Stage stage){
        setup();
        this.stage = stage;
        run();

    }
    public void send(Message m){
        out.println(gson.toJson(m,Message.class));
    }

    private void run() {
        stage.setScene(currentScene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/calamaio.png")));
        stage.centerOnScreen();
        stage.sizeToScene();
        stage.show();
    }

    private void handleMessages(){
        String jsonString="";
        while (!socket.isClosed()) {
            try {
                jsonString = in.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println("Error, disconnecting . . .");
                in.close();
                out.close();
                break;
            }
            ClientMessage message = gson.fromJson(jsonString, ClientMessage.class);
            Platform.runLater(new Thread(()->message.accept(clientVisitorHandler, this)));
        }
    }
    public void connect(String serverIP,Integer serverPortNumber){
        try {
            socket = new Socket(serverIP, serverPortNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Server not available");
            return;
        }
        this.serverIP=serverIP;
        this.serverPortNumber=serverPortNumber;
        new Thread(this::handleMessages).start();

    }

    private void setup() {
        List<String> listFxml = new ArrayList<>(Arrays.asList(LOGIN,NPLAYERS,BOARD,LOBBY,WAITING,SETUP,OTHERBOARD));
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
        stage.setResizable(false);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public ClientModel getClientModel() {
        return clientModel;
    }

    @Override
    public Gson getGson() {
        return gson;
    }

    @Override
    public Scanner getIn() {
        return in;
    }

    @Override
    public PrintWriter getOut() {
        return out;
    }

    @Override
    public void visualizeDisconnectionMessage(DisconnectionMessage message) {

    }

    @Override
    public void visualizeGeneralMessage(ClientMessage message) {

    }

    @Override
    public void visualizeLobbyInfoMessage(LobbyInfoMessage message) {
        Lobby lc=(Lobby)buildedControllers.get(LOBBY);
        Platform.runLater(new Thread(()->lc.setPlayers(message.getNickList(),message.getNum())));
        if(currentScene!=buildedScenes.get(LOBBY))
             Platform.runLater(new Thread(()->changeScene(LOBBY)));
    }

    @Override
    public void visualizeSlotUpdate() {

    }

    @Override
    public void visualizeStartGameUpdate() {
        SetupController sc = (SetupController) buildedControllers.get(SETUP);
        sc.updateLeader();
        BoardController bc = (BoardController) buildedControllers.get(BOARD);
        bc.updateCardMatrix();
        bc.updateResMarket();
        if(clientModel.getCurrentNickname().equals(clientModel.getNickname()))
            Platform.runLater(new Thread(()->changeScene(SETUP)));
        else Platform.runLater(new Thread(()->changeScene(WAITING)));
    }

    @Override
    public void visualizeNewTurnUpdate(GamePhase previousGamePhase) {
        if(clientModel.getGamePhase().equals(GamePhase.SETUP)){
            if(clientModel.getCurrentNickname().equals(clientModel.getNickname()))
                Platform.runLater(new Thread(()->changeScene(SETUP)));
            else Platform.runLater(new Thread(()->changeScene(WAITING)));
        }else if(clientModel.getGamePhase().equals(GamePhase.ONGOING)) {
            BoardController bc = (BoardController) buildedControllers.get(BOARD);
            bc.setIcons();
            Platform.runLater(new Thread(() -> changeScene(BOARD)));
        }
    }

    @Override
    public void visualizeBonusResourceMessage(BonusResourceMessage message) {
        ComunicationController.showInfo(currentScene, message.getMessage());
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
//        if(clientModel.getGamePhase().equals(GamePhase.ONGOING))
            ComunicationController.showLorenzo(currentScene, message.getMessage());
    }

    @Override
    public void visualizePopeFavorUpdate() {
        BoardController bc = (BoardController) buildedControllers.get(BOARD);
        bc.updatePopeFavor();
    }

    @Override
    public void visualizeMoveResourceUpdate() {

    }

    @Override
    public void visualizeErrorMessage(ErrorMessage message) {
        ComunicationController.showError(currentScene, message.getMessage());
    }

    @Override
    public void visualizeLeadersInHandUpdate() {
        BoardController bc = (BoardController) buildedControllers.get(BOARD);
        bc.updateLCards();
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

    @Override
    public void visualizeNumberOfPlayer(PlayerNumberRequest message) {
       Platform.runLater(new Thread(()->changeScene(NPLAYERS)));
    }

    @Override
    public void visualizeWait() {
        Platform.runLater(new Thread(()->changeScene(WAITING)));
    }

    @Override
    public void visualizeRegisterRequest() {
        if(currentScene==buildedScenes.get(LOGIN)){
            LogIn l=(LogIn)buildedControllers.get(LOGIN);
            l.register();
        }
    }
}
