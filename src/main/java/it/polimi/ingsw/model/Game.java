package it.polimi.ingsw.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.*;
import it.polimi.ingsw.network.client.ClientModel.CLI.ClientPopeFavorState;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.server.GameObserver;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public abstract class Game implements BoardObserver, PublicInterface {
    protected static final Integer ROW=3;
    protected static final Integer COL=4;

    private Market market;
    private Stack<DevelopmentCard>[][] cardMatrix;
    private List<DevelopmentCard> developmentCards;
    private List<LeaderCard> leaderCards;
    private List<Player> players;
    private Player currPlayer;
    private GamePhase gamePhase;
    private TurnPhase turnPhase;
    private boolean endGameTrigger;

    private Map<String,LeaderCard> nameLeaderCardMap;
    private Map<String,DevelopmentCard> nameDevelopmentCardMap;
    //-------------------------
    GameObserver externalObserver;

    public void addExternalObserver(GameObserver go){
       externalObserver=go;
    }
    public void notifyEndGameTriggered(){
        if(externalObserver!=null)externalObserver.updateEndGameTriggered();
    }
    public void notifyPopeFavors(){
        if(externalObserver!=null)externalObserver.updatePopeFavors();
    }
    public void notifyEndGameResult(Result result){
        if(externalObserver!=null) externalObserver.updateEndGameResult(result);
    }
    //-------------------------


    protected boolean isEndGameTrigger() { return endGameTrigger; }
    protected List<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }
    protected List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }
    protected Player getCurrPlayer() { return currPlayer; }
    protected void pushBlackCross(Integer push){}
    protected void tokenShuffle(){}
    protected List<Token> getTokens(){return null;}
    protected FaithPath getBlackCrossFaithPath(){return null;}
    protected void setCurrPlayer(Player currPlayer) { this.currPlayer = currPlayer; }
    protected TurnPhase getTurnPhase() { return turnPhase; }
    protected Stack<Token> getTokenStack() {return  null; }
    protected void setGamePhase(GamePhase gamePhase) { this.gamePhase = gamePhase; }
    protected Market getMarket() { return market; }
    protected void setTurnPhase(TurnPhase turnPhase) { this.turnPhase = turnPhase; }

    /**
     * Reininitilize the cardMatrix for Test purposes
     */
    protected void initializeCardMatrixForTests(){
        Stack<DevelopmentCard>[][] newCardMatrix=new Stack[ROW][COL];
        for(int r=0;r<ROW;r++){
            for(int c=0;c<COL;c++){
                newCardMatrix[r][c]=new Stack<>();
            }
        }
        List<DevelopmentCard> copy=new ArrayList<>(developmentCards);
        for(int i=0;i<developmentCards.size();i++){
            DevelopmentCard dc=copy.get(0);
            newCardMatrix[dc.getLevel()-1][dc.getColor().ordinal()].push(dc);
            copy.remove(dc);
        }
        cardMatrix=newCardMatrix;
    }
    /**
     * market setter for testing purposes
     * @param market new market
     */
    protected void setMarket(Market market) {
        this.market = market;
    }

    /**
     * Constructor of the class Game where we initialize all the attributes
     */
    protected Game()  {
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
        endGameTrigger=false;
    }
    /**
     * Using GSON we initialize the developmentCard list
     */
    protected void loadDevelopmentCardsFromJSON() {
        nameDevelopmentCardMap=new HashMap<>();
        Gson gson=new Gson();
        Type foundListType=new TypeToken<ArrayList<DevelopmentCard>>(){}.getType();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("src/main/resources/json/developmentCard.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("DevelopmentCard.json not found");
        }
        developmentCards=gson.fromJson(reader,foundListType);
        for(DevelopmentCard dc :developmentCards){
            nameDevelopmentCardMap.put(dc.getName(),dc);
        }
    }

    /**
     * Using GSON we initialize the leaderCard list
     */
    protected void loadLeaderCardsFromJSON() {
        nameLeaderCardMap=new HashMap<>();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Requirement.class, new InterfaceAdapter<Requirement>());
        builder.registerTypeAdapter(SpecialAbility.class, new InterfaceAdapter<SpecialAbility>());
        Gson gson = builder.create();

        Type foundListType=new TypeToken<ArrayList<LeaderCard>>(){}.getType();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("src/main/resources/json/leaderCard.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("leaderCard.json not found");
        }
        leaderCards=gson.fromJson(reader,foundListType);
        for(LeaderCard ld :leaderCards){
            nameLeaderCardMap.put(ld.getName(),ld);
        }
    }

    /**
     *
     * @return a map with players and their popefavorstate
     */
    public Map<String,Map<Integer, ClientPopeFavorState>> getPopeFavors(){
        Map<String,Map<Integer, ClientPopeFavorState>> popeFavorsMap=new HashMap<>();
        for(Player p : players){
            Map<Integer,ClientPopeFavorState>  pfMap=new HashMap<>();
            int cont=1;
            for(PopeFavor pf : p.getBoard().getFaithPath().getPopeFavorList()){
                pfMap.put(cont,ClientPopeFavorState.valueOf(pf.getState().toString().toUpperCase()));
                cont++;
            }
            popeFavorsMap.put(p.getNickname(),pfMap);
        }
        return popeFavorsMap;
    }

    /**
     * Random push in the stacks of the cardMatrix for each DevelopmentCard
     * ROW 0: level 1 | ROW 1: level 2 | ROW 2: level 3
     * COL 0: GREEN | COL 1: BLUE | COL 2: YELLOW | COL 3: VIOLET
     */
    protected void initializeCardMatrix(){
        cardMatrix = new Stack[ROW][COL];
        for (int r = 0; r < ROW; r++){
            for (int c = 0; c < COL; c++){
                cardMatrix[r][c] = new Stack<>();
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
     * implemented in Singleplayer
     * @param toDiscard is the color of the card to discard
     */
    protected void discardColor(Color toDiscard){ }

    public void orderTokenStack(){}

    /**
     * implemented differently in single and multiplayer
     */
    protected void nextTurn(){}

    @Override
    public void updateEndGame() {
        endGameTrigger=true;
        notifyEndGameTriggered();
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
        if(players.size()>1)notifyPopeFavors();
    }

    /**
     * Get a row or a column of marbles at market
     * @param rc indicate row or column
     * @param index is the index of the row/column
     */
    protected void buyAtMarket(char rc,int index){
        if(rc=='r'){
            market.getRow(index, currPlayer);
        }else if(rc=='c'){
            market.getColumn(index, currPlayer);
        }else throw new IllegalArgumentException("rc must be 'r' or 'c'" );
    }

    //----------------PublicInterface----------------------------------------------------------------------
    //CONTROLLER:
    public Map<String, LeaderCard> getNameLeaderCardMap() { return nameLeaderCardMap; }
    public Map<String,DevelopmentCard> getNameDevelopmentCardMap(){return nameDevelopmentCardMap;}
    /**
     * usefulfor the controller
     * @return a map with the nickname and the faithpath position
     */
    public Map<String,Integer> getFaithPathsMap(){
        Map<String,Integer> map=new HashMap<>();
        for(Player p : players){
            map.put(p.getNickname(),p.getBoard().getFaithPath().getPosition());
        }
        return map;
    }
    public Warehouse getCurrentWarehouse(){
       return currPlayer.getBoard().getWarehouse();
    }
    public List<Production> getCurrentActiveProductions(){
        List<Production> productions=new ArrayList<>();
        if(currPlayer.getBoard().getBaseProduction().checkSelected()){
            productions.add(currPlayer.getBoard().getBaseProduction());
        }
        for(Production p : currPlayer.getExtraProductions()){
            if(p.checkSelected())productions.add(p);
        }
        for (Map.Entry<Integer, Stack<DevelopmentCard>> entry : currPlayer.getBoard().getSlots().entrySet()) {
            if(entry.getValue().size()>0){
                Production p=entry.getValue().get(entry.getValue().size()-1).getProd();
                if(p.checkSelected()){
                    productions.add(p);
                }
            }
        }
        return productions;
    }
    public List<ResourceType> getMarblesInList(){
        List<ResourceType> list=new ArrayList<>();
        Marble[][] marbles=market.getMarbles();
        for(int r=0;r<ROW;r++){
            for(int c=0;c<COL;c++){
                if(marbles[r][c] instanceof WhiteMarble ) list.add(ResourceType.WHITE);
                if(marbles[r][c] instanceof RedMarble ) list.add(ResourceType.RED);
                if(marbles[r][c] instanceof ResourceMarble ) {
                    ResourceMarble rs=(ResourceMarble)marbles[r][c];
                    list.add(rs.getType());
                }
            }
        }
        if(market.getPendingMarble() instanceof WhiteMarble)list.add(ResourceType.WHITE);
        if(market.getPendingMarble() instanceof RedMarble)list.add(ResourceType.RED);
        if(market.getPendingMarble() instanceof ResourceMarble) {
            ResourceMarble rs=(ResourceMarble)market.getPendingMarble();
            list.add(rs.getType());
        }
        return list;

    }
    /**
     * fot the move resource update
     * @return the spaces of the selected deposit
     */
    public ResourceType[] getDepositResources(int id){
        if(id<=3) {
            return currPlayer.getBoard().getWarehouse().getMaindepot().get(id-1).getSpace();
        }else{
            return currPlayer.getBoard().getWarehouse().getExtradepots().get(id-4).getSpace();
        }
    }
    public GamePhase getGamePhase() { return gamePhase; }
    /**
     * for the updates
     * @return the current player faithpath position
     */
    public Integer getCurrentFaithPath(){
        return currPlayer.getBoard().getFaithPath().getPosition();
    }

    /**
     * interface method for retrieving the position of lorenzo
     * @return the blackCrossFaith position
     */
    public Integer getLorenzoPosition(){
        if(getBlackCrossFaithPath()==null) return null;
        else return getBlackCrossFaithPath().getPosition();
    }
    /**
     * set the active parameter of a players in caso of disconnection/reconnection
     * @param player is the nickname of the player
     * @param active is the state that we want
     */
    public void setActive(String player,boolean active){
        for(Player p : players){
            if(p.getNickname().equals(player)){
                p.setActive(active);
            }
        }
    }
    public Stack<DevelopmentCard>[][] getCardMatrix(){
        return cardMatrix;
    }
    /**
     * useful fot the controller
     * @return the nickname of the current player
     */
    public String getCurrentNickname(){return currPlayer.getNickname();}

    /**
     * useful for the controller
     * @return a map that link the nicknames with the orders
     */
    public Map<String,Integer> getNickOrderMap(){
        Map<String,Integer> map=new HashMap<>();
        for(Player p : players){
            map.put(p.getNickname(),p.getOrder());
        }
        return  map;
    }

    /**
     *
     * @return the current player leadersInHand names
     */
    public List<String> getCurrentLeadersInHand(){
        List<String> listNamesLeaders=new ArrayList<>();
        for(LeaderCard l : currPlayer.getLeadersInHand()){
            listNamesLeaders.add(l.getName());
        }
        return listNamesLeaders;
    }
    /**
     * useful for the controller
     * @return a list of the ordered nicknames
     */
    public List<String> getListNickname(){
        List<String> list=new ArrayList<>();
        for(Player p : players){
            list.add(p.getNickname());
        }
        return list;
    }
    public List<Player> getPlayers() { return players; }
    /**
     * Give the players a random order and the related bonus resources
     * Set the currentPlayer and the SETUP phases
     * @throws GameNotFullException if the expected number of players is not met
     * @throws IllegalResourceException if UNKNOWN is illegal (it's not)
     */
    public void startGame() throws  IllegalResourceException, GameNotFullException {
        if(players.size()==0) throw new GameNotFullException();
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
    /**
     * compile a report of the statistics of the game with points and winner
     * @return the result of the game
     */
    public Result endGame(){
        Result result=new Result();
        for(Player p : players){
            result.addToResults(p.getNickname(), p.countPoints(), p.getBoard().countTotalResources());//countPoints;
        }
        return result;
    }
    /**
     * Factory method for the creation of the Game
     * @param nPlayers is the number of players
     * @return the Game instance
     * @throws IllegalPlayersNumberException if nPlayers<1 or >4
     */
    public static Game createGame(Integer nPlayers) throws IllegalPlayersNumberException {
        if(nPlayers==1) return new Singleplayer();
        else return new Multiplayer(nPlayers);
    }
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
    public Map<ResourceType,Integer> getCurrentStrongbox(){
        return currPlayer.getBoard().getStrongBox();
    }
    public Token getLastUsedToken(){
        return null;
    }
    /**
     * addition of a new player into the game
     * @param nickname of the new player
     * @throws FullGameException if the game is already full
     * @throws IllegalArgumentException if the nickname is illegal
     */
    public void addPlayer(String nickname) throws FullGameException,IllegalArgumentException {
        if(nickname.equals("")){
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

    public List<ResourceType> getCurrentPlayerPending() {
        return currPlayer.getBoard().getWarehouse().getPendingList();
    }

    //USER:
    //SetUpTurn
    public void chooseLeader(List<LeaderCard> l) throws NonEmptyException, IllegalLeaderCardsException, IllegalActionException {
        if(turnPhase==TurnPhase.STARTSETUPTURN) {
            currPlayer.chooseLeaders(l);
            turnPhase=TurnPhase.ENDSETUPTURN;
        }else throw new IllegalActionException();
    }

    public void chooseResourceToDeposit(Integer id,ResourceType res) throws IllegalActionException, FullSpaceException, UnknownNotFoundException, IllegalResourceException {
        if(gamePhase==GamePhase.SETUP && turnPhase==TurnPhase.ENDSETUPTURN){
            currPlayer.getBoard().getWarehouse().chooseResourceToDeposit(id,res);
        }else throw new IllegalActionException();
    }

    //NormalTurn
    public void playLeader(int index) throws CardNotAvailableException, RequirementNotMetException, IllegalActionException, IllegalResourceException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN || turnPhase==TurnPhase.ENDTURN))
            currPlayer.playLeader(currPlayer.getLeadersInHand().get(index));
        else throw new IllegalActionException();
    }

    public void discardLeader(int index) throws CardNotAvailableException, IllegalActionException, IndexOutOfBoundsException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN || turnPhase==TurnPhase.ENDTURN))
            currPlayer.discardLeader(currPlayer.getLeadersInHand().get(index));
        else throw new IllegalActionException();
    }

    public void buyAtMarketInterface(char rc,int index) throws IllegalActionException {
        if(gamePhase==GamePhase.ONGOING && turnPhase==TurnPhase.STARTTURN){
            buyAtMarket(rc,index);
            if(currPlayer.getBoard().getWarehouse().getPendingResources().values().stream().allMatch(i -> i == 0))
                turnPhase=TurnPhase.ENDTURN;
            else turnPhase=TurnPhase.DEPOSITPHASE;
        }else throw new IllegalActionException();
    }

    public void depositResource(Integer id,ResourceType res) throws IllegalActionException, FullSpaceException, IllegalResourceException {
        if(gamePhase==GamePhase.ONGOING && turnPhase==TurnPhase.DEPOSITPHASE){
            currPlayer.getBoard().getWarehouse().addResourceInDeposit(id,res);
            if(currPlayer.getBoard().getWarehouse().getPendingResources().values().stream().allMatch(i -> i == 0)){
                turnPhase=TurnPhase.ENDTURN;
            }
        }else throw new IllegalActionException();
    }

    public void discardResource(ResourceType res) throws IllegalActionException, IllegalResourceException, DepositableResourceException {
        if(gamePhase==GamePhase.ONGOING && turnPhase==TurnPhase.DEPOSITPHASE){
            currPlayer.getBoard().getWarehouse().discardResource(res);
            if(currPlayer.getBoard().getWarehouse().getPendingResources().values().stream().allMatch(i -> i == 0)){
                turnPhase=TurnPhase.ENDTURN;
            }
        }else throw new IllegalActionException();
    }

    public void transformWhiteIn(ResourceType res) throws IllegalActionException, IllegalResourceException, NoWhiteResourceException {
        if(gamePhase==GamePhase.ONGOING && turnPhase==TurnPhase.DEPOSITPHASE) {
            currPlayer.transformWhiteIn(res);
        }else throw new IllegalActionException();
    }

    public void substituteUnknownInInputBaseProduction(ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN || turnPhase==TurnPhase.PICKUPPHASE)){
            currPlayer.getBoard().getBaseProduction().replaceUnknownInput(res);
        }else throw new IllegalActionException();
    }

    public void substituteUnknownInOutputBaseProduction(ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException {
        if(gamePhase==GamePhase.ONGOING &&  (turnPhase==TurnPhase.STARTTURN || turnPhase==TurnPhase.PICKUPPHASE)){
            currPlayer.getBoard().getBaseProduction().replaceUnknownOutput(res);
        }else throw new IllegalActionException();
    }

    public void substituteUnknownInInputExtraProduction(Integer index,ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException {
        if(gamePhase==GamePhase.ONGOING &&  (turnPhase==TurnPhase.STARTTURN || turnPhase==TurnPhase.PICKUPPHASE)){
            currPlayer.getExtraProductions().get(index).replaceUnknownInput(res);
        }else throw new IllegalActionException();
    }

    public void substituteUnknownInOutputExtraProduction(Integer index,ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException {
        if(gamePhase==GamePhase.ONGOING &&  (turnPhase==TurnPhase.STARTTURN || turnPhase==TurnPhase.PICKUPPHASE)){
            currPlayer.getExtraProductions().get(index).replaceUnknownOutput(res);
        }else throw new IllegalActionException();
    }

    public void toggleBaseProd() throws UnknownFoundException, IllegalActionException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN ||turnPhase==TurnPhase.PICKUPPHASE)) {
            currPlayer.getBoard().getBaseProduction().toggleSelected();
            if(currPlayer.countSelectedProductions()==0){
                turnPhase=TurnPhase.STARTTURN;
            }else turnPhase=TurnPhase.PICKUPPHASE;
        }else throw new IllegalActionException();
    }

    public void toggleExtraProd(Integer index) throws UnknownFoundException, IllegalActionException, IndexOutOfBoundsException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN ||turnPhase==TurnPhase.PICKUPPHASE)) {
            currPlayer.getExtraProductions().get(index).toggleSelected();
            if(currPlayer.countSelectedProductions()==0){
                turnPhase=TurnPhase.STARTTURN;
            }else turnPhase=TurnPhase.PICKUPPHASE;
        }else throw new IllegalActionException();
    }

    public void toggleCardProd(Integer slot) throws UnknownFoundException, IllegalActionException, IllegalSlotException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN ||turnPhase==TurnPhase.PICKUPPHASE)) {
            Stack<DevelopmentCard> tmp=currPlayer.getBoard().getSlots().get(slot);
            if(tmp.size()>0){
                tmp.get(tmp.size()-1).getProd().toggleSelected();
            }else throw new IllegalSlotException();
            if(currPlayer.countSelectedProductions()==0){
                turnPhase=TurnPhase.STARTTURN;
            }else turnPhase=TurnPhase.PICKUPPHASE;
        }else throw new IllegalActionException();
    }

    public void pickUpResourceFromWarehouse(Integer id) throws IllegalActionException, ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN ||turnPhase==TurnPhase.PICKUPPHASE)) {
            currPlayer.pickUpResourceFromWarehouse(id);
            turnPhase=TurnPhase.PICKUPPHASE;
        }else throw new IllegalActionException();
    }

    public void pickUpResourceFromStrongbox(ResourceType res) throws IllegalActionException, ResourcesNotAvailableException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN ||turnPhase==TurnPhase.PICKUPPHASE)) {
            currPlayer.pickUpResourceFromStrongbox(res);
            turnPhase=TurnPhase.PICKUPPHASE;
        }else throw new IllegalActionException();
    }

    public void revertPickUp() throws IllegalActionException, FullSpaceException, IllegalResourceException {
        if(gamePhase==GamePhase.ONGOING && turnPhase==TurnPhase.PICKUPPHASE) {
            currPlayer.revertPickUp();
            turnPhase=TurnPhase.STARTTURN;
        }else throw new IllegalActionException();
    }

    public void activateProductions() throws IllegalResourceException, ResourcesNotAvailableException, TooManyResourcesException, UnknownFoundException, IllegalActionException {
        if(gamePhase==GamePhase.ONGOING && turnPhase==TurnPhase.PICKUPPHASE){
            currPlayer.activateProductions();
            turnPhase=TurnPhase.ENDTURN;
        }else throw new IllegalActionException();
    }

    public void buyCard(Integer row,Integer col,Integer slot) throws ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException, IllegalActionException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.PICKUPPHASE || turnPhase==TurnPhase.STARTTURN && currPlayer.countActivatedDiscounts()>0)){
            //you can buy in STARTTURN only if you have a selected discounts
            if(cardMatrix[row][col].size()>0) {
                currPlayer.buyCard(cardMatrix[row][col].get(cardMatrix[row][col].size()-1), slot);
                cardMatrix[row][col].pop();
                turnPhase = TurnPhase.ENDTURN;
            }else throw new EmptyStackException();
        }else throw new IllegalActionException();

    }

    public void moveResource(Integer dep1,Integer dep2) throws IllegalActionException, IllegalResourceException, FullSpaceException, NonEmptyException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN ||turnPhase==TurnPhase.DEPOSITPHASE ||turnPhase==TurnPhase.ENDTURN)) {
            currPlayer.getBoard().getWarehouse().moveResource(dep1,dep2);
        }else throw new IllegalActionException();
    }
    public void toggleDiscount(ResourceType res) throws IllegalActionException, DiscountNotFoundException {
        if(gamePhase==GamePhase.ONGOING && (turnPhase==TurnPhase.STARTTURN ||turnPhase==TurnPhase.PICKUPPHASE)){
            currPlayer.toggleDiscount(res);
        }else throw new IllegalActionException();
    }

    //GenericTurn
    public void passTurn() throws IllegalActionException {
        if(gamePhase==GamePhase.SETUP){
            if(turnPhase==TurnPhase.ENDSETUPTURN && currPlayer.getBoard().getWarehouse().getPendingResources().get(ResourceType.UNKNOWN)==0){
                if(currPlayer.getOrder()==players.size()){
                    gamePhase=GamePhase.ONGOING;
                    turnPhase=TurnPhase.STARTTURN;
                }else turnPhase=TurnPhase.STARTSETUPTURN;
                if(players.size()>1)
                    nextTurn();
            }else throw new IllegalActionException();
        }else if(gamePhase==GamePhase.ONGOING){
            if(turnPhase==TurnPhase.ENDTURN){
                if(currPlayer.getOrder()==players.size() && endGameTrigger){
                    gamePhase=GamePhase.ENDGAME;
                    Result result=endGame();
                    notifyEndGameResult(result);
                }
                currPlayer.resetProductions();
                turnPhase=TurnPhase.STARTTURN;
                nextTurn();
            }else throw new IllegalActionException();
        }
    }
    //-----------------------------------------------------------------------------------------------------
    protected void saveGameStateOnJson(String name) {
        for(Player p : players){
            p.getBoard().getWarehouse().getObservers().clear();
            p.getBoard().getObservers().clear();
            p.getBoard().getFaithPath().getObserver().clear();
        }
        if(getBlackCrossFaithPath()!=null){
            getBlackCrossFaithPath().getObserver().clear();
        }
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Requirement.class, new InterfaceAdapter<Requirement>());
        builder.registerTypeAdapter(SpecialAbility.class, new InterfaceAdapter<SpecialAbility>());
        builder.registerTypeAdapter(Token.class, new InterfaceAdapter<Token>());
        builder.registerTypeAdapter(Marble.class, new InterfaceAdapter<Marble>());
        builder.registerTypeAdapter(BoardObserver.class, new InterfaceAdapter<BoardObserver>());
        builder.registerTypeAdapter(Game.class, new InterfaceAdapter<Game>());

        Gson gson = builder.create();
        String gameGson=gson.toJson(this);
        FileWriter w;
        try {
            w=new FileWriter("src/main/resources/json/gameState/"+name+".json");
            w.write(gameGson);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Player p : players){
            p.getBoard().getWarehouse().addObserver(this);
            p.getBoard().addObserver(this);
            p.getBoard().getFaithPath().addObserver(this);
        }
        if(getBlackCrossFaithPath()!=null){
            getBlackCrossFaithPath().addObserver(this);
        }
    }

}

