package it.polimi.ingsw.model.interfaces;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.client.ClientModel.CLI.ClientPopeFavorState;
import it.polimi.ingsw.network.server.GameObserver;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public interface PublicInterface {
     Map<String, LeaderCard> getNameLeaderCardMap();
     Map<String, DevelopmentCard> getNameDevelopmentCardMap();
     Map<String,Integer> getFaithPathsMap();
     List<ResourceType> getMarblesInList();
     ResourceType[] getDepositResources(int id);
     List<Production> getCurrentActiveProductions();
     Integer getCurrentFaithPath();
     Integer getLorenzoPosition();
     Warehouse getCurrentWarehouse();
     void startGame() throws IllegalResourceException, GameNotFullException;
     GamePhase getGamePhase();
     Result endGame();
     boolean getCurrentActive();
     void clearPlayer(String nickname);
     void addPlayer(String nickname) throws FullGameException;
     void setActive(String player,boolean active);
     List<String> getListNickname();
     String getCurrentNickname();
     Map<String,Integer> getNickOrderMap();
     List<Player> getPlayers();
     List<List<LeaderCard>> divideLeaderCards() throws EmptyPlayersException;
     List<ResourceType> getCurrentPlayerPending();
     Stack<DevelopmentCard>[][] getCardMatrix();
     List<String> getCurrentLeadersInHand();
     Map<ResourceType,Integer> getCurrentStrongbox();
     Token getLastUsedToken();
     Map<String,Map<Integer, ClientPopeFavorState>> getPopeFavors();
     void chooseLeader(List<LeaderCard> l) throws NonEmptyException, IllegalLeaderCardsException, IllegalActionException;
     void chooseResourceToDeposit(Integer id,ResourceType res) throws IllegalActionException, FullSpaceException, UnknownNotFoundException, IllegalResourceException;

     void playLeader(int index) throws CardNotAvailableException, RequirementNotMetException, IllegalActionException, IllegalResourceException;
     void discardLeader(int index) throws CardNotAvailableException, IllegalActionException;

     void depositResource(Integer id,ResourceType res) throws IllegalActionException, FullSpaceException, IllegalResourceException;
     void discardResource(ResourceType res) throws IllegalActionException, IllegalResourceException, DepositableResourceException;
     void moveResource(Integer dep1,Integer dep2) throws IllegalActionException, IllegalResourceException, FullSpaceException, NonEmptyException;

     void transformWhiteIn(ResourceType res) throws IllegalActionException, IllegalResourceException, NoWhiteResourceException;
     void toggleDiscount(ResourceType res) throws IllegalActionException, DiscountNotFoundException;

     void substituteUnknownInInputBaseProduction(ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException;
     void substituteUnknownInOutputBaseProduction(ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException;
     void substituteUnknownInInputExtraProduction(Integer index,ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException;
     void substituteUnknownInOutputExtraProduction(Integer index,ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException;

     void toggleBaseProd() throws UnknownFoundException, IllegalActionException;
     void toggleExtraProd(Integer index) throws UnknownFoundException, IllegalActionException;
     void toggleCardProd(Integer slot) throws UnknownFoundException, IllegalActionException, IllegalSlotException;

     void pickUpResourceFromWarehouse(Integer id) throws IllegalActionException, ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException;
     void pickUpResourceFromStrongbox(ResourceType res) throws IllegalActionException, ResourcesNotAvailableException;
     void revertPickUp() throws IllegalActionException, FullSpaceException, IllegalResourceException;

     void activateProductions() throws IllegalResourceException, ResourcesNotAvailableException, TooManyResourcesException, UnknownFoundException, IllegalActionException;
     void buyCard(Integer row,Integer col,Integer slot) throws ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException, IllegalActionException;
     void buyAtMarketInterface(char rc,int index) throws IllegalActionException;

     void passTurn() throws IllegalActionException;


     //--
     void addExternalObserver(GameObserver go);
}
