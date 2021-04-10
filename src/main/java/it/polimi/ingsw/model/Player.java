package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.Source;
import it.polimi.ingsw.model.exceptions.CardNotAvailableException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NoWhiteResourceException;
import it.polimi.ingsw.model.exceptions.RequirementNotMetException;
import it.polimi.ingsw.model.interfaces.Requirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {

    private String nickname;
    private boolean first;
    private Integer points;
    private Board board;

    private List<LeaderCard> leadersInHand = new ArrayList<>();
    private List<LeaderCard> leadersInGame = new ArrayList<>();

    private Map<ResourceType, Integer> discounts = new HashMap<>();
    private Map<ResourceType, Integer> whiteTo = new HashMap<>();
    private List<Production> extraProductions = new ArrayList<>();

    private Map<Source, Map<ResourceType, Integer>> pickedResources;

    /**
     * used for testing purposes
     */
    public Player() {

    }

    /**
     * under implementation
     * @param nickname player name
     * @param first indicates whether the player is the first to play
     */
    public Player(String nickname, boolean first) {
        this.nickname = nickname;
        this.first = first;
        board = new Board();
    }

    public void setFirst(boolean first) { this.first = first; }

    public String getNickname() {
        return nickname;
    }

    public boolean isFirst() {
        return first;
    }

    public Integer getPoints() {
        return points;
    }

    public Board getBoard() {
        return board;
    }

    public List<LeaderCard> getLeadersInHand() {
        return leadersInHand;
    }

    public List<LeaderCard> getLeadersInGame() {
        return leadersInGame;
    }

    public Map<ResourceType, Integer> getDiscounts() {
        return discounts;
    }

    public Map<ResourceType, Integer> getWhiteTo() {
        return whiteTo;
    }

    public List<Production> getExtraProductions() {
        return extraProductions;
    }

    void chooseLeaders(List<LeaderCard> leaderCards) throws Exception {
        if (!leadersInHand.isEmpty())
            throw new Exception();
        for (LeaderCard leaderCard : leaderCards) {
            if (leadersInHand.contains(leaderCard))
                throw new Exception();
            else leadersInHand.add(leaderCard);
        }
    }

    /**
     * if the card passed as parameter is available and its requirements are met,
     * all its abilities get activated, then the card is removed
     * from the leadersInHand list and added to the leadersInGame list
     * @param leaderCard card that is activated
     * @throws CardNotAvailableException thrown if leaderCard is not in the leadersInHand list
     */
    void playLeader(LeaderCard leaderCard) throws CardNotAvailableException, RequirementNotMetException {
        if(leadersInHand.contains(leaderCard)) {
            for (Requirement requirement : leaderCard.getRequirements()) {
                if (!requirement.check(this.board))
                    throw new RequirementNotMetException();
            }
            for (SpecialAbility specialAbility : leaderCard.getSpecialAbilities())
                specialAbility.activate(this);
            leadersInHand.remove(leaderCard);
            leadersInGame.add(leaderCard);
        } else throw new CardNotAvailableException();
    }

    /**
     * Modifies the map discounts:
     *  if the resource type is already present, increments its value by 1
     *  if the resource type is not present, it gets added to the map with value 1
     * @param resourceType discount added
     */
    public void addDiscount(ResourceType resourceType) {
        if(discounts.containsKey(resourceType))
            discounts.replace(resourceType, discounts.get(resourceType) + 1);
        else discounts.put(resourceType, 1);
    }

    /**
     * Modifies the map for the white marble conversion:
     *      *  if the resource type is already present, increments its value by 1
     *      *  if the resource type is not present, it gets added to the map with value 1
     * @param resourceType conversion added
     */
    public void addWhiteTo(ResourceType resourceType) {
        if(whiteTo.containsKey(resourceType))
            whiteTo.replace(resourceType, whiteTo.get(resourceType) + 1);
        else whiteTo.put(resourceType, 1);
    }

    /**
     * adds the new production to the extra productions list
     * @param production added
     */
    public void addExtraProduction(Production production) {
        extraProductions.add(production);
    }

    //----------------------------------------------------

    /**
     * Transform the white resource in pending in the resource contained in whiteTo
     * @param res is the ResourceType
     */
    public void transformWhiteIn(ResourceType res) throws NoWhiteResourceException, IllegalResourceException {
        if(whiteTo.containsKey(res)){
            int n=whiteTo.get(res);
            getBoard().getWarehouse().replaceWhiteFromPending(res,n);
        }else throw new IllegalResourceException();
    }
}
