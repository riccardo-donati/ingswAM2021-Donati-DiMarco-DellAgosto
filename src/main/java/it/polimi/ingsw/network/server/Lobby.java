package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.LeaderCard;
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
    private List<VirtualClient> players=new ArrayList<>();
    @Expose
    private boolean started;
    @Expose
    private static int n = 0;
    private Gson gson;
    private Controller gameController;


    public static void setN(int n) {
        Lobby.n = n;
    }

    public static int getN() {
        return n;
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
        return nPlayers==players.size();
    }

    public Lobby(int nPlayers, VirtualClient firstPlayer){
        n++;
        this.idLobby=n;
        this.nPlayers=nPlayers;
        this.players.add(firstPlayer);
        this.gameController=new Controller();
        gson= Utilities.initializeGsonMessage();
        started=false;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void notifyLobby(Message m){
        for(VirtualClient vc : players){
            if(vc.getClientHandler()!=null) {
                vc.getClientHandler().getOut().println(gson.toJson(m, Message.class));
                vc.getClientHandler().getOut().flush();
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
        for(int i=0;i<players.size();i++){
            List<String> l=new ArrayList<>();
            for(int j=0;j<4;j++){
                l.add(lists.get(i).get(j).getName());
            }
            Message m=new StartGameMessage(gameController.getOrderPlayerList(),l);
            players.get(i).send(m);
        }

    }

    public boolean isStarted() {
        return started;
    }

    public void addPlayer(VirtualClient vc){
        players.add(vc);
    }
}