package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;

import java.util.*;

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

    private ClientMarket market;
    private ClientCardMatrix cardMatrix;

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public List<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    public ClientModel(){
        leaderCards= Utilities.loadLeaderCardsFromJSON();
        developmentCards=Utilities.loadDevelopmentCardsFromJSON();
        market=new ClientMarket();
        cardMatrix=new ClientCardMatrix();
    }

    public ClientCardMatrix getCardMatrix() {
        return cardMatrix;
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

    public ClientMarket getMarket() { return market; }

    public void setUpSinglePlayer(){
        boards.get(currentNickname).getFaithPath().setLorenzoPosition(0);
    }

    public LeaderCard getLeaderCard(String name){
        for(LeaderCard l : leaderCards){
            if(l.getName().equals(name)){
                return l;
            }
        }
        return null;
    }
    public DevelopmentCard getDevelopmentCard(String name){
        for(DevelopmentCard d : developmentCards){
            if(d.getName().equals(name)) return d;
        }
        return null;
    }
    public String getDevelopmentCardStringified(String name){
        DevelopmentCard d=getDevelopmentCard(name);
        if(d!=null) return Utilities.stringify(d);
        return "";
    }
    public String getLeaderCardStringified(String name){
        LeaderCard l=getLeaderCard(name);
        if(l!=null) return Utilities.stringify(l);
        return "";
    }
    public String stringifyPlayers(){
        StringBuilder sb = new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape() + "PLAYERS: " + Color.RESET);
        for (String nick : playersInOrder) {
            sb.append("[ " +Color.ANSI_GREEN.escape()+ nick +Color.RESET+ " | " + boards.get(nick).getFaithPath().getPosition() + Color.ANSI_RED.escape() + "♰" + Color.RESET +" Cards:"+boards.get(nick).getTotalCardsBought() +" ],");
        }
        sb.deleteCharAt(sb.toString().length()-1);
        if(getCurrentBoard().getFaithPath().getLorenzoPosition()!=null){
            sb.append("[ "+Color.ANSI_GREEN.escape()+"LORENZO"+Color.RESET+" | "+getCurrentBoard().getFaithPath().getLorenzoPosition()+ Color.ANSI_RED.escape() + "♰" + Color.RESET+" Discarded Cards:"+getCardMatrix().getDiscardedCards()+" ]");
        }
        sb.append("\n");
        return sb.toString();
    }
    public ClientBoard getCurrentBoard(){
        return boards.get(currentNickname);
    }
    public String toString(){
        if(myNickname!=null) {
            StringBuilder sb = new StringBuilder();
            sb.append(stringifyPlayers());
            sb.append("════"+myNickname+" Board═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
            sb.append(boards.get(myNickname));
            sb.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
            sb.append(market);
            sb.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
            sb.append(cardMatrix);
            sb.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
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
            sb.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
            sb.append(boards.get(nick).getDeposits());
            System.out.println(sb.toString());
        }
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public Map<String, ClientBoard> getBoards() {
        return boards;
    }

    public String getCurrentNickname() {
        return currentNickname;
    }

    public ClientBoard getMyBoard(){
        return boards.get(myNickname);
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
        cm.boards.get("Da").getFaithPath().addToFaithPath(10);
        cm.boards.get("Riki").getFaithPath().addToFaithPath(2);
        //System.out.println(cm);
        cm.getBoards().get(cm.getNickname()).push(1,cm.developmentCards.get(1));
        cm.getBoards().get(cm.getNickname()).push(2,cm.developmentCards.get(10));
        cm.getBoards().get(cm.getNickname()).push(3,cm.developmentCards.get(12));
        cm.getBoards().get(cm.getNickname()).push(3,cm.developmentCards.get(2));
        cm.getBoards().get(cm.getNickname()).push(1,cm.developmentCards.get(40));

        cm.getMarket().initializeMarbles();
        cm.getBoards().get(cm.getNickname()).getLeadersInHand().add(cm.leaderCards.get(12));
        cm.getBoards().get(cm.getNickname()).getLeadersInHand().add(cm.leaderCards.get(0));
        cm.getBoards().get(cm.getNickname()).getLeadersInBoard().add(cm.leaderCards.get(8));
        cm.getBoards().get(cm.getNickname()).getLeadersInBoard().add(cm.leaderCards.get(15));

        System.out.println(cm);

        ClientCardMatrix ccm = new ClientCardMatrix();
        ccm.pushCard(0, 3, cm.developmentCards.get(1));
        ccm.pushCard(1, 1, cm.developmentCards.get(10));
        ccm.pushCard(1, 2, cm.developmentCards.get(12));
        ccm.pushCard(0, 1, cm.developmentCards.get(2));
        ccm.pushCard(2, 3, cm.developmentCards.get(40));
        ccm.pushCard(2, 1, cm.developmentCards.get(41));
        ccm.pushCard(2, 1, cm.developmentCards.get(32));
        ccm.pushCard(1, 1, cm.developmentCards.get(24));
        ccm.pushCard(2, 3, cm.developmentCards.get(27));
        ccm.pushCard(2, 1, cm.developmentCards.get(28));
        ccm.pushCard(2, 0, cm.developmentCards.get(30));
        ccm.pushCard(0, 2, cm.developmentCards.get(33));
        ccm.pushCard(1, 1, cm.developmentCards.get(15));
        System.out.println(ccm.toString());
/*
        System.out.println(Utilities.stringify(cm.leaderCards.get(12)));
        System.out.println(Utilities.stringify(cm.leaderCards.get(1)));
        System.out.println(Utilities.stringify(cm.leaderCards.get(4)));
        System.out.println(Utilities.stringify(cm.leaderCards.get(8)));

 */
    }

}
