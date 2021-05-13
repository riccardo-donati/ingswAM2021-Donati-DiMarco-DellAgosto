package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.StartGameUpdate;

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
    private Controller gameController;


    public int getnPlayers() {
        return nPlayers;
    }

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
        this.nPlayers=nPlayers;
        this.players.add(firstPlayer);
        this.gameController=new Controller();
        gson= Utilities.initializeGsonMessage();
        started=false;
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

    public List<String> getNicknames(){
        List<String> nicknames=new ArrayList<>();
        for(VirtualClient vc : players){
            nicknames.add(vc.getNickname());
        }
        return nicknames;
    }

    public Controller getGameController() {
        return gameController;
    }

    public void startGame(){
        notifyLobby(new GenericMessage("Game starting . . ."));
        gameController.initializeGame(nPlayers);
        gameController.addPlayers(getNicknames());
        gameController.start();
        started=true;
        List<List<LeaderCard>> lists=gameController.getLeaderCards();
        if(lists.size()!=players.size()){
            System.out.println("Internal Error, please reinitialize the game");
            return;
        }
        notifyLobby(new GenericMessage("Game started!"));
        for(VirtualClient vc : players){
            List<String> l=gameController.getPlayerLeaderCardList(vc.getNickname());
            Message m=new StartGameUpdate(gameController.getOrderPlayerList(),l);
            vc.send(m);
        }

    }

    public boolean isStarted() {
        return started;
    }

    public void addPlayer(VirtualClient vc){
        players.add(vc);
    }
}