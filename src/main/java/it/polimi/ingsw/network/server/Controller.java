package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.PublicInterface;
import it.polimi.ingsw.model.interfaces.Token;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.CLI.enums.ClientPopeFavorState;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.commands.RevertPickUpCommand;
import it.polimi.ingsw.network.messages.commands.ToggleDiscountCommand;
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
    @Expose
    boolean disconnected;
    @Expose
    private PublicInterface game;
    @Expose
    private List<List<LeaderCard>> lists;

    private Server server;

    //-----------------

    public synchronized void setServer(Server server) {
        this.server = server;
    }

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

    public synchronized boolean isDisconnected() {
        return disconnected;
    }

    public synchronized   List<VirtualClient> getPlayersInLobby() { return players; }

    public synchronized boolean isFull(){
        return nPlayers == players.size();
    }

    public synchronized void setGameObservers(){
        game.addExternalObserver(this);
        game.setGameObservers();
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    /**
     * for test purposes
     * @param nPlayers number of players
     * @param firstPlayer the creator of the lobby
     */
    public Controller(int nPlayers, VirtualClient firstPlayer){
        this.idLobby = globalID;
        globalID++;
        this.nPlayers=nPlayers;
        this.players.add(firstPlayer);
        gson= Utilities.initializeGsonMessage();
        gameState=GamePhase.NOTSTARTED;
        disconnected=false;
    }
    public Controller(int nPlayers, VirtualClient firstPlayer, Server server){
        this.idLobby = globalID;
        globalID++;
        this.nPlayers=nPlayers;
        this.players.add(firstPlayer);
        gson= Utilities.initializeGsonMessage();
        gameState=GamePhase.NOTSTARTED;
        disconnected=false;

        this.server=server;
    }
    public synchronized List<String> getActivePlayers(){
        return game.getActivePlayers();
    }
    public synchronized void setGson(Gson gson) {
        this.gson = gson;
    }
    public synchronized  void resetCurrentPlayerRef(){
        game.resetCurrentPlayerRef();
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

    public synchronized GamePhase getGameState() {
        return gameState;
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
        setGameState(GamePhase.SETUP);
    }

    public synchronized void addPlayerInLobby(VirtualClient vc){
        if(vc!=null)
            players.add(vc);
    }
    public synchronized List<ResourceType> getPlayerPending(String nickname){
        return game.getPlayerPending(nickname);
    }
    public synchronized Map<String,Map<Integer, Stack<String>>> getAllSlots(){
        return game.getAllSlots();
    }
    public synchronized Map<String,List<String>> getAllLeadersInBoard(){
        return game.getAllLeadersInBoard();
    }
    public synchronized List<String> getLeadersInHand(String nickname){
        return game.getLeadersInHand(nickname);
    }
    public synchronized Map<String,Map<Integer,String>> getAllDiscardedCards(){ return game.getAllDiscardedCards(); }
    public synchronized Map<String,Map<Integer,String>> getAllPlayedCards(){ return game.getAllPlayedCards(); }
    public synchronized Map<Resource,Integer> getPlayerPickedResources(String nickname){
        return game.getPlayerPickedResources(nickname);
    }

    public synchronized void reconnectPlayer(String nickname){
        VirtualClient vc = getVirtualClient(nickname);
        if(vc!=null){
            setActive(nickname,true);
            ReconnectUpdate reconnectUpdate=new ReconnectUpdate(getFaithPathsMap(),getPopeFavors(),getLorenzoPosition(),getAllStrongboxes(),getAllWarehouses(),getMarblesInList(),getCardMatrix(),getOrderPlayerList(),getCurrentNickname(),getAllSlots(),getAllLeadersInBoard(),getLeadersInHand(nickname),getGamePhase(),getPlayerLeaderCardList(nickname),getPlayerPending(nickname),getActivePlayers(),getAllPlayedCards(),getAllDiscardedCards(),getPlayerUnknownProductions(nickname),getTurnPhase(),getPlayerPickedResources(nickname),getPlayerDiscounts(nickname),getCurrentActiveProductions());
            vc.send(reconnectUpdate);
        }
    }
    public synchronized Stack<String>[][] getCardMatrix(){
        Stack<DevelopmentCard>[][] cardMatrix= game.getCardMatrix();
        Stack<String>[][] cardMatrixString=new Stack[3][4];
        for(int r=0;r<3;r++){
            for(int c=0;c<4;c++){
                cardMatrixString[r][c]=new Stack<>();
                for(int i=0;i<cardMatrix[r][c].size();i++){
                    String nick=cardMatrix[r][c].get(i).getName();
                    cardMatrixString[r][c].push(nick);
                }
            }
        }
        return cardMatrixString;
    }
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
    public synchronized TurnPhase getTurnPhase(){ return game.getTurnPhase();}
    public synchronized void startGame(){
        try {
            game.startGame();
        } catch (IllegalResourceException e) {
            System.out.println("Internal error, please reinitialize the game");
        } catch (GameNotFullException e) {
            System.out.println("Can't start the game because it's not full! Add more players");
        } catch (NullPointerException e){
            System.out.println("Game not initialized");
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
    public synchronized List<ResourceDiscount> getPlayerDiscounts(String nickname){
        return game.getPlayerDiscounts(nickname);
    }
    public synchronized void disconnectAllPlayers(){
        game.disconnectAllPlayers();
    }
    public synchronized void clearPlayer(String nickname){
        game.clearPlayer(nickname);
    }
    public synchronized Map<Integer,Production> getPlayerUnknownProductions(String name){
        return game.getPlayerUnknownProductions(name);
    }
    public synchronized List<String> getCurrentLeadersInHand(){
        return game.getCurrentLeadersInHand();
    }
    public synchronized List<ResourceType> getCurrentPlayerPending(){
        return game.getCurrentPlayerPending();
    }
    public synchronized Map<String,List<ClientDeposit>> getAllWarehouses(){
        Map<String,List<ClientDeposit>> allClientDeposits=new HashMap<>();
        Map<String,Warehouse> allWarehouseMap=game.getAllWarehouses();
        for (Map.Entry<String, Warehouse> entry : allWarehouseMap.entrySet()) {
            allClientDeposits.put(entry.getKey(),Utilities.warehouseToListOfClientDeposits(entry.getValue()));
        }
        return allClientDeposits;
    }
    public synchronized Map<Resource,Integer> getCurrentStrongbox(){
        Map<ResourceType,Integer> s=game.getCurrentStrongbox();
        Map<Resource,Integer> strongbox=new HashMap<>();
        for (Map.Entry<ResourceType, Integer> entry : s.entrySet()) {
            strongbox.put(Utilities.resourceTypeToResource(entry.getKey()),entry.getValue());
        }
        return strongbox;
    }
    public synchronized Map<String,Map<Resource,Integer>> getAllStrongboxes(){
        Map<String,Map<ResourceType,Integer>> strongboxesMap=game.getAllStrongboxes();
        Map<String,Map<Resource,Integer>> clientStrongboxesMap=new HashMap<>();
        for (Map.Entry<String,Map<ResourceType,Integer>> entry : strongboxesMap.entrySet()) {
            clientStrongboxesMap.put(entry.getKey(),Utilities.strongboxToClientStrongbox(entry.getValue()));
        }
        return clientStrongboxesMap;
    }

    public synchronized List<ClientDeposit> getCurrentWarehouse(){
        return Utilities.warehouseToListOfClientDeposits(game.getCurrentWarehouse());
    }
    public synchronized List<Production> getCurrentActiveProductions(){
        return game.getCurrentActiveProductions();
    }

    public void stopLobbyPingers(){
        for(VirtualClient vc : players){
            if(vc.getClientHandler()!=null)vc.getClientHandler().stopPinger();
        }
    }
    public void startLobbyPingers(){
        for(VirtualClient vc : players){
            if(vc.getClientHandler()!=null)vc.getClientHandler().startPinger();
        }
    }
    //COMMANDS
    public synchronized void chooseLeader(List<String> l,String nickname) throws IllegalLeaderCardsException, IllegalActionException, NonEmptyException, NotYourTurnException, IllegalCommandException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                Map<String, LeaderCard> map = getNameLeaderCardMap();
                List<LeaderCard> list = new ArrayList<>();
                list.add(map.get(l.get(0)));
                list.add(map.get(l.get(1)));
                for (LeaderCard lc : list) {
                    if (lc == null) throw new IllegalCommandException();
                }
                game.chooseLeader(list);

                //update
                getVirtualClient(nickname).send(new LeadersInHandUpdate(getCurrentLeadersInHand()));
                int nBonus = getNickOrderMap().get(nickname);
                getVirtualClient(nickname).send(new BonusResourceMessage(nBonus));

            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void chooseResourceToDeposit(Integer id,ResourceType res,String nickname) throws NotYourTurnException, UnknownNotFoundException, IllegalActionException, IllegalResourceException, FullSpaceException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.chooseResourceToDeposit(id, res);
                //update
                notifyLobby(new DepositUpdate(id, Utilities.resourceTypeToResource(res)));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void playLeader(int index,String nickname) throws NotYourTurnException, IllegalResourceException, IllegalActionException, RequirementNotMetException, CardNotAvailableException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                String name=null;
                if(game.getLeadersInHand(game.getCurrentNickname()).size()>index)
                    name=game.getLeadersInHand(game.getCurrentNickname()).get(index);
                game.playLeader(index);
                //update
                notifyLobby(new PlayLeaderUpdate(index,name));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void discardLeader(int index,String nickname) throws IllegalActionException, CardNotAvailableException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.discardLeader(index);
                notifyLobby(new DiscardLeaderUpdate(index));
                notifyLobby(new FaithUpdate(getCurrentFaithPath()));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void buyAtMarketInterface(char rc,int index,String nickname) throws NotYourTurnException, IllegalActionException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.buyAtMarketInterface(rc, index);
                //update
                List<ResourceType> list = getCurrentPlayerPending();
                notifyLobby(new FaithUpdate(getCurrentFaithPath()));
                notifyLobby(new MarketUpdate(getMarblesInList()));
                if (list.size() > 0)
                    getVirtualClient(nickname).send(new PendingResourcesUpdate(list));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void depositResource(Integer id,ResourceType res,String nickname) throws NotYourTurnException, IllegalResourceException, FullSpaceException, IllegalActionException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.depositResource(id, res);
                //update
                notifyLobby(new DepositUpdate(id, Utilities.resourceTypeToResource(res)));
                List<ResourceType> list = getCurrentPlayerPending();
                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null) vc.send(new PendingResourcesUpdate(list));

            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void discardResource(ResourceType res,String nickname) throws NotYourTurnException, IllegalResourceException, DepositableResourceException, IllegalActionException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.discardResource(res);
                List<ResourceType> list = getCurrentPlayerPending();
                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null)
                    vc.send(new PendingResourcesUpdate(list));
                notifyLobby(new DiscardResourceUpdate());
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void transformWhiteIn(ResourceType res,String nickname) throws IllegalResourceException, IllegalActionException, NoWhiteResourceException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.transformWhiteIn(res);
                List<ResourceType> list = getCurrentPlayerPending();
                if (list.size() > 0) {
                    VirtualClient vc = getVirtualClient(nickname);
                    if (vc != null) vc.send(new PendingResourcesUpdate(list));
                }
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void substituteUnknownInInputBaseProduction(ResourceType res,String nickname) throws UnknownNotFoundException, IllegalResourceException, IllegalActionException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.substituteUnknownInInputBaseProduction(res);
                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null) vc.send(new UnknownProductionUpdate(-1, res, 'i'));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void substituteUnknownInOutputBaseProduction(ResourceType res,String nickname) throws NotYourTurnException, UnknownNotFoundException, IllegalResourceException, IllegalActionException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.substituteUnknownInOutputBaseProduction(res);
                //update
                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null) vc.send(new UnknownProductionUpdate(-1, res, 'o'));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void substituteUnknownInInputExtraProduction(Integer index,ResourceType res,String nickname) throws NotYourTurnException, UnknownNotFoundException, IllegalResourceException, IllegalActionException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.substituteUnknownInInputExtraProduction(index, res);
                //update
                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null) vc.send(new UnknownProductionUpdate(index, res, 'i'));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void substituteUnknownInOutputExtraProduction(Integer index,ResourceType res,String nickname) throws UnknownNotFoundException, IllegalResourceException, IllegalActionException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.substituteUnknownInOutputExtraProduction(index, res);
                VirtualClient vc = getVirtualClient(nickname);
                vc.send(new UnknownProductionUpdate(index, res, 'o'));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void toggleBaseProd(String nickname) throws UnknownFoundException, IllegalActionException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.toggleBaseProd();
                VirtualClient vc = getVirtualClient(nickname);
                vc.send(new ToggleProductionUpdate(getCurrentActiveProductions()));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void toggleExtraProd(Integer index,String nickname) throws UnknownFoundException, IllegalActionException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.toggleExtraProd(index);
                VirtualClient vc = getVirtualClient(nickname);
                vc.send(new ToggleProductionUpdate(getCurrentActiveProductions()));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void toggleCardProd(Integer slot,String nickname) throws IllegalActionException, IllegalSlotException, UnknownFoundException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.toggleCardProd(slot);
                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null) vc.send(new ToggleProductionUpdate(getCurrentActiveProductions()));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void pickUpResourceFromWarehouse(Integer id,String nickname) throws IllegalActionException, ResourcesNotAvailableException, NonEmptyException, DepositNotExistingException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.pickUpResourceFromWarehouse(id);
                notifyLobby(new PickUpWarehouseUpdate(id));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void pickUpResourceFromStrongbox(ResourceType res,String nickname) throws IllegalActionException, ResourcesNotAvailableException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.pickUpResourceFromStrongbox(res);
                notifyLobby(new PickUpStrongboxUpdate(Utilities.resourceTypeToResource(res)));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void revertPickUp(String nickname) throws IllegalResourceException, FullSpaceException, IllegalActionException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.revertPickUp();
                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null) {
                    vc.send(new RevertUpdate());
                    vc.send(new DepositsUpdate(getCurrentWarehouse(), getCurrentStrongbox(), getTurnPhase()));
                }
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void activateProductions(String nickname) throws IllegalActionException, IllegalResourceException, ResourcesNotAvailableException, TooManyResourcesException, UnknownFoundException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                List<Production> before= it.polimi.ingsw.model.Utilities.copyProductionsList(game.getCurrentActiveProductions());
                game.activateProductions();
                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null) {
                    vc.send(new RevertUpdate());
                    vc.send(new ResetProductionsUpdate(before));
                }
                notifyLobby(new FaithUpdate(getCurrentFaithPath()));
                notifyLobby(new DepositsUpdate(getCurrentWarehouse(), getCurrentStrongbox(), getTurnPhase()));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void buyCard(Integer row,Integer col,Integer slot,String nickname) throws IllegalActionException, ResourcesNotAvailableException, TooManyResourcesException, IllegalSlotException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.buyCard(row, col, slot);
                //update
                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null) {
                    vc.send(new RevertUpdate());
                }
                notifyLobby(new SlotUpdate(slot, row, col));
                notifyLobby(new DepositsUpdate(getCurrentWarehouse(), getCurrentStrongbox(), getTurnPhase()));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void moveResource(Integer dep1,Integer dep2,String nickname) throws IllegalActionException, NonEmptyException, FullSpaceException, IllegalResourceException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.moveResource(dep1, dep2);
                //update
                ResourceType[] d1 = new ResourceType[0];
                ResourceType[] d2 = new ResourceType[0];
                try {
                    d1 = getDepositResources(dep1);
                    d2 = getDepositResources(dep2);
                } catch (IndexOutOfBoundsException | NullPointerException ignored) {
                }
                notifyLobby(new MoveResourceUpdate(d1, d2, dep1, dep2));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void toggleDiscount(ResourceType res,String nickname) throws IllegalActionException, DiscountNotFoundException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.toggleDiscount(res);

                VirtualClient vc = getVirtualClient(nickname);
                if (vc != null) {
                    vc.send(new ToggleDiscountUpdate(res));
                }
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
    }
    public synchronized void passTurn(String nickname) throws IllegalActionException, NotYourTurnException, WaitingReconnectionsException {
        if(!disconnected) {
            if (getCurrentNickname().equals(nickname)) {
                game.passTurn();
                while (!game.getCurrentActive()) game.passTurn();
                gameState = game.getGamePhase();
                //locally saving server
                //im stopping the pingers because the server could be lock (choice of number of players)
                stopLobbyPingers();
                if(server!=null) server.saveServerStatus();
                startLobbyPingers();
                //update
                if (getnPlayers() == 1) {
                    notifyLobby(new LorenzoUpdate(getLorenzoPosition(), getLastUsedToken(), getGamePhase()));
                } else notifyLobby(new NewTurnUpdate(getCurrentNickname(), getGamePhase()));
            } else throw new NotYourTurnException();
        }else throw new WaitingReconnectionsException();
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
                //stopping the pingers whenever i use a method of server because it could be locked
                stopLobbyPingers();
                server.unregisterClient(vc);
            }
        }
        server.removeLobby(this);

    }

    public void setGame(Game g){
        this.game=g;
    }

}