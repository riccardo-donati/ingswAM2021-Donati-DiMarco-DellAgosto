package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.PublicInterface;
import it.polimi.ingsw.model.interfaces.Token;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.ClientPopeFavorState;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.*;

import java.util.*;

public class Controller implements GameObserver {
    @Expose
    private int idLobby;
    @Expose
    private int nPlayers;
    @Expose
    private List<VirtualClient> players = new ArrayList<>();
    @Expose
    private static int globalID = 0;
    private Gson gson;
    @Expose
    GamePhase gameState;
    private PublicInterface game;
    private List<List<LeaderCard>> lists;

    private Server server;

    //-----------------
    public synchronized int getnPlayers() {
        return nPlayers;
    }

    public synchronized static void setGlobalID(int id) {
        Controller.globalID = id;
    }

    public synchronized static int getGlobalID() {
        return globalID;
    }

    public synchronized int getIdLobby() {
        return idLobby;
    }

    public synchronized List<String> getNames(){
        List<String> names = new ArrayList<>();
        for(VirtualClient vc : players){
            names.add(vc.getNickname());
        }
        return names;
    }

    public synchronized   List<VirtualClient> getPlayersInLobby() { return players; }

    public synchronized boolean isFull(){
        return nPlayers == players.size();
    }

    public Controller(int nPlayers, VirtualClient firstPlayer,Server server){
        this.idLobby = globalID;
        globalID++;
        this.nPlayers=nPlayers;
        this.players.add(firstPlayer);
        gson= Utilities.initializeGsonMessage();
        gameState=GamePhase.NOTSTARTED;

        this.server=server;
    }

    public synchronized void setGson(Gson gson) {
        this.gson = gson;
    }

    public synchronized void notifyLobby(Message message){
        for(VirtualClient virtualClient : players){
            if(virtualClient.getClientHandler() != null) {
                virtualClient.getClientHandler().send(message);
            }
        }
    }

    public synchronized void removePlayer(VirtualClient player){
        players.remove(player);
    }

    public synchronized List<String> getNicknames(){
        List<String> nicknames=new ArrayList<>();
        for(VirtualClient vc : players){
            nicknames.add(vc.getNickname());
        }
        return nicknames;
    }

    public synchronized void setGameState(GamePhase gamePhase) {
        this.gameState = gamePhase;
    }

    public synchronized List<String> getPlayerLeaderCardList(String nick){
        List<String> list=new ArrayList<>();
        Map<String,Integer> nom=getNickOrderMap();
        for(LeaderCard ld : lists.get(nom.get(nick)-1)){
            list.add(ld.getName());
        }
        return list;
    }

    public synchronized List<String> getOrderPlayerList() { return game.getListNickname(); }
    public synchronized Map<String,Map<Integer, ClientPopeFavorState>> getPopeFavors(){return game.getPopeFavors();}
    /**
     * create the instance of Game
     * @param numberOfPlayers is the number of the lobby's players
     */
    public synchronized void initializeGame(int numberOfPlayers){
        if(game!=null){
            System.out.println("Game already initialized!");
            return;
        }
        try {
            game=Game.createGame(numberOfPlayers);
            game.addExternalObserver(this);
        } catch (IllegalPlayersNumberException e) {
            System.out.println("Game not initialized because numberOfPlayers is an illegal value!");
        }
    }

