package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.Source;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.Requirement;

import java.util.*;

public class Player {

    private String nickname;
    private boolean first;
    private Board board;

    private List<LeaderCard> leadersInHand = new ArrayList<>();
    private List<LeaderCard> leadersInGame = new ArrayList<>();

    private Map<ResourceType, Integer> discounts = new HashMap<>();
    private Map<ResourceType, Integer> whiteTo = new HashMap<>();
    private List<Production> extraProductions = new ArrayList<>();

    private Map<Integer,Map<ResourceType, Integer>> pickedResource=new HashMap<>();

    /**
     *pick up a resource from a deposit of warehouse
     * @param id is the id of the deposit
     * @throws ResourcesNotAvailableException if the resource isn't available
     */
    public void pickUpResourceFromWarehouse(Integer id) throws ResourcesNotAvailableException {
        Deposit d;
        try {
            if (id <= 3) {
                d = getBoard().getWarehouse().getMaindepot().get(id - 1);
            } else {
                d = getBoard().getWarehouse().getExtradepots().get(id - 4);
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            System.out.println("Deposito inesistente");
            return;
        }
        ResourceType r=getBoard().getWarehouse().removeResourceFromDeposit(id);
        if(r!=ResourceType.EMPTY){
            if(!pickedResource.containsKey(id)){
                pickedResource.put(id,new HashMap<>());
                pickedResource.get(id).put(ResourceType.GREY,0);
                pickedResource.get(id).put(ResourceType.BLUE,0);
                pickedResource.get(id).put(ResourceType.VIOLET,0);
                pickedResource.get(id).put(ResourceType.YELLOW,0);
            }
            pickedResource.get(id).replace(r,pickedResource.get(id).get(r)+1);
        }else throw new ResourcesNotAvailableException();
    }

    public void pickUpResourceFromStrongbox(ResourceType res) throws ResourcesNotAvailableException {
        ResourceType removed=getBoard().removeResourceFromStrongbox(res);
        if(removed!=ResourceType.EMPTY){
            pickedResource.get(0).replace(removed,pickedResource.get(0).get(removed)+1);
        }else throw new ResourcesNotAvailableException();
    }
    /**
     * deposit back all the resources in the pickedUp map
     */
    public void revertPickUp(){
        for (Map.Entry<Integer, Map<ResourceType, Integer>> entry : pickedResource.entrySet()) {
            if(entry.getKey()==0){              //strongbox
                for (Map.Entry<ResourceType, Integer> entry2 : entry.getValue().entrySet()) {
                    try {
                        for(int i=0;i<entry2.getValue();i++)
                        getBoard().addResourceInStrongbox(entry2.getKey());
                    } catch (IllegalResourceException e) {
                        e.printStackTrace();
                        System.out.println("Impossible Revert!");
                        return;
                    }
                }
            }else {                             //warehouse
                Deposit d;
                if (entry.getKey() > 3) {
                    d = getBoard().getWarehouse().getExtradepots().get(entry.getKey() - 4);
                } else {
                    d = getBoard().getWarehouse().getMaindepot().get(entry.getKey() - 1);
                }
                for (Map.Entry<ResourceType, Integer> entry2 : entry.getValue().entrySet()) {
                    for (int i = 0; i < entry2.getValue(); i++) {
                        try {
                            d.addResource(entry2.getKey());

                        } catch (IllegalResourceException e) {
                            e.printStackTrace();
                            System.out.println("Impossible Revert!");
                            return;
                        } catch (FullSpaceException e) {
                            e.printStackTrace();
                            System.out.println("Impossible Revert!");
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * initialize the pickedResource Map
     */
    public void initializePickedResource(){
        pickedResource.put(0,new HashMap<>()); //strongbox
        pickedResource.put(1,new HashMap<>());
        pickedResource.put(2,new HashMap<>());
        pickedResource.put(3,new HashMap<>());
        for(int i=0;i<4;i++){
            pickedResource.get(i).put(ResourceType.GREY,0);
            pickedResource.get(i).put(ResourceType.BLUE,0);
            pickedResource.get(i).put(ResourceType.VIOLET,0);
            pickedResource.get(i).put(ResourceType.YELLOW,0);
        }

    }
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

        initializePickedResource();
    }

    public void setFirst(boolean first) { this.first = first; }

    public String getNickname() {
        return nickname;
    }

    public boolean isFirst() {
        return first;
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

    public Integer countPoints(){
        Integer temp = 0;
        for (LeaderCard l : leadersInGame){
            temp += l.getPoints();
        }
        return temp+board.countBoardsPoints();
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

    /**
     * Merges two Map<ResourceType, Integer> objects
     * @param mainMap map that is modified
     * @param mapToAdd map that is merged to the first one
     */
    public static void mergeResourceTypeMaps(Map<ResourceType, Integer> mainMap, Map<ResourceType, Integer> mapToAdd) {
        for (ResourceType resourceType : mapToAdd.keySet()) {
            if (mainMap.containsKey(resourceType))
                mainMap.replace(resourceType, mainMap.get(resourceType) + mapToAdd.get(resourceType));
            else mainMap.put(resourceType, mapToAdd.get(resourceType));
        }
    }

    /**
     * checks every available production, creating a collective map of all inputs and outputs of selected productions,
     * then if the resources in the map input are available, passes the map output to the strongbox to be added,
     * and passes the map input to the board to be removed
     * @throws ResourcesNotAvailableException when the resource in the map input are not available
     */
    public void activateProductions() throws ResourcesNotAvailableException {
        Map<ResourceType, Integer> input = new HashMap<>();
        Map<ResourceType, Integer> output = new HashMap<>();
        for (Production production : extraProductions) {
            if (production.checkSelected()) {
                mergeResourceTypeMaps(input, production.getInput());
                mergeResourceTypeMaps(output, production.getOutput());
                production.toggleSelected();
            }
        }
        for (Stack<DevelopmentCard> stack : board.getSlots().values()) {
            for (DevelopmentCard developmentCard : stack) {
                Production production = developmentCard.getProd();
                if (production.checkSelected()) {
                    mergeResourceTypeMaps(input, production.getInput());
                    mergeResourceTypeMaps(output, production.getOutput());
                    production.toggleSelected();
                }
            }
        }
        Production production = board.getBaseProduction();
        if (production.checkSelected()) {
            mergeResourceTypeMaps(input, production.getInput());
            mergeResourceTypeMaps(output, production.getOutput());
            production.toggleSelected();
        }

        Map<ResourceType, Integer> resourcesAvailable = board.getWarehouse().getTotalResources();
        mergeResourceTypeMaps(resourcesAvailable, board.getStrongBox());
        for (ResourceType resourceType : input.keySet())
            if (!resourcesAvailable.containsKey(resourceType) || resourcesAvailable.get(resourceType) < input.get(resourceType))
                throw new ResourcesNotAvailableException();

        // remove input from warehouse
        board.depositInStrongbox(output);
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
