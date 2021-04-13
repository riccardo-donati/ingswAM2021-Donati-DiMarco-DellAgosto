package it.polimi.ingsw.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.BoardObserver;
import it.polimi.ingsw.model.interfaces.Requirement;
import it.polimi.ingsw.model.interfaces.Token;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

public abstract class Game implements BoardObserver {
    protected static final Integer ROW=3;
    protected static final Integer COL=4;

    private Market market;
    private Stack<DevelopmentCard>[][] cardMatrix;
    private List<DevelopmentCard> developmentCards;
    private List<LeaderCard> leaderCards;
    private List<Player> players;
    private Player currPlayer;

    public List<Player> getPlayers() { return players; }

    public List<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public Stack<DevelopmentCard>[][] getCardMatrix() {
        return cardMatrix;
    }

    public Player getCurrPlayer() { return currPlayer; }

    public void pushBlackCross(Integer push){}

    public void tokenShuffle(){}

    public List<Token> getTokens(){return null;}

    /**
     * Constructor of the class Game where we initialize all the attributes
     */
    public Game()  {
        //initialization of the developmentCards from JSON
        developmentCards=new ArrayList<>();
        loadDevelopmentCardsFromJSON();
        //initialization of the leaderCards from JSON
        leaderCards=new ArrayList<>() ;
        loadLeaderCardsFromJSON();

        market=new Market();
        market.initializeMarket();
        initializeCardMatrix();
        this.players=new ArrayList<>();
        //-----
        gamePhase=GamePhase.NOTSTARTED;
    }
    /**
     * Using GSON we initialize the developmentCard list
     */
    public void loadDevelopmentCardsFromJSON() {
        Gson gson=new Gson();
        Type foundListType=new TypeToken<ArrayList<DevelopmentCard>>(){}.getType();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("src/main/java/it/polimi/ingsw/model/json/developmentCard.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("DevelopmentCard.json not found");
        }
        developmentCards=gson.fromJson(reader,foundListType);
    }
    /**
     * Using GSON we initialize the leaderCard list
     */
    public void loadLeaderCardsFromJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Requirement.class, new InterfaceAdapter<Requirement>());
        builder.registerTypeAdapter(SpecialAbility.class, new InterfaceAdapter<SpecialAbility>());
        Gson gson = builder.create();

        Type foundListType=new TypeToken<ArrayList<LeaderCard>>(){}.getType();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("src/main/java/it/polimi/ingsw/model/json/leaderCard.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("leaderCard.json not found");
        }
        leaderCards=gson.fromJson(reader,foundListType);
    }

    /**
     * Random push in the stacks of the cardMatrix for each DevelopmentCard
     * ROW 0: level 1 | ROW 1: level 2 | ROW 2: level 3
     * COL 0: GREEN | COL 1: BLUE | COL 2: YELLOW | COL 3: VIOLET
     */
    public void initializeCardMatrix(){
        cardMatrix=new Stack[ROW][COL];
        for(int r=0;r<ROW;r++){
            for(int c=0;c<COL;c++){
                cardMatrix[r][c]=new Stack<>();
            }
        }
        List<DevelopmentCard> copy=new ArrayList<>(developmentCards);
        Random rn=new Random();
        for(int i=0;i<developmentCards.size();i++){
            int n=rn.nextInt(copy.size());
            DevelopmentCard dc=copy.get(n);
            cardMatrix[dc.getLevel()-1][dc.getColor().ordinal()].push(dc);
            copy.remove(dc);
        }
    }

    /**
     * addition of a new player into the game
     * @param nickname of the new player
     */
    public void addPlayer(String nickname) throws FullGameException {

        if(nickname==""){
            throw new IllegalArgumentException("nickname is empty");
        }
        for(Player p : players){
            if(nickname.equals(p.getNickname())){
                throw new IllegalArgumentException("nickname already taken");
            }
        }
        Player newPlayer=new Player(nickname);
        //game registration as an observer
        newPlayer.getBoard().getFaithPath().addObserver(this);
        newPlayer.getBoard().addObserver(this);
        newPlayer.getBoard().getWarehouse().addObserver(this);
        players.add(newPlayer);

    }

    public void discardColor(Color toDiscard){ }

    public Result endGame(){
        Result result=new Result();
        for(Player p : players){
            result.addToResults(p.getNickname(),p.countPoints());//countPoints;
        }
        return result;
    }

    public void nextTurn(){}
    public void startGame() throws EmptyPlayersException, IllegalResourceException {
        if(players.size()==0) throw new EmptyPlayersException();
        Collections.shuffle(players);
        for(int i=0;i<players.size();i++){
            players.get(i).setOrder(i+1);
            if(i==1){
                players.get(i).getBoard().getWarehouse().addResourceInPending(ResourceType.UNKNOWN);
            }else if(i==2){
                players.get(i).getBoard().getWarehouse().addResourceInPending(ResourceType.UNKNOWN);
                players.get(i).getBoard().getFaithPath().addToPosition(1);
            }else if(i==3){
                players.get(i).getBoard().getWarehouse().addResourceInPending(ResourceType.UNKNOWN);
                players.get(i).getBoard().getWarehouse().addResourceInPending(ResourceType.UNKNOWN);
                players.get(i).getBoard().getFaithPath().addToPosition(1);
            }
        }
        currPlayer=players.get(0);
        gamePhase=GamePhase.SETUP;
        turnPhase=TurnPhase.STARTSETUPTURN;
    }
    public void chooseLeader(List<LeaderCard> l) throws NonEmptyException, IllegalLeaderCardsException, IllegalActionException {
        if(turnPhase==TurnPhase.STARTSETUPTURN) {
            currPlayer.chooseLeaders(l);
            turnPhase=TurnPhase.ENDSETUPTURN;
        }else throw new IllegalActionException();

    }
    public void passTurn() throws IllegalActionException {
        if(gamePhase==GamePhase.SETUP){
            if(turnPhase==TurnPhase.ENDSETUPTURN && currPlayer.getBoard().getWarehouse().getPendingResources().get(ResourceType.UNKNOWN)==0){
                if(currPlayer.getOrder()==players.size()){
                    gamePhase=GamePhase.ONGOING;
                    turnPhase=TurnPhase.STARTTURN;
                }else turnPhase=TurnPhase.STARTSETUPTURN;
                nextTurn();
            }else throw new IllegalActionException();
        }//else if gamephase is ONGOING
    }
    public void chooseResourceToDeposit(Integer id,ResourceType res) throws IllegalResourceException, FullSpaceException, UnknownNotFindException {
        if(gamePhase==GamePhase.SETUP){
            currPlayer.getBoard().getWarehouse().chooseResourceToDeposit(id,res);
        }
    }

    public GamePhase getGamePhase() { return gamePhase; }

    public TurnPhase getTurnPhase() { return turnPhase; }

    /**
     * for each player extract 4 leader cards and return them to the controller
     * @return the list of lists of 4 leader cards
     * @throws EmptyPlayersException if there aren't any player
     */
    public List<List<LeaderCard>> divideLeaderCards() throws EmptyPlayersException {
        if(players.size()==0) throw new EmptyPlayersException();
        List<LeaderCard> copyLeaders=new ArrayList<>(leaderCards);
        List<List<LeaderCard>> result=new ArrayList<>();
        Random r=new Random();
        for(int i=0;i<players.size();i++){
            result.add(new ArrayList<>());
            for(int j=0;j<4;j++){
                LeaderCard toAdd=copyLeaders.get(r.nextInt(copyLeaders.size()));
                result.get(i).add(toAdd);
                copyLeaders.remove(toAdd);
            }
        }
        return result;
    }

    @Override
    public void updateEndGame() {
        //non deve richiamare endgame ma deve cambiare la "booleana" di endgame
        endGame();
    }

    @Override
    public void updateDiscard(Warehouse wh) {
        for(Player p : players){
            if(!p.getBoard().getWarehouse().equals(wh))
                p.getBoard().getFaithPath().addToPosition(1);
        }
    }

    @Override
    public void updatePopeFavor() {
        for (Player p: players) {
            for (PopeFavor pf: p.getBoard().getFaithPath().getPopeFavorList()) {
                if(pf.getState().equals(PopeFavorState.UNACTIVE)){
                    if(pf.checkInside(p.getBoard().getFaithPath().getPosition())){
                        pf.changeState(PopeFavorState.ACTIVE);
                    }
                    else pf.changeState(PopeFavorState.DISCARDED);
                    break;
                }
            }
        }
    }
    public void buyAtMarket(char rc,int index){
        if(rc=='r'){
            try {
                market.getRow(index, currPlayer);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                return;
            }
        }else if(rc=='c'){
            try {
                market.getColumn(index, currPlayer);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                return;
            }
        }else throw new IllegalArgumentException("rc must be 'r' or 'c'" );
    }

    public FaithPath getBlackCrossFaithPath(){return null;}
    public void setCurrPlayer(Player currPlayer) { this.currPlayer = currPlayer; }

    //-------phases-------------------------------------------------------------
    private GamePhase gamePhase;
    private TurnPhase turnPhase;


}
