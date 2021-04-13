package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.Requirement;

import java.util.*;

public class Player {

    private String nickname;
    private Integer order;
    private Board board;

    private List<LeaderCard> leadersInHand = new ArrayList<>();
    private List<LeaderCard> leadersInGame = new ArrayList<>();

    private Map<ResourceType, Integer> discounts = new HashMap<>();
    private Map<ResourceType, Integer> whiteTo = new HashMap<>();
    private List<Production> extraProductions = new ArrayList<>();

    private Map<Integer,Map<ResourceType, Integer>> pickedResource=new HashMap<>();

    public void setOrder(int order){
        this.order=order;
    }
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
    public void revertPickUp() {
        for (Map.Entry<Integer, Map<ResourceType, Integer>> entry : pickedResource.entrySet()) {
            if (entry.getKey() == 0) {              //strongbox
                for (Map.Entry<ResourceType, Integer> entry2 : entry.getValue().entrySet()) {
                    try {
                        for (int i = 0; i < entry2.getValue(); i++)
                            getBoard().addResourceInStrongbox(entry2.getKey());
                    } catch (IllegalResourceException e) {
                        e.printStackTrace();
                        System.out.println("Impossible Revert!");
                        return;
                    }
                }
            } else {                             //warehouse
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
        clearPickedUp();
    }

    /**
     * clear the pickedResource map
     */
    public void clearPickedUp(){
        for (Map.Entry<Integer, Map<ResourceType,Integer>> entry : pickedResource.entrySet()) {
            entry.getValue().replace(ResourceType.GREY,0);
            entry.getValue().replace(ResourceType.BLUE,0);
            entry.getValue().replace(ResourceType.VIOLET,0);
            entry.getValue().replace(ResourceType.YELLOW,0);
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
     * */
    public Player(String nickname) {
        this.nickname = nickname;
        this.order = -1;
        board = new Board();

        initializePickedResource();
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getOrder(){return order;}

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

    void chooseLeaders(List<LeaderCard> leaderCards) throws IllegalLeaderCardsException, NonEmptyException {
        if(leaderCards.size()!=2 || leaderCards.get(0).equals(leaderCards.get(1))){
            throw new IllegalLeaderCardsException();
        }
        if (!leadersInHand.isEmpty())
            throw new NonEmptyException();
        leadersInHand.addAll(leaderCards);
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
     * @throws ResourcesNotAvailableException if the resources in the map input are not available
     * @throws IllegalResourceException if the output contains illegal resources
     * @throws TooManyResourcesException if the resources in pickedResources are too many for the input of the big production
     */
    public void activateProductions() throws ResourcesNotAvailableException, IllegalResourceException, TooManyResourcesException, UnknownFindException {
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
        Production bigProd=new Production(input,output);
        checkPickedResourcesForProduction(bigProd);

        clearPickedUp();
        elaborateOutput(output);
    }
    //----------------------------------------------------

    /**
     * Deposit in strongbox the normal resources and convert in faith the Red resources
     * @param output is output of a production
     */
    public void elaborateOutput(Map<ResourceType,Integer> output) throws IllegalResourceException {
        if(output.containsKey(ResourceType.UNKNOWN) || output.containsKey(ResourceType.WHITE) || output.containsKey(ResourceType.EMPTY)){
            throw new IllegalResourceException();
        }
        int faithProgression=0;
        if(output.containsKey(ResourceType.RED)) {
            faithProgression = output.get(ResourceType.RED);
            output.replace(ResourceType.RED,0);
        }
        board.depositInStrongbox(output);
        board.getFaithPath().addToPosition(faithProgression);
    }
    /**
     * check in pickedResource map if you have enough resources for activating a certain production
     * @param p is the production
     * @throws ResourcesNotAvailableException if you have picked up less resources than the needed
     * @throws TooManyResourcesException if you have picked up more resources than the needed
     */
    public void checkPickedResourcesForProduction(Production p) throws ResourcesNotAvailableException, TooManyResourcesException {
        Map<ResourceType, Integer> resourcesAvailable=new HashMap<>();
        for (Map.Entry<Integer, Map<ResourceType,Integer>> entry : pickedResource.entrySet()) {
            mergeResourceTypeMaps(resourcesAvailable,entry.getValue());
        }
        int totResProd=0;
        int totResPick=0;
        for (ResourceType resourceType : p.getInput().keySet()) {
            totResProd+=p.getInput().get(resourceType);
            if (!resourcesAvailable.containsKey(resourceType) || resourcesAvailable.get(resourceType) < p.getInput().get(resourceType))
                throw new ResourcesNotAvailableException();
            if (resourcesAvailable.get(resourceType)>p.getInput().get(resourceType)){
                throw new TooManyResourcesException();
            }
        }
        for (Map.Entry<ResourceType, Integer> entry : resourcesAvailable.entrySet()) {
            totResPick+=entry.getValue();
        }
        if(totResPick!=totResProd) throw new TooManyResourcesException();
    }

    /**
     * check in all the sources if you have enough resources for activating a certain production
     * @param p is the production
     * @throws ResourcesNotAvailableException if you have less resources than the needed
     */
    public void checkTotalResourcesForProduction(Production p) throws ResourcesNotAvailableException {
        Map<ResourceType, Integer> resourcesAvailable = board.getWarehouse().getTotalResources();
        mergeResourceTypeMaps(resourcesAvailable, board.getStrongBox());
        //add also the picked up resources
        for (Map.Entry<Integer, Map<ResourceType,Integer>> entry : pickedResource.entrySet()) {
            mergeResourceTypeMaps(resourcesAvailable,entry.getValue());
        }
        for (ResourceType resourceType : p.getInput().keySet())
            if (!resourcesAvailable.containsKey(resourceType) || resourcesAvailable.get(resourceType) < p.getInput().get(resourceType))
                throw new ResourcesNotAvailableException();
    }

    /**
     * check in pickedResource map if you have enough resources for buying a certain davelopment card
     * @param d is the development card
     * @throws ResourcesNotAvailableException if you have picked up less resources than the needed
     * @throws TooManyResourcesException if you have picked up more resources than the needed
     */
    public void checkPickedResourceForDevelopmentCard(DevelopmentCard d) throws ResourcesNotAvailableException, TooManyResourcesException {
        Map<ResourceType, Integer> resourcesAvailable=new HashMap<>();
        for (Map.Entry<Integer, Map<ResourceType,Integer>> entry : pickedResource.entrySet()) {
            mergeResourceTypeMaps(resourcesAvailable,entry.getValue());
        }
        int totResReq=0;
        int totResPick=0;
        for(ResourceRequirement rs : d.getCost()){
            totResReq+=rs.getQuantity();
            if(resourcesAvailable.get(rs.getResource())<rs.getQuantity()){
                throw new ResourcesNotAvailableException();
            }
            if (resourcesAvailable.get(rs.getResource())>rs.getQuantity()){
                throw new TooManyResourcesException();
            }
        }
        //check if there are other resources not considered
        for (Map.Entry<ResourceType, Integer> entry : resourcesAvailable.entrySet()) {
            totResPick+=entry.getValue();
        }
        if(totResPick!=totResReq) throw new TooManyResourcesException();
    }

    /**
     * buy the selected development card and push it on the board
     * @param d is the development card
     * @param slot is the id of the slots map on the board
     * @throws IllegalSlotException if you can't put the card in that slot
     */
    public void buyCard(DevelopmentCard d,Integer slot) throws IllegalSlotException, TooManyResourcesException, ResourcesNotAvailableException {

        checkPickedResourceForDevelopmentCard(d);
        if(d.getLevel()==1){
            if(getBoard().getSlots().get(slot).size()>0){
                throw new IllegalSlotException();
            }
        }else if(d.getLevel()==2){
            if(getBoard().getSlots().get(slot).size()!=1 || getBoard().getSlots().get(slot).get(0).getLevel()!=1){
                throw new IllegalSlotException();
            }
        }else if(d.getLevel()==3){
            if(getBoard().getSlots().get(slot).size()!=2 || getBoard().getSlots().get(slot).get(1).getLevel()!=2 || getBoard().getSlots().get(slot).get(0).getLevel()!=1){
                throw new IllegalSlotException();
            }
        }
        getBoard().getSlots().get(slot).push(d);
        clearPickedUp();
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
    public void substituteUnknownInInputProduction(Production p,ResourceType res) throws UnknownNotFindException {
        if(p.getInput().containsKey(ResourceType.UNKNOWN)){
            int n=p.getInput().get(ResourceType.UNKNOWN);
            if(n==1) p.getInput().remove(ResourceType.UNKNOWN);
            else if(n>1) p.getInput().replace(ResourceType.UNKNOWN,n-1);
            if(p.getInput().containsKey(res)){
                p.getInput().replace(res,p.getInput().get(res)+1);
            }else {
                p.addInput(res, 1);
            }
        }else throw new UnknownNotFindException();
    }
    public void substituteUnknownInOutputProduction(Production p,ResourceType res) throws UnknownNotFindException {
        if(p.getOutput().containsKey(ResourceType.UNKNOWN)){
            int n=p.getOutput().get(ResourceType.UNKNOWN);
            if(n==1) p.getOutput().remove(ResourceType.UNKNOWN);
            else if(n>1) p.getOutput().replace(ResourceType.UNKNOWN,n-1);
            if(p.getOutput().containsKey(res)){
                p.getOutput().replace(res,p.getOutput().get(res)+1);
            }else {
                p.addOutput(res, 1);
            }
        }else throw new UnknownNotFindException();
    }
}
