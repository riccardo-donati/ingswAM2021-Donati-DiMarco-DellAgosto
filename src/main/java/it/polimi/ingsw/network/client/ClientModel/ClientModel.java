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
    private final List<String> disconnectedPlayers = new ArrayList<>();

    // phases
    private TurnPhase turnPhase;
    private GamePhase gamePhase;

    private final ClientMarket market;
    private final ClientCardMatrix cardMatrix;

    /**
     * loading of the leader and development cards and generic initialization
     */
    public ClientModel() {
        leaderCards = Utilities.loadLeaderCardsFromJSON();
        developmentCards = Utilities.loadDevelopmentCardsFromJSON();

        assert developmentCards != null;
        for (DevelopmentCard card : developmentCards)
            nameDevelopmentMap.put(card.getName(), card);

        assert leaderCards != null;
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

    /**
     * load the cardMatrix from a matrix of cards name
     * @param stringCards is a matrix of Stack of cards name
     */
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

    /**
     *
     * @return a readable string of the players
     */
    public String stringifyPlayers() {
        StringBuilder sb = new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape()).append("PLAYERS: ").append(Color.RESET);
        for (String nick : playersInOrder) {
            sb.append("[ ").append(Color.ANSI_GREEN.escape()).append(nick).append(Color.RESET).append(" | ").append(boards.get(nick).getFaithPath().getPosition()).append(Color.ANSI_RED.escape()).append("♰").append(Color.RESET).append(" Cards:").append(boards.get(nick).getTotalCardsBought()).append(" ],");
        }
        sb.deleteCharAt(sb.toString().length()-1);
        if(getCurrentBoard().getFaithPath().getLorenzoPosition() != null){
            sb.append("[ ").append(Color.ANSI_GREEN.escape()).append("LORENZO").append(Color.RESET).append(" | ").append(getCurrentBoard().getFaithPath().getLorenzoPosition()).append(Color.ANSI_RED.escape()).append("♰").append(Color.RESET).append(" Discarded Cards:").append(getCardMatrix().getDiscardedCards()).append(" ]");
        }
        sb.append("\n");
        return sb.toString();
    }

    public ClientBoard getCurrentBoard() {
        return boards.get(currentNickname);
    }

    public String toString() {
        if (myNickname != null) {
            return stringifyPlayers() +
                    "════" + myNickname + " Board═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n" +
                    boards.get(myNickname) +
                    "═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n" +
                    market +
                    "═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n" +
                    cardMatrix +
                    "═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n";
        }
        return "";
    }

    /**
     * print an ASCII ART per i depositi
     * @param nick
     */
    public void visualizeDeposits(String nick) {
        if(boards.get(nick)!=null){
            String sb = "═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n" +
                    boards.get(nick).getDeposits();
            System.out.println(sb);
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

}
