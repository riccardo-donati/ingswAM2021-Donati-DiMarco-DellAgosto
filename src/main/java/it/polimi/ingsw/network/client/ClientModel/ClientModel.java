package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.CLI.enums.Color;

import java.util.*;

public class ClientModel {

    // maps
    // nickname -> board
    private final Map<String, ClientBoard> boards = new HashMap<>();
    // name -> development card
    public final Map<String, DevelopmentCard> nameDevelopmentMap = new HashMap<>();
    // name -> leader card
    public final Map<String, LeaderCard> nameLeaderMap = new HashMap<>();

    // cards
    private final List<DevelopmentCard> developmentCards;
    private final List<LeaderCard> leaderCards;
    private final List<String> setupPhaseLeaderCards = new ArrayList<>();

    // players
    private List<String> playersInOrder;
    private String myNickname;
    private String currentNickname;
    private List<String> disconnectedPlayers=new ArrayList<>();

    // phases
    private TurnPhase turnPhase;
    private GamePhase gamePhase;

    private final ClientMarket market;
    private final ClientCardMatrix cardMatrix;


    public ClientModel() {
        leaderCards = Utilities.loadLeaderCardsFromJSON();
        developmentCards = Utilities.loadDevelopmentCardsFromJSON();

        for (DevelopmentCard card : developmentCards)
            nameDevelopmentMap.put(card.getName(), card);

        for (LeaderCard card : leaderCards)
            nameLeaderMap.put(card.getName(), card);

        market = new ClientMarket();
        cardMatrix = new ClientCardMatrix();
    }

    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    public ClientCardMatrix getCardMatrix() {
        return cardMatrix;
    }

    public List<String> getPlayersInOrder() {
        return playersInOrder;
    }

    public List<String> getSetupPhaseLeaderCards() {
        return setupPhaseLeaderCards;
    }

    public void addDisconnected(String nickname){disconnectedPlayers.add(nickname);}

    public void removeDisconnected(String nickname){disconnectedPlayers.remove(nickname);}

    public List<String> getDisconnectedPlayers() { return disconnectedPlayers; }

    public String getNickname() {
        return myNickname;
    }

    public ClientMarket getMarket() {
        return market;
    }

    public LeaderCard getLeaderCard(String name){
        return nameLeaderMap.get(name);
    }

    public List<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
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

    public void setNickname(String nickname) {
        this.myNickname = nickname;
    }

    public void setUpSinglePlayer(){
        boards.get(currentNickname).getFaithPath().setLorenzoPosition(0);
    }

    public void putBoard(String nickname, ClientBoard cb) {
        boards.put(nickname, cb);
    }

    public void addSetupPhaseLeaderCard(String name) {
        setupPhaseLeaderCards.add(name);
    }

    public void loadCardMatrixFromNames(Stack<String>[][] stringCards) {
        Stack<DevelopmentCard>[][] cards = new Stack[3][4];
        for(int r=0;r<3;r++){
            for(int c=0;c<4;c++){
                cards[r][c]=new Stack<>();
                for(int i=0;i<stringCards[r][c].size();i++) {
                    String name=stringCards[r][c].get(i);
                    if(name!=null) {
                        DevelopmentCard d=nameDevelopmentMap.get(name);
                        if(d!=null) cards[r][c].push(d);
                    }
                }
            }
        }
        cardMatrix.setCards(cards);
    }

    public DevelopmentCard getDevelopmentCard(String name) {
        return nameDevelopmentMap.get(name);
    }

    public String stringifyLeaderCardFromName(String card) {
        if(card != null)
            return Utilities.stringify(nameLeaderMap.get(card));
        else return "";
    }

    public String stringifyPlayers() {
        StringBuilder sb = new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape() + "PLAYERS: " + Color.RESET);
        for (String nick : playersInOrder) {
            sb.append("[ " +Color.ANSI_GREEN.escape()+ nick +Color.RESET+ " | " + boards.get(nick).getFaithPath().getPosition() + Color.ANSI_RED.escape() + "♰" + Color.RESET +" Cards:"+boards.get(nick).getTotalCardsBought() +" ],");
        }
        sb.deleteCharAt(sb.toString().length()-1);
        if(getCurrentBoard().getFaithPath().getLorenzoPosition() != null){
            sb.append("[ "+Color.ANSI_GREEN.escape()+"LORENZO"+Color.RESET+" | "+getCurrentBoard().getFaithPath().getLorenzoPosition()+ Color.ANSI_RED.escape() + "♰" + Color.RESET+" Discarded Cards:"+getCardMatrix().getDiscardedCards()+" ]");
        }
        sb.append("\n");
        return sb.toString();
    }

    public ClientBoard getCurrentBoard() {
        return boards.get(currentNickname);
    }

    public String toString() {
        if (myNickname != null) {
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

    public void visualizeBoard(String nick) {
        if(boards.get(nick)!=null) {
            StringBuilder sb = new StringBuilder();
            sb.append("════" + nick + " Board═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
            sb.append(boards.get(nick));
            System.out.println(sb.toString());
        }
    }

    public void visualizeDeposits(String nick) {
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

    public ClientBoard getMyBoard() {
        return boards.get(myNickname);
    }

    /*
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

    }
    */

}
