package it.polimi.ingsw.model.interfaces;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Result;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;

import java.util.List;
import java.util.Map;

public interface PublicInterface {
    public Map<String, LeaderCard> getNameLeaderCardMap();
    public Map<String,Integer> getFaithPathsMap();
    public List<ResourceType> getMarblesInList();
    public ResourceType[] getDepositResources(int id);
    public GamePhase getGamePhase();
    public Integer getCurrentFaithPath();
    public Integer getLorenzoPosition();
    public void setActive(String player,boolean active);
    public String getCurrentNickname();
    public Map<String,Integer> getNickOrderMap();
    public List<String> getListNickname();
    public List<Player> getPlayers();
    public void startGame() throws IllegalResourceException, GameNotFullException;
    public Result endGame();
    public List<List<LeaderCard>> divideLeaderCards() throws EmptyPlayersException;
    public void addPlayer(String nickname) throws FullGameException;
    public List<ResourceType> getCurrentPlayerPending();
    public void chooseLeader(List<LeaderCard> l) throws NonEmptyException, IllegalLeaderCardsException, IllegalActionException;
    public void chooseResourceToDeposit(Integer id,ResourceType res) throws IllegalActionException, FullSpaceException, UnknownNotFoundException, IllegalResourceException;
    public void playLeader(int index) throws CardNotAvailableException, RequirementNotMetException, IllegalActionException, IllegalResourceException;
    public void discardLeader(int index) throws CardNotAvailableException, IllegalActionException;
    public void buyAtMarketInterface(char rc,int index) throws IllegalActionException;
    public void depositResource(Integer id,ResourceType res) throws IllegalActionException, FullSpaceException, IllegalResourceException;
    public void discardResource(ResourceType res) throws IllegalActionException, IllegalResourceException, DepositableResourceException;
    public void transformWhiteIn(ResourceType res) throws IllegalActionException, IllegalResourceException, NoWhiteResourceException;
    public void substituteUnknownInInputBaseProduction(ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException;
    public void substituteUnknownInOutputBaseProduction(ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException;
    public void substituteUnknownInInputExtraProduction(Integer index,ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException;
    public void substituteUnknownInOutputExtraProduction(Integer index,ResourceType res) throws IllegalActionException, UnknownNotFoundException, IllegalResourceException;
    public void toggleBaseProd() throws UnknownFoundException, IllegalActionException;
    public void toggleExtraProd(Integer index) throws UnknownFoundException, IllegalActionException;
    public void toggleCardProd(Integer slot) throws UnknownFoundException, IllegalActionException, IllegalSlotException;
    public void pickUpResourceFromWarehouse(Integer id) throws IllegalActionException, ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException;
    public void pickUpResourceFromStrongbox(ResourceType res) throws IllegalActionException, ResourcesNotAvailableException;
    public void revertPickUp() throws IllegalActionException, FullSpaceException, IllegalResourceException;
    public void activateProductions() throws IllegalResourceException, ResourcesNotAvailableException, TooManyResourcesException, UnknownFoundException, IllegalActionException;
    public void buyCard(Integer row,Integer col,Integer slot) throws ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException, IllegalActionException;
    public void moveResource(Integer dep1,Integer dep2) throws IllegalActionException, IllegalResourceException, FullSpaceException, NonEmptyException;
    public void toggleDiscount(ResourceType res) throws IllegalActionException, DiscountNotFoundException;
    public void passTurn() throws IllegalActionException;


}
