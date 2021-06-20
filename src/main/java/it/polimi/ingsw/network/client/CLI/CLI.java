package it.polimi.ingsw.network.client.CLI;

import com.google.gson.Gson;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.Result;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.Parser;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.CLI.enums.Color;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitorHandler;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class CLI implements Client {
    private final String serverIP;
    private final Integer serverPortNumber;
    private Socket socket;

    private PrintWriter out;
    private Scanner in;
    private BufferedReader stdIn;

    private Gson gson;

    private final ClientVisitorHandler clientVisitorHandler;
    private final ClientModel clientModel;
    private String currCommand = "";

    public CLI(String serverIP, Integer serverPortNumber){
        this.serverIP = serverIP;
        this.serverPortNumber = serverPortNumber;
        clientModel = new ClientModel();
        clientVisitorHandler = new ClientVisitorHandler();
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public Scanner getIn() {
        return in;
    }

    public Gson getGson() {
        return gson;
    }

    public ClientModel getClientModel() {
        return clientModel;
    }

    public void setCurrCommand(String currCommand) {
        this.currCommand = currCommand;
    }

    public void handleStdIn(){
        String line;
        while (!socket.isClosed()) {
            try {
                line = stdIn.readLine();
                Message message;
                try {
                    message = Parser.parse(currCommand + line, this);
                    if (message != null)
                        out.println(gson.toJson(message, Message.class));
                } catch (IllegalCommandException e) {
                    System.out.println(Color.ANSI_RED.escape() + "Wrong syntax" + Color.RESET);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        gson = Utilities.initializeGsonMessage();

        try {
            socket = new Socket(serverIP, serverPortNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            new Thread(this::handleStdIn).start();
        } catch (Exception e){
            System.out.println("Server not available");
            return;
        }
        System.out.println("Connected to server port " + serverPortNumber + " and IP address " + serverIP + "\n");
        String jsonString;
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
            message.accept(clientVisitorHandler, this);
        }
    }

    public static void main(String[] args) throws IOException {
        String serverIP;
        Integer serverPortNumber;
        System.out.println("Select an option:");
        System.out.println("1)Connect from file");
        System.out.println("2)Connect manually");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Integer n=null;
        while(true) {
            try {
                String choice=input.readLine();
                n = Integer.parseInt(choice);
                if(n==1 || n==2) break;
                System.out.println("Invalid choice, try again . . .");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again . . .");
            }
        }
        switch (n) {
            case 1 -> {
                System.out.println("Loading connection settings from file . . .");
                try {
                    serverIP = Utilities.loadServerIP();
                    serverPortNumber = Utilities.loadServerPortNumber();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.err.println("serverSettings.json file not found");
                    System.err.println("Closing . . .");
                    return;
                }
            }
            case 2 -> {
                System.out.println("Insert the Ip address:");
                serverIP = input.readLine();
                while (true) {
                    System.out.println("Insert the port:");
                    try {
                        serverPortNumber = Integer.parseInt(input.readLine());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect port format, try again . . .");
                    }
                }
            }
            default -> {
                System.out.println("Error, closing . . .");
                return;
            }
        }
        new CLI(serverIP, serverPortNumber).run();

    }

    // Client interface implementation

    @Override
    public void visualizeDisconnectionMessage(DisconnectionMessage message) {
        System.out.println(message.getMessage());
    }

    @Override
    public void visualizeGeneralMessage(ClientMessage message) {
        System.out.println(message.getMessage());
    }

    @Override
    public void visualizeLobbyInfoMessage(LobbyInfoMessage message) {
        System.out.println(message.getMessage());
    }

    @Override
    public void visualizeSlotUpdate() {
        if(getClientModel().getCurrentNickname().equals(getClientModel().getNickname())){
            System.out.println(getClientModel().getCurrentBoard().stringifySlots());
        }
    }

    @Override
    public void visualizeStartGameUpdate() {
        System.out.println(clientModel);
        System.out.println(Color.ANSI_GREEN.escape() + "SETUP PHASE BEGIN"+Color.RESET);

        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------------------------------------\n");
        sb.append("Player Order:\n");
        List<String> playerOrder = clientModel.getPlayersInOrder();
        for (int i=0;i<playerOrder.size()-1;i++){
            sb.append(Color.ANSI_GREEN.escape()).append(playerOrder.get(i)).append(Color.RESET);
            sb.append(" -> ");
        }
        sb.append(Color.ANSI_GREEN.escape()).append(playerOrder.get(playerOrder.size() - 1)).append(Color.RESET+"\n");

        List<String> leaderCards = clientModel.getSetupPhaseLeaderCards();
        sb.append("Choose 2 leader cards: \n");
        for (int i = 0; i < leaderCards.size(); i++) {
            sb.append(Color.ANSI_RED.escape()).append(i + 1).append(Color.RESET).append(": ").append(clientModel.stringifyLeaderCardFromName(leaderCards.get(i))).append("\n");
        }
        sb.append("------------------------------------------------------------------------------------");
        System.out.println(sb.toString());
    }

    @Override
    public void visualizeNewTurnUpdate(GamePhase previousGamePhase) {
        if (clientModel.getGamePhase() == GamePhase.ONGOING  && clientModel.getNickname().equals(clientModel.getCurrentNickname()))
            System.out.println(clientModel);
        System.out.println("New turn -> " + clientModel.getCurrentNickname());
        if (previousGamePhase == GamePhase.SETUP && clientModel.getGamePhase() == GamePhase.ONGOING) {
            System.out.println(Color.ANSI_GREEN.escape()+"NORMAL GAME PHASE BEGIN"+ Color.RESET);
        }
    }

    @Override
    public void visualizeBonusResourceMessage(BonusResourceMessage message) {
        System.out.println(message.getMessage());
    }

    @Override
    public void visualizePendingResourceUpdate() {
        System.out.println(clientModel.getCurrentBoard().stringifyPending());
    }

    @Override
    public void visualizeDepositUpdate(DepositUpdate message) {
        if(clientModel.getCurrentNickname().equals(clientModel.getNickname()))
            clientModel.visualizeDeposits(clientModel.getNickname());
        else System.out.println(clientModel.getCurrentNickname() + " " + message.getMessage());
    }

    @Override
    public void visualizeLorenzoUpdate(LorenzoUpdate message, GamePhase previousGamePhase) {
        System.out.println(clientModel);
        if (previousGamePhase == GamePhase.SETUP && clientModel.getGamePhase() == GamePhase.ONGOING)
            System.out.println(Color.ANSI_GREEN.escape() + "NORMAL GAME PHASE BEGIN" + Color.RESET);
        System.out.println(Utilities.stringify(message.getLastUsedToken()));
    }

    @Override
    public void visualizePopeFavorUpdate() {
        if(clientModel.getNickname().equals(clientModel.getCurrentNickname()))
            System.out.println(clientModel.getCurrentBoard().getFaithPath());
    }

    @Override
    public void visualizeMoveResourceUpdate() {
        if(clientModel.getNickname().equals(clientModel.getCurrentNickname()))
            System.out.println(clientModel.getBoards().get(clientModel.getCurrentNickname()).getDeposits());
    }

    @Override
    public void visualizeErrorMessage(ErrorMessage message) {
        System.out.println(Color.ANSI_RED.escape() + message.getMessage() + Color.RESET);
    }

    @Override
    public void visualizeLeadersInHandUpdate() {
        System.out.println(clientModel.getBoards().get(clientModel.getCurrentNickname()).stringifyLeaders());
    }

    @Override
    public void visualizeDepositsUpdate() {
        if(clientModel.getCurrentNickname().equals(clientModel.getNickname())){
            System.out.println(clientModel.getCurrentBoard().getDeposits());
        }
    }

    @Override
    public void visualizeToggleProductionUpdate() {
        System.out.print("Active productions: ");
        if(clientModel.getCurrentNickname().equals(clientModel.getNickname())){
            for(Production production : clientModel.getCurrentBoard().getActiveProductions()){
                System.out.print("[" + Utilities.stringify(production) + "]");
            }
        }
        System.out.print("\n");
    }

    @Override
    public void visualizeUnknownProductionUpdate() {
        System.out.println(clientModel.getCurrentBoard().stringifyProductions());
    }

    @Override
    public void visualizePickUpWarehouseUpdate() {
        if(clientModel.getCurrentNickname().equals(clientModel.getNickname())){
            System.out.println(clientModel.getCurrentBoard().getDeposits());
            System.out.println(clientModel.getCurrentBoard().getDeposits().stringifyHandResources());
        }
    }

    @Override
    public void visualizePickUpStrongboxUpdate() {
        if(clientModel.getCurrentNickname().equals(clientModel.getNickname())){
            System.out.println(clientModel.getCurrentBoard().getDeposits());
            System.out.println(clientModel.getCurrentBoard().getDeposits().stringifyHandResources());
        }
    }

    @Override
    public void visualizePlayLeaderUpdate() {
        if(clientModel.getCurrentNickname().equals(clientModel.getNickname())){
            System.out.println(clientModel.getCurrentBoard().stringifyLeaders());
        }
    }

    @Override
    public void visualizeToggleDiscountUpdate() {
        if(clientModel.getCurrentNickname().equals(clientModel.getNickname())){
            System.out.println(clientModel.getCurrentBoard().stringifyActiveDiscounts());
        }
    }

    @Override
    public void visualizeEndGameMessage() {
        if(clientModel.getCurrentBoard().getFaithPath().getLorenzoPosition() == null)
            System.out.println(Color.ANSI_GREEN.escape() + "END GAME TRIGGERED, THE GAME WILL END AT THE START OF THE FIRST PLAYER TURN" + Color.RESET);
        else System.out.println(Color.ANSI_GREEN.escape() + "THE GAME IS ENDING . . ." + Color.RESET);
    }

    @Override
    public void visualizeEndGameResultUpdate(Result gameResult) {
        System.out.println(Utilities.stringify(gameResult));
    }

    @Override
    public void visualizeNumberOfPlayer(PlayerNumberRequest message) {
        System.out.println(message.getMessage());
        currCommand="numberofplayers";

    }

    @Override
    public void visualizeWait() {
        System.out.println("Hold a second . . .");
    }

    @Override
    public void visualizeRegisterRequest() {
        System.out.println("Please insert your nickname: ");
        currCommand="register ";
    }

    @Override
    public void visualizeReconnection(ReconnectUpdate message) {
        System.out.println(clientModel);
        System.out.println("-----------------------------");
        System.out.println("You just reconnected!");
        System.out.println("Phase: "+clientModel.getGamePhase());
        System.out.println("Turn: "+clientModel.getCurrentNickname());
        StringBuilder sb=new StringBuilder();
        if(clientModel.getGamePhase()==GamePhase.SETUP && clientModel.getCurrentNickname().equals(clientModel.getNickname()) && clientModel.getCurrentBoard().getLeadersInHand().size()==0){
            //if you haven't chosen leadercards in singleplayer
            sb.append("Choose 2 leader cards: \n");
            List<String> leaderCards=message.getFourLeaderCards();
            for(int i=0;i<leaderCards.size();i++){
                sb.append(Color.ANSI_RED.escape()).append(i+1).append(Color.RESET).append(": ").append(clientModel.stringifyLeaderCardFromName(leaderCards.get(i))).append("\n");
            }
        }
        if(message.getPendingResources().size()>0){
            if(message.getPendingResources().size() > 0) {
                sb.append("Deposit this pending resources:\n[");
                for (ResourceType resourceType : message.getPendingResources()) {
                    Resource resource = Resource.valueOf(resourceType.label.toUpperCase());
                    sb.append(resourceType.label).append("(").append(resource.label).append("),");
                    //mex.append(ResourceType.valueOfLabel(res.toString())+"("+Resource.valueOf()+")");
                }
                if (message.getPendingResources().size() > 0) sb.deleteCharAt(sb.toString().length() - 1);
                sb.append("]");
            }
        }
        System.out.println("-----------------------------");
        System.out.println(sb.toString());
    }

    @Override
    public void visualizeDiscardLeaderUpdate() {
        if(clientModel.getNickname().equals(clientModel.getCurrentNickname())) {
            System.out.println(clientModel.getMyBoard().getFaithPath());
            System.out.println(clientModel.getMyBoard().stringifyLeaders());
        }
    }

    @Override
    public void visualizeFaithUpdate() {
        //do nothing
    }

    @Override
    public void visualizeMarketUpdate() {
        //do nothing
    }

    @Override
    public void visualizeDisconnectedMessage(String nickname) {
        System.out.println("Disconnected Players: ");
        System.out.println(clientModel.getDisconnectedPlayers());
    }

    @Override
    public void visualizeReconnectMessage(ReconnectMessage message) {
        System.out.println(message.getMessage());
        if (clientModel.getNickname().equals(message.getReconnectedNickname())) {
            currCommand = "";
        }
    }

    @Override
    public void visualizeDiscardResource() {
        if(clientModel.getMyBoard().getFaithPath().getLorenzoPosition()!=null){
            System.out.println(clientModel.getMyBoard().getFaithPath());
        }
    }

    @Override
    public void visualizeRevert() {
        //do nothing
    }

    @Override
    public void visualizeResetProductions() {
        System.out.println("Productions resetted!");
    }

}
