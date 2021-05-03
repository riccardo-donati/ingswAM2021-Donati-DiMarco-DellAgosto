package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
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
    private static int n=0;
    private Gson gson;
    //private Game game;


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
        List<String > names=new ArrayList<>();
        for(VirtualClient vc : players){
            names.add(vc.getNickname());
        }
        return names;
    }
    public List<VirtualClient> getPlayers() {
        return players;
    }
    public boolean isPiena(){
        return nPlayers==players.size();
    }

    public Lobby(int nPlayers, VirtualClient firstPlayer){
        n++;
        this.idLobby=n;
        this.nPlayers=nPlayers;
        this.players.add(firstPlayer);
        gson=Utilities.initializeGsonMessage();
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
        if(players.contains(player))
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