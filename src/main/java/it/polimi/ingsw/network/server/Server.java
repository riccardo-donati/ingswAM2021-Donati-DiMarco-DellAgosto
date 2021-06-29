package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.WaitingReconnectionsException;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.exceptions.ReconnectionException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.DisconnectedMessage;
import it.polimi.ingsw.network.messages.updates.ReconnectMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    @Expose
    private final int port;
    private ServerSocket serverSocket;
    @Expose
    private final List<VirtualClient> virtualClientList = new ArrayList<>();
    @Expose
    private final List<Controller> lobbies = new ArrayList<>();
    private List<VirtualClient> waitingList = new ArrayList<>();
    private Map<Integer,String> clientHandlerNickMap = new HashMap<>();
    @Expose
    private final Map<String,Integer> nickLobbyMap = new HashMap<>();

    public Server(int port) {
        this.port = port;
    }

    /**
     * creates server socket and starts listening for connections
     * when a client connects a new connection socket is created and a new client handler is started
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("The server socket could not be created");
            System.out.println("Closing . . .");
            return;
        }
        System.out.println("Server started on port " + port);
        System.out.println("Welcome to the Masters of Renaissance server!");
        System.out.println("Waiting for players to connect . . .");

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("A new client connected");
                new Thread(new ClientHandler(socket, this)).start();
            } catch (IOException e) { // goes here if the server socket gets closed
                System.out.println("Closing . . .");
                break;
            }
        }
    }

    /**
     * save server status on a new json file
     */
    public synchronized void saveServerStatus(){
        Gson gsonSave = Utilities.initializeGsonLoadAndSave();

        String serverToJson = gsonSave.toJson(this, Server.class);
        FileWriter w;
        try {
            w = new FileWriter("serverStatus.json");
            w.write(serverToJson);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The server state could not be saved");
        }

    }

    public synchronized void setClientHandlerNickMap(Map<Integer, String> clientHandlerNickMap) {
        this.clientHandlerNickMap = clientHandlerNickMap;
    }

    public synchronized void setWaitingList(List<VirtualClient> waitingList) {
        this.waitingList = waitingList;
    }

    /**
     * if the game has not started the connections is closed
     * if the game has started, sets the players unavailable and notifies the lobby
     * @param chId disconnected client handler's id
     */
    public synchronized void handleDisconnection(Integer chId){
        String nick=clientHandlerNickMap.get(chId);
        if (nick != null){
            Controller lobby = searchLobby(nickLobbyMap.get(nick));
            if(lobby.getGameState()!=GamePhase.NOTSTARTED)
                lobby.notifyLobby(new DisconnectedMessage(nick));
            if(lobby.getGameState()== GamePhase.NOTSTARTED){
                try {
                    searchVirtualClient(nick).getClientHandler().closeConnection();
                } catch (InterruptedException | NullPointerException e) {
                    e.printStackTrace();
                }
            }else if(lobby.getGameState()==GamePhase.ONGOING ||lobby.getGameState()==GamePhase.SETUP){
                lobby.setActive(nick,false);
                if(lobby.getLorenzoPosition()==null) {
                    if (lobby.getActivePlayers().size()>0)
                        lobby.clearPlayer(nick);
                }
                if(lobby.getCurrentNickname().equals(nick)){
                    try {
                        if(lobby.getLorenzoPosition()==null) {
                            if(lobby.getActivePlayers().size()>0)
                                lobby.passTurn(nick);
                        }
                    } catch (IllegalActionException | NotYourTurnException | WaitingReconnectionsException ignored) { }
                }
            }
        }
    }

    /**
     * @return id of the non-full lobby, -1 otherwise
     */
    public synchronized int checkLobby(){
        for(Controller lobby : lobbies){
            if(!lobby.isFull())
                return lobby.getIdLobby();
        }
        return -1;
    }

    /**
     *
     * @param id lobby's id
     * @return reference to the corresponding lobby
     */
    public synchronized Controller searchLobby(int id){
        for(Controller lobby : lobbies){
            if(lobby.getIdLobby() == id){
                return lobby;
            }
        }
        return null;
    }

    /**
     * removes a lobby from the list of lobbies
     * @param c lobby to be removed
     */
    public synchronized void removeLobby(Controller c){
        if (c!=null) lobbies.remove(c);
    }

    /**
     * removes a virtual client from client handlers map and lobbies map
     * @param vc virtual client to be removed
     */
    public synchronized void unregisterClient(VirtualClient vc){
        virtualClientList.remove(vc);
        try {
            if (clientHandlerNickMap.get(vc.getClientHandler().getId()) != null)
                clientHandlerNickMap.remove(vc.getClientHandler().getId());
        }catch (NullPointerException ignored){}
        if(nickLobbyMap.get(vc.getNickname())!=null)
            nickLobbyMap.remove(vc.getNickname());
        System.out.println("Unregister client "+vc.getNickname());
    }

    /**
     * removes a virtual client from its lobby, if the lobby is empty it gets deleted
     * @param chId virtual client's id
     */
    public synchronized void removeVirtualClient(Integer chId){
        if(clientHandlerNickMap.get(chId)!=null) {
            VirtualClient vcToRemove = searchVirtualClient(clientHandlerNickMap.get(chId));
            virtualClientList.remove(vcToRemove);
            clientHandlerNickMap.remove(chId);
            if(nickLobbyMap.get(vcToRemove.getNickname())!=null) {
                int lobbyId=nickLobbyMap.remove(vcToRemove.getNickname());
                Controller l=searchLobby(lobbyId);
                l.removePlayer(vcToRemove);
                if(l.getPlayersInLobby().size()==0){
                    lobbies.remove(l);
                }else {
                    l.notifyLobby(new LobbyInfoMessage(l.getNames(),l.getnPlayers()));
                }
            }
        }
    }

    /**
     *
     * @param nick virtual client's nickname
     * @return virtual client's reference
     */
    public synchronized VirtualClient searchVirtualClient(String nick){
        for(VirtualClient vc : virtualClientList){
            if(vc.getNickname().equals(nick))
                return vc;
        }
        return null;
    }

    /**
     * swaps old virtual client with new virtual client after reconnection
     * @param vc new virtual client
     * @param oldVc old virtual client
     */
    public synchronized void reconnect(VirtualClient vc,VirtualClient oldVc){
        clientHandlerNickMap.remove(oldVc.getClientHandler().getId());
        oldVc.setClientHandler(vc.getClientHandler());
        clientHandlerNickMap.put(oldVc.getClientHandler().getId(),oldVc.getNickname());
    }

    /**
     * handles client register response
     * @param vc virtual client to be added
     * @throws ReconnectionException when a client reconnects
     * @throws IllegalArgumentException when the client's nickname is already taken
     */
    public synchronized void addVirtualClient(VirtualClient vc) throws ReconnectionException, IllegalArgumentException {
        VirtualClient vLook = searchVirtualClient(vc.getNickname());
        if (vLook != null && vLook.getClientHandler() == null) {
            //player reconnecting after server crashed
            vLook.setClientHandler(vc.getClientHandler());
            clientHandlerNickMap.put(vc.getClientHandler().getId(), vc.getNickname());
            Controller lobby = searchLobby(nickLobbyMap.get(vLook.getNickname()));
            vc.getClientHandler().setLobby(lobby);
            lobby.notifyLobby(new ReconnectMessage(vLook.getNickname()));
            lobby.reconnectPlayer(vc.getNickname());
            if(lobby.getActivePlayers().size()==lobby.getnPlayers()){
                lobby.notifyLobby(new GenericMessage("All the players reconnected! The game is resuming from the last start turn . . ."));
                lobby.setDisconnected(false);
            }
            throw new ReconnectionException();
        }
        else if (vLook != null && vLook.getClientHandler().isConnected()) {
            //nickname is not unique
            vc.getClientHandler().send(new ErrorMessage("Nickname already taken"));
            vc.getClientHandler().send(new RegisterRequest());
            throw new IllegalArgumentException();
        } else if (vLook != null && !vLook.getClientHandler().isConnected()) {
            //player reconnecting after disconnection
            reconnect(vc, vLook);
            Controller lobby = searchLobby(nickLobbyMap.get(vLook.getNickname()));
            vc.getClientHandler().setLobby(lobby);
            lobby.notifyLobby(new ReconnectMessage(vLook.getNickname()));
            lobby.reconnectPlayer(vc.getNickname());
            if(!vc.getNickname().equals(lobby.getCurrentNickname()) && !lobby.getActivePlayers().contains(lobby.getCurrentNickname())){
                //if you reconnect after the disconnection of all players and its a turn of a disconnected player clear the player and passturn
                lobby.clearPlayer(lobby.getCurrentNickname());
                try {
                    lobby.passTurn(lobby.getCurrentNickname());
                } catch (IllegalActionException | NotYourTurnException | WaitingReconnectionsException ignored) {
                }
            }
            throw new ReconnectionException();
        } else {
            //nickname is unique
            waitingList.add(vc);
            clientHandlerNickMap.put(vc.getClientHandler().getId(), vc.getNickname());
            if (checkLobby() != -1) {
                for (Controller lobby : lobbies) {
                    if (lobby.getIdLobby() == checkLobby()) {
                        lobby.notifyLobby(new GenericMessage(vc.getNickname() + " joined the lobby"));
                        lobby.addPlayerInLobby(vc);
                        vc.getClientHandler().setLobby(lobby);
                        vc.getClientHandler().send(new GenericMessage("You joined a lobby!"));
                        lobby.notifyLobby(new LobbyInfoMessage(lobby.getNames(),lobby.getnPlayers()));
                        waitingList.remove(vc);
                        virtualClientList.add(vc);
                        nickLobbyMap.put(vc.getNickname(), lobby.getIdLobby());
                        if (lobby.isFull()) {
                            lobby.start();
                            //saveServerStatus();
                        }
                    }
                }
            }
        }
    }

    public Map<String, Integer> getNickLobbyMap() {
        return nickLobbyMap;
    }

    public List<Controller> getLobbies() {
        return lobbies;
    }

    /**
     * creates new lobby and adds the client that created it
     * if the lobby is full (singleplayer) the game starts
     * @param nPlayers lobby size
     * @param clientHandler client that created the lobby
     */
    public synchronized void createNewLobby(int nPlayers, ClientHandler clientHandler){
        for(VirtualClient vc: waitingList) {
            if(vc.getClientHandler().equals(clientHandler)) {
                Controller newLobby = new Controller(nPlayers, vc,this);
                lobbies.add(newLobby);
                virtualClientList.add(vc);
                clientHandler.setLobby(newLobby);
                waitingList.remove(vc);
                nickLobbyMap.put(vc.getNickname(), newLobby.getIdLobby());
                List<String> users=new ArrayList<>();
                users.add(vc.getNickname());
                vc.getClientHandler().send(new LobbyInfoMessage(users,newLobby.getnPlayers()));
                if(newLobby.isFull()){
                    newLobby.start();
                }
                return;
            }
        }
    }

    /**
     * creates server (new or from save) and starts it
     * @param args used for restart after crash
     */
    public static void main(String[] args) {
        if(args.length>0 && args[0].equals("crash")) {
            Server s = Utilities.loadServerStatus();
            System.out.println("Reboot after server crash . . .");
            s.startServer();
        }else {
            Integer port;
            try {
                port = Utilities.loadServerPortNumber();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.err.println("serverSettings.json file not found");
                System.err.println("Closing . . .");
                return;
            }

            if (port == null || port < 1000 || port > 10000) {
                System.err.println("Not valid port number value");
                System.err.println("Closing . . .");
                return;
            } //else System.err.println("port number value loaded successfully\n");

            new Server(port).startServer();
        }
    }

}