    /**
     *
     * @param nickname is the nickcname of the virtual client
     * @return the virtual client with the nickname or null
     */
    public synchronized VirtualClient getVirtualClient(String nickname){
        for(VirtualClient vc : players){
            if(vc.getNickname().equals(nickname)){
                return vc;
            }
        }
        return null;
    }
    /**
     * add the players to the Game instance
     * @param nicknames is a list of nicknames
     */
    public synchronized void addPlayers(List<String> nicknames){
        if(nicknames!=null){
            for(String nick : nicknames){
                try {
                    game.addPlayer(nick);
                } catch (FullGameException e) {
                    System.out.println("The game is full, not all nicknames added");
                } catch (IllegalArgumentException e){
                    System.out.println(e.getMessage());
                } catch (NullPointerException e){
                    System.out.println("Game not initialized");
                    return;
                }
            }
        }
    }
    /**
     * start the game and notify the players with the messages
     */
    public synchronized void start(){
        notifyLobby(new GenericMessage("Game starting . . ."));
        initializeGame(nPlayers);
        addPlayers(getNicknames());
        startGame();
        //gamePhase= GamePhase.SETUP;
        lists=divideLeaderCards();
        if(lists.size()!=players.size()){
            System.out.println("Internal Error, please reinitialize the game");
            return;
        }
        notifyLobby(new GenericMessage("Game started!"));
        for(VirtualClient vc : players){
            List<String> l=getPlayerLeaderCardList(vc.getNickname());
            Message m=new StartGameUpdate(getOrderPlayerList(),l,getFaithPathsMap(),getMarblesInList(),getCardMatrix());
            vc.send(m);
        }
    }

    public synchronized void addPlayerInLobby(VirtualClient vc){
        if(vc!=null)
            players.add(vc);
    }

