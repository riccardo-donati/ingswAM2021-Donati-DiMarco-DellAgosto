package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.messages.*;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    @Expose
    private int idLobby;
    @Expose
    private int nPlayers;
    @Expose
    private List<VirtualClient> players = new ArrayList<>();
    @Expose
    private boolean started;
    @Expose
    private static int globalID = 0;
    private Gson gson;
    //private Game game;


    public static void setGlobalID(int id) {
        Lobby.globalID = id;
    }

    public static int getGlobalID() {
        return globalID;
    }

    public int getIdLobby() {
        return idLobby;
    }

    public List<String> getNames(){
        List<String> names = new ArrayList<>();
        for(VirtualClient vc : players){
            names.add(vc.getNickname());
        }
        return names;
    }
    public List<VirtualClient>

    getPlayers() {
        return players;
    }

    public boolean isFull(){
        return nPlayers == players.size();
    }

    public Lobby(int nPlayers, VirtualClient firstPlayer){
        globalID++;

        this.idLobby = globalID;
        this.nPlayers = nPlayers;
        gson = Utilities.initializeGsonMessage();
        started = false;

        this.players.add(firstPlayer);
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void notifyLobby(Message message){
        for(VirtualClient virtualClient : players){
            if(virtualClient.getClientHandler() != null) {
                virtualClient.getClientHandler().send(message);
            }
        }
    }

    public void removePlayer(VirtualClient player){
        players.remove(player);
    }

    public void startGame(){
        notifyLobby(new GenericMessage("Game starting . . ."));
        //game=new Game(nPlayers);
        notifyLobby(new GenericMessage("Game started!"));
        started=true;
    }

    public boolean isStarted() {
        return started;
    }

    public void addPlayer(VirtualClient vc){
        players.add(vc);
    }
}