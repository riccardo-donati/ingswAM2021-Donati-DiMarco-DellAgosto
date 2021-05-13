package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.exceptions.ReconnectionException;
import it.polimi.ingsw.network.messages.*;

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
    private List<Lobby> lobbies = new ArrayList<>();
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
            e.printStackTrace();
            System.out.println("The server socket could not be created");
            System.out.println("Closing . . .");
            return;
        }
        System.out.println("Welcome to the Masters of Renaissance server!");
        System.out.println("Waiting for players to connect . . .");

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("A new client connected");
                new Thread(new ClientHandler(socket, this)).start();
            } catch (IOException e) { // goes here if the server socket gets closed
                System.out.println("Closing . . .");
                saveServerStatus();
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

    public void saveServerStatus(){
        Gson gsonSave = Utilities.initializeGsonLoadAndSave();

        String serverToJson = gsonSave.toJson(this, Server.class);
        FileWriter w;
        try {
            w = new FileWriter("src/main/resources/json/serverStatus.json");
            w.write(serverToJson);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The server state could not be saved");
        }
    }

    public static Server loadServerStatus(){
        Gson gsonLoad=Utilities.initializeGsonLoadAndSave();
        FileReader fr = null;
        try {
            fr = new FileReader("src/json/serverStatus.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader b;
        b=new BufferedReader(fr);
        String json="";
        try {
            json=b.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Server s=gsonLoad.fromJson(json,Server.class);

        for(Lobby l : s.lobbies){
            l.getPlayers().clear();
            l.setGson(Utilities.initializeGsonMessage());
        }
        for (Map.Entry<String, Integer> entry : s.getNickLobbyMap().entrySet()) {
            if(entry.getValue()>Lobby.getGlobalID()) Lobby.setGlobalID(entry.getValue());
            VirtualClient vc=s.searchVirtualClient(entry.getKey());
            Lobby l= s.searchLobby(entry.getValue());
            l.addPlayer(vc);
        }
        s.clientHandlerNickMap=new HashMap<>();
        s.waitingList=new ArrayList<>();
        s.gson=Utilities.initializeGsonMessage();
        return s;
    }

    public void handleDisconnection(Integer chId){
        if (clientHandlerNickMap.get(chId) != null){
            String nick = clientHandlerNickMap.get(chId);
            Lobby lobby = searchLobby(nickLobbyMap.get(nick));
            lobby.notifyLobby(new GenericMessage(nick +  " disconnected!"));
            if(lobby.getGamePhase()== GamePhase.NOTSTARTED){
                try {
                    searchVirtualClient(nick).getClientHandler().closeConnection();
                } catch (InterruptedException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized int checkLobby(){
        for(Lobby lobby : lobbies){
            if(!lobby.isFull())
                return lobby.getIdLobby();
        }
        return -1;
    }

    public Lobby searchLobby(int id){
        for(Lobby lobby : lobbies){
            if(lobby.getIdLobby() == id){
                return lobby;
            }
        }
        return null;
    }

    public void removeVirtualClient(Integer chId){
        if(clientHandlerNickMap.get(chId)!=null) {
            VirtualClient vcToRemove = searchVirtualClient(clientHandlerNickMap.get(chId));
            virtualClientList.remove(vcToRemove);
            clientHandlerNickMap.remove(chId);
            if(nickLobbyMap.get(vcToRemove.getNickname())!=null) {
                int lobbyId=nickLobbyMap.remove(vcToRemove.getNickname());
                Lobby l=searchLobby(lobbyId);
                l.removePlayer(vcToRemove);
                //l.notifyLobby(new GenericMessage(vcToRemove.getNickname()+" disconnected!"));
                if(l.getPlayers().size()==0){
                    lobbies.remove(l);
                }else {
                    l.notifyLobby(new LobbyInfoMessage(l.getNames()));
                }
            }
        }
    }

    public VirtualClient searchVirtualClient(String nick){
        for(VirtualClient vc : virtualClientList){
            if(vc.getNickname().equals(nick))
                return vc;
        }
        return null;
    }

    public VirtualClient searchVirtualClient(Integer idCH){
        for(VirtualClient vc : virtualClientList){
            if(vc.getClientHandler().getId() == idCH)
                return vc;
        }
        return null;
    }

    public void reconnect(VirtualClient vc,VirtualClient oldVc){
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
            Lobby lobby = searchLobby(nickLobbyMap.get(vLook.getNickname()));
            lobby.notifyLobby(new GenericMessage(vLook.getNickname() + " reconnected after server disconnection!"));
            throw new ReconnectionException();
        }
        if (vLook != null && vLook.getClientHandler().isConnected()) {
            //nickname is not unique
            vc.getClientHandler().send(new GenericMessage("Nickname already taken"));
            vc.getClientHandler().send(new RegisterRequest());
            throw new IllegalArgumentException();
        } else if (vLook != null && !vLook.getClientHandler().isConnected()) {
            //player reconnecting after disconnection
            reconnect(vc, vLook);
            Lobby lobby = searchLobby(nickLobbyMap.get(vLook.getNickname()));
            lobby.notifyLobby(new ReconnectMessage(vLook.getNickname()));
            throw new ReconnectionException();
        } else {
            //nickname is unique
            waitingList.add(vc);
            clientHandlerNickMap.put(vc.getClientHandler().getId(), vc.getNickname());
//            maybe not needed to notify players in other lobbies?
//            for (VirtualClient v : virtualClientList)
//                if (v.getClientHandler() != null)
//                    v.getClientHandler().send(new GenericMessage(vc.getNickname() + " joined the server!"));
            if (checkLobby() != -1) {
                for (Lobby lobby : lobbies) {
                    if (lobby.getIdLobby() == checkLobby()) {
                        lobby.notifyLobby(new GenericMessage(vc.getNickname() + " joined the lobby"));
                        lobby.addPlayer(vc);
                        vc.getClientHandler().send(new GenericMessage("You joined a lobby!"));
                        vc.getClientHandler().send(new LobbyInfoMessage(lobby.getNames()));
                        waitingList.remove(vc);
                        virtualClientList.add(vc);
                        nickLobbyMap.put(vc.getNickname(), lobby.getIdLobby());
                        if (lobby.isFull()) {
                            lobby.startGame();
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
                Lobby newLobby = new Lobby(nPlayers, vc);
                lobbies.add(newLobby);
                virtualClientList.add(vc);
                waitingList.remove(vc);
                nickLobbyMap.put(vc.getNickname(), newLobby.getIdLobby());
                vc.getClientHandler().send(new GenericMessage("Lobby created"));
                if(newLobby.isFull()){
                    newLobby.startGame();
                    //saveServerStatus();
                }
                return;
            }
        }
    }

    public static void main(String[] args) {
        Integer port;
        try {
            port = Utilities.loadServerPortNumber();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("serverSettings.json file not found");
            System.err.println("Closing . . .");
            return;
        }

        if (port == null) {
            System.err.println("Port number value not found");
            System.err.println("Closing . . .");
            return;
        } else if (port < 1000 || port > 10000) {
            System.err.println("The port number value has to be between 1000 and 9999");
            System.err.println("Closing . . .");
            return;
        } //else System.err.println("port number value loaded successfully\n");

        new Server(port).startServer();
    }
}