    //interface try catch?
    public synchronized Stack<DevelopmentCard>[][] getCardMatrix(){return game.getCardMatrix();}
    public synchronized Map<String, LeaderCard> getNameLeaderCardMap(){ return game.getNameLeaderCardMap(); }
    public synchronized Map<String,DevelopmentCard> getNameDevelopmentCardMap(){return game.getNameDevelopmentCardMap();}
    public synchronized Map<String,Integer> getFaithPathsMap(){
        return game.getFaithPathsMap();
    }
    public synchronized List<ResourceType> getMarblesInList(){
        return game.getMarblesInList();
    }
    public synchronized ResourceType[] getDepositResources(int id){
        return game.getDepositResources(id);
    }
    public synchronized GamePhase getGamePhase(){
        return game.getGamePhase();
    }
    public synchronized void startGame(){
        try {
            game.startGame();
        } catch (IllegalResourceException e) {
            System.out.println("Internal error, please reinitialize the game");
        } catch (GameNotFullException e) {
            System.out.println("Can't start the game because it's not full! Add more players");
        } catch (NullPointerException e){
            System.out.println("Game not initialized");
            return;
        }
    }
    public synchronized Integer getCurrentFaithPath(){
        return game.getCurrentFaithPath();
    }
    public synchronized Integer getLorenzoPosition(){
        return game.getLorenzoPosition();
    }
    public synchronized void setActive(String player,boolean active){
        game.setActive(player,active);
    }
    public synchronized String getCurrentNickname(){
        return game.getCurrentNickname();
    }
    public synchronized Map<String,Integer> getNickOrderMap(){
        return game.getNickOrderMap();
    }
    public synchronized List<String> getListNickname(){
        return game.getListNickname();
    }
    public synchronized List<Player> getPlayers(){ return game.getPlayers(); }
    public synchronized Result endGame(){
        return game.endGame();
    }
    public synchronized Token getLastUsedToken(){
        return game.getLastUsedToken();
    }
    public synchronized List<List<LeaderCard>> divideLeaderCards(){
        try {
            return game.divideLeaderCards();
        } catch (EmptyPlayersException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public synchronized void addPlayer(String nickname){
        try {
            game.addPlayer(nickname);
        } catch (FullGameException e) {
            e.printStackTrace();
        }
    }
    public synchronized void clearPlayer(String nickname){
        game.clearPlayer(nickname);
    }
    public synchronized List<ResourceType> getCurrentPlayerPending(){
        return game.getCurrentPlayerPending();
    }
    //COMMANDS
    public synchronized void chooseLeader(List<String> l,String nickname) throws IllegalLeaderCardsException, IllegalActionException, NonEmptyException, NotYourTurnException, IllegalCommandException {
        if(getCurrentNickname().equals(nickname)) {
            Map<String, LeaderCard> map = getNameLeaderCardMap();
            List<LeaderCard> list = new ArrayList<>();
            list.add(map.get(l.get(0)));
            list.add(map.get(l.get(1)));
            for(LeaderCard lc : list){
                if(lc==null)throw new IllegalCommandException();
            }
            game.chooseLeader(list);
            //update
            getVirtualClient(nickname).send(new LeadersInHandUpdate(getCurrentLeadersInHand()));
            int nBonus=getNickOrderMap().get(nickname);
            getVirtualClient(nickname).send(new BonusResourceMessage(nBonus));

        }
        else throw new NotYourTurnException();
    }
    public synchronized List<String> getCurrentLeadersInHand(){
        return game.getCurrentLeadersInHand();
    }
    public synchronized void chooseResourceToDeposit(Integer id,ResourceType res,String nickname) throws NotYourTurnException, UnknownNotFoundException, IllegalActionException, IllegalResourceException, FullSpaceException {
        if(getCurrentNickname().equals(nickname)) {
            game.chooseResourceToDeposit(id, res);
            //update
            notifyLobby(new DepositUpdate(id,Utilities.resourceTypeToResource(res)));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void playLeader(int index,String nickname) throws NotYourTurnException, IllegalResourceException, IllegalActionException, RequirementNotMetException, CardNotAvailableException {
        if(getCurrentNickname().equals(nickname)) {
            game.playLeader(index);
            //update
            VirtualClient vc = getVirtualClient(nickname);
            if(vc!=null) vc.send(new PlayLeaderUpdate(index));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void discardLeader(int index,String nickname) throws IllegalActionException, CardNotAvailableException, NotYourTurnException {
        if (getCurrentNickname().equals(nickname)) {
            game.discardLeader(index);
            notifyLobby(new DiscardLeaderUpdate(index));
            notifyLobby(new FaithUpdate(getCurrentFaithPath()));
        } else throw new NotYourTurnException();
    }
    public synchronized void buyAtMarketInterface(char rc,int index,String nickname) throws NotYourTurnException, IllegalActionException {
        if(getCurrentNickname().equals(nickname)) {
            game.buyAtMarketInterface(rc, index);
            //update
            List<ResourceType> list=getCurrentPlayerPending();
            notifyLobby(new FaithUpdate(getCurrentFaithPath()));
            notifyLobby(new MarketUpdate(getMarblesInList()));
            if(list.size()>0)
                getVirtualClient(nickname).send(new PendingResourcesMessage(list));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void depositResource(Integer id,ResourceType res,String nickname) throws NotYourTurnException, IllegalResourceException, FullSpaceException, IllegalActionException {
        if(getCurrentNickname().equals(nickname)) {
            game.depositResource(id, res);
            //update
            notifyLobby(new DepositUpdate(id, Utilities.resourceTypeToResource(res)));
            List<ResourceType> list=getCurrentPlayerPending();
            if(list.size()>0) {
                VirtualClient vc = getVirtualClient(nickname);
                if(vc!=null) vc.send(new PendingResourcesMessage(list));
            }
        }
        else throw new NotYourTurnException();
    }
    public synchronized void discardResource(ResourceType res,String nickname) throws NotYourTurnException, IllegalResourceException, DepositableResourceException, IllegalActionException {
        if(getCurrentNickname().equals(nickname)) {
            game.discardResource(res);
            notifyLobby(new DiscardResourceUpdate());
            List<ResourceType> list=getCurrentPlayerPending();
            if(list.size()>0) {
                VirtualClient vc = getVirtualClient(nickname);
                if(vc!=null) vc.send(new PendingResourcesMessage(list));
            }
        }else throw new NotYourTurnException();
    }
    public synchronized void transformWhiteIn(ResourceType res,String nickname) throws IllegalResourceException, IllegalActionException, NoWhiteResourceException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.transformWhiteIn(res);
            List<ResourceType> list=getCurrentPlayerPending();
            if(list.size()>0) {
                VirtualClient vc = getVirtualClient(nickname);
                if(vc!=null) vc.send(new PendingResourcesMessage(list));
            }
        }
        else throw new NotYourTurnException();
    }
    public synchronized void substituteUnknownInInputBaseProduction(ResourceType res,String nickname) throws UnknownNotFoundException, IllegalResourceException, IllegalActionException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.substituteUnknownInInputBaseProduction(res);
            VirtualClient vc = getVirtualClient(nickname);
            if(vc!=null) vc.send(new UnknownProductionUpdate(-1,res,'i'));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void substituteUnknownInOutputBaseProduction(ResourceType res,String nickname) throws NotYourTurnException, UnknownNotFoundException, IllegalResourceException, IllegalActionException {
        if(getCurrentNickname().equals(nickname)) {
            game.substituteUnknownInOutputBaseProduction(res);
            //update
            VirtualClient vc = getVirtualClient(nickname);
            if(vc!=null) vc.send(new UnknownProductionUpdate(-1,res,'o'));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void substituteUnknownInInputExtraProduction(Integer index,ResourceType res,String nickname) throws NotYourTurnException, UnknownNotFoundException, IllegalResourceException, IllegalActionException {
        if(getCurrentNickname().equals(nickname)) {
            game.substituteUnknownInInputExtraProduction(index, res);
            //update
            VirtualClient vc = getVirtualClient(nickname);
            if(vc!=null) vc.send(new UnknownProductionUpdate(index,res,'i'));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void substituteUnknownInOutputExtraProduction(Integer index,ResourceType res,String nickname) throws UnknownNotFoundException, IllegalResourceException, IllegalActionException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.substituteUnknownInOutputExtraProduction(index, res);
            VirtualClient vc = getVirtualClient(nickname);
            vc.send(new UnknownProductionUpdate(index,res,'o'));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void toggleBaseProd(String nickname) throws UnknownFoundException, IllegalActionException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.toggleBaseProd();
            VirtualClient vc = getVirtualClient(nickname);
            vc.send(new ToggleProductionUpdate(getCurrentActiveProductions()));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void toggleExtraProd(Integer index,String nickname) throws UnknownFoundException, IllegalActionException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.toggleExtraProd(index);
            VirtualClient vc = getVirtualClient(nickname);
            vc.send(new ToggleProductionUpdate(getCurrentActiveProductions()));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void toggleCardProd(Integer slot,String nickname) throws IllegalActionException, IllegalSlotException, UnknownFoundException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.toggleCardProd(slot);
            VirtualClient vc = getVirtualClient(nickname);
            if(vc!=null)vc.send(new ToggleProductionUpdate(getCurrentActiveProductions()));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void pickUpResourceFromWarehouse(Integer id,String nickname) throws IllegalActionException, ResourcesNotAvailableException, NonEmptyException, DepositNotExistingException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.pickUpResourceFromWarehouse(id);
            notifyLobby(new PickUpWarehouseUpdate(id));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void pickUpResourceFromStrongbox(ResourceType res,String nickname) throws IllegalActionException, ResourcesNotAvailableException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.pickUpResourceFromStrongbox(res);
            notifyLobby(new PickUpStrongboxUpdate(Utilities.resourceTypeToResource(res)));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void revertPickUp(String nickname) throws IllegalResourceException, FullSpaceException, IllegalActionException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.revertPickUp();
            VirtualClient vc = getVirtualClient(nickname);
            if(vc!=null)vc.send(new DepositsUpdate(getCurrentWarehouse(),getCurrentStrongbox()));

        }
        else throw new NotYourTurnException();
    }
    public synchronized List<ClientDeposit> getCurrentWarehouse(){
        List<ClientDeposit> list=new ArrayList<>();
        Warehouse wh=game.getCurrentWarehouse();
        for(Deposit d : wh.getMaindepot()){
            ClientDeposit cd=Utilities.depositToClientDeposit(d,'m');
            if(cd!=null)list.add(cd);
        }
        for(Deposit d: wh.getExtradepots()){
            ClientDeposit cd=Utilities.depositToClientDeposit(d,'e');
            if(cd!=null)list.add(cd);
        }
        return list;
    }
    public synchronized List<Production> getCurrentActiveProductions(){
        return game.getCurrentActiveProductions();
    }
    public Map<Resource,Integer> getCurrentStrongbox(){
        Map<ResourceType,Integer> s=game.getCurrentStrongbox();
        Map<Resource,Integer> strongbox=new HashMap<>();
        for (Map.Entry<ResourceType, Integer> entry : s.entrySet()) {
            strongbox.put(Utilities.resourceTypeToResource(entry.getKey()),entry.getValue());
        }
        return strongbox;
    }
    public synchronized void activateProductions(String nickname) throws IllegalActionException, IllegalResourceException, ResourcesNotAvailableException, TooManyResourcesException, UnknownFoundException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.activateProductions();
            notifyLobby(new FaithUpdate(getCurrentFaithPath()));
            notifyLobby(new DepositsUpdate(getCurrentWarehouse(),getCurrentStrongbox()));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void buyCard(Integer row,Integer col,Integer slot,String nickname) throws IllegalActionException, ResourcesNotAvailableException, TooManyResourcesException, IllegalSlotException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.buyCard(row, col, slot);
            //update
            notifyLobby(new SlotUpdate(slot,row,col));
            notifyLobby(new DepositsUpdate(getCurrentWarehouse(),getCurrentStrongbox()));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void moveResource(Integer dep1,Integer dep2,String nickname) throws IllegalActionException, NonEmptyException, FullSpaceException, IllegalResourceException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.moveResource(dep1, dep2);
            //update
            ResourceType[] d1=new ResourceType[0];
            ResourceType[] d2=new ResourceType[0];
            try{
                d1=getDepositResources(dep1);
                d2=getDepositResources(dep2);
            }catch (IndexOutOfBoundsException | NullPointerException ignored){}
            notifyLobby(new MoveResourceUpdate(d1,d2,dep1,dep2));
        }
        else throw new NotYourTurnException();
    }
    public synchronized void toggleDiscount(ResourceType res,String nickname) throws IllegalActionException, DiscountNotFoundException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.toggleDiscount(res);

        }
        else throw new NotYourTurnException();
    }
    public synchronized void passTurn(String nickname) throws IllegalActionException, NotYourTurnException {
        if(getCurrentNickname().equals(nickname)) {
            game.passTurn();
            if(!game.getCurrentActive()) game.passTurn();
            gameState=game.getGamePhase();
            //update
            if(getnPlayers()==1){
                notifyLobby(new LorenzoUpdate(getLorenzoPosition(),getLastUsedToken(),getGamePhase()));
            }else notifyLobby(new NewTurnUpdate(getCurrentNickname(),getGamePhase()));
        }
        else throw new NotYourTurnException();
    }
    //---------------------------------------------------------
    @Override
    public synchronized void updateEndGameTriggered() {
        notifyLobby(new EndGameMessage());
    }

    @Override
    public synchronized void updatePopeFavors() {
        notifyLobby(new PopeFavorUpdate(getFaithPathsMap(),getPopeFavors(),getLorenzoPosition()));
    }

    @Override
    public synchronized void updateEndGameResult(Result result) { ;
        notifyLobby(new EndGameResultUpdate(result));
        //close the connections
        for(VirtualClient vc : players){
            if(vc.getClientHandler()!=null) {
                try {
                    vc.getClientHandler().endConnection();
                } catch (InterruptedException | NullPointerException e) {
                    e.printStackTrace();
                }
                server.unregisterClient(vc);
            }
        }
        server.removeLobby(this);

    }

    public void setGame(Game g){
        this.game=g;
    }

}