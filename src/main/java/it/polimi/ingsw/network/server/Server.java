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
    private int port;
    private ServerSocket serverSocket;
    @Expose
    private List<VirtualClient> virtualClientList = new ArrayList<>();
    @Expose
    private List<Controller> lobbies = new ArrayList<>();
    private Gson gson;
    private List<VirtualClient> waitingList = new ArrayList<>();
    private Map<Integer,String> clientHandlerNickMap = new HashMap<>();
    @Expose
    private Map<String,Integer> nickLobbyMap = new HashMap<>();

    public Server(int port) {
        gson = Utilities.initializeGsonMessage();
        this.port = port;
    }

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

    public void closeServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public synchronized void setGson(Gson gson){
        this.gson=gson;
    }
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

    public synchronized int checkLobby(){
        for(Controller lobby : lobbies){
            if(!lobby.isFull())
                return lobby.getIdLobby();
        }
        return -1;
    }

    public synchronized Controller searchLobby(int id){
        for(Controller lobby : lobbies){
            if(lobby.getIdLobby() == id){
                return lobby;
            }
        }
        return null;
    }
    public synchronized void removeLobby(Controller c){
        if(c!=null && lobbies.contains(c)) lobbies.remove(c);
    }

    public synchronized void unregisterClient(VirtualClient vc){
        if(virtualClientList.contains(vc))
            virtualClientList.remove(vc);
        try {
            if (clientHandlerNickMap.get(vc.getClientHandler().getId()) != null)
                clientHandlerNickMap.remove(vc.getClientHandler().getId());
        }catch (NullPointerException ignored){}
        if(nickLobbyMap.get(vc.getNickname())!=null)
            nickLobbyMap.remove(vc.getNickname());
        System.out.println("Unregister client "+vc.getNickname());
    }

    public synchronized void removeVirtualClient(Integer chId){
        if(clientHandlerNickMap.get(chId)!=null) {
            VirtualClient vcToRemove = searchVirtualClient(clientHandlerNickMap.get(chId));
            virtualClientList.remove(vcToRemove);
            clientHandlerNickMap.remove(chId);
            if(nickLobbyMap.get(vcToRemove.getNickname())!=null) {
                int lobbyId=nickLobbyMap.remove(vcToRemove.getNickname());
                Controller l=searchLobby(lobbyId);
                l.removePlayer(vcToRemove);
                //l.notifyLobby(new GenericMessage(vcToRemove.getNickname()+" disconnected!"));
                if(l.getPlayersInLobby().size()==0){
                    lobbies.remove(l);
                }else {
                    l.notifyLobby(new LobbyInfoMessage(l.getNames(),l.getnPlayers()));
                }
            }
        }
    }

    public synchronized VirtualClient searchVirtualClient(String nick){
        for(VirtualClient vc : virtualClientList){
            if(vc.getNickname().equals(nick))
                return vc;
        }
        return null;
    }

    public synchronized VirtualClient searchVirtualClient(Integer idCH){
        for(VirtualClient vc : virtualClientList){
            if(vc.getClientHandler().getId() == idCH)
                return vc;
        }
        return null;
    }

    public synchronized void reconnect(VirtualClient vc,VirtualClient oldVc){
        clientHandlerNickMap.remove(oldVc.getClientHandler().getId());
        oldVc.setClientHandler(vc.getClientHandler());
        clientHandlerNickMap.put(oldVc.getClientHandler().getId(),oldVc.getNickname());
    }

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

    public Map<Integer, String> getClientHandlerNickMap() {
        return clientHandlerNickMap;
    }

    public Map<String, Integer> getNickLobbyMap() {
        return nickLobbyMap;
    }

    public List<VirtualClient> getVirtualClientList() {
        return virtualClientList;
    }

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
                    //saveServerStatus();
                }
                return;
            }
        }
    }

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

            if (port < 1000 || port > 10000) {
                System.err.println("The port number value has to be between 1000 and 9999");
                System.err.println("Closing . . .");
                return;
            } //else System.err.println("port number value loaded successfully\n");

            new Server(port).startServer();
        }
    }

    public List<Controller> getLobbies() {
        return lobbies;
    }

}
