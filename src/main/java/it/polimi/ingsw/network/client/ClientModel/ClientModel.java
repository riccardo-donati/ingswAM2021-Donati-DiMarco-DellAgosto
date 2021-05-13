package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.updates.DepositUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientModel {
    private Map<String, ClientBoard> boards=new HashMap<>();
    private Map<Integer,String> idNameLeadersMap=new HashMap<>();

    private List<String> playersInOrder;
    private String myNickname;
    private String currentNickname;

    private TurnPhase turnPhase;
    private GamePhase gamePhase;

    private List<DevelopmentCard> developmentCards;
    private List<LeaderCard> leaderCards;

    public ClientModel(){
        leaderCards= Utilities.loadLeaderCardsFromJSON();
        developmentCards=Utilities.loadDevelopmentCardsFromJSON();
    }

    public List<String> getPlayersInOrder() {
        return playersInOrder;
    }

    public Map<Integer, String> getIdNameLeadersMap() {
        return idNameLeadersMap;
    }

    public void putBoard(String nickname, ClientBoard cb){
        boards.put(nickname,cb);
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
    }

    public void setTurnPhase(TurnPhase turnPhase) {
        this.turnPhase = turnPhase;
    }

    public void setPlayersOrder(List<String> playersOrder) {

        this.playersInOrder = playersOrder;
    }

    public void setCurrentNickname(String currentNickname) {
        this.currentNickname = currentNickname;
    }

    public void putIdNameLeadersMap(Integer id, String name){ idNameLeadersMap.put(id,name); }
    public void setNickname(String nickname) {
        this.myNickname = nickname;
    }

    public String getNickname() { return myNickname; }

    public void setUpSinglePlayer(){
        boards.get(currentNickname).getFaithPath().setLorenzoPosition(0);
    }
    public String toString(){
        if(myNickname!=null) {
            StringBuilder sb = new StringBuilder();
            sb.append(Color.ANSI_PURPLE.escape() + "PLAYERS: " + Color.RESET);
            for (String nick : playersInOrder) {
                sb.append("[ " +Color.ANSI_GREEN.escape()+ nick +Color.RESET+ " | " + boards.get(nick).getFaithPath().getPosition() + Color.ANSI_RED.escape() + "♰" + Color.RESET + " ],");
            }
            sb.deleteCharAt(sb.toString().length()-1);
            sb.append("\n");
            sb.append("════"+myNickname+" Board═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
            sb.append(boards.get(myNickname));
            return sb.toString();
        }
        return "";
    }
    public void visualizeBoard(String nick){
        if(boards.get(nick)!=null) {
            StringBuilder sb = new StringBuilder();
            sb.append("════" + nick + " Board═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
            sb.append(boards.get(nick));
            System.out.println(sb.toString());
        }
    }
    public void visualizeDeposits(String nick){
        if(boards.get(nick)!=null){
            StringBuilder sb = new StringBuilder();
            sb.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
            sb.append(boards.get(nick).getDeposits());
            System.out.println(sb.toString());
        }
    }

    public Map<String, ClientBoard> getBoards() {
        return boards;
    }

    public String getCurrentNickname() {
        return currentNickname;
    }


    public static void main(String[] args) {
        ClientModel cm=new ClientModel();
        List<String> listInOrder=new ArrayList<>();
        listInOrder.add("Da");
        listInOrder.add("giec");
        listInOrder.add("Riki");
        cm.setPlayersOrder(listInOrder);

        cm.putBoard("Riki",new ClientBoard());
        cm.putBoard("Da",new ClientBoard());
        cm.putBoard("giec",new ClientBoard());
        cm.setNickname("Riki");
        cm.boards.get("Da").getFaithPath().addToPos(10);
        cm.boards.get("Riki").getFaithPath().addToPos(2);
        //System.out.println(cm);
        cm.visualizeBoard("giec");
    }

}
