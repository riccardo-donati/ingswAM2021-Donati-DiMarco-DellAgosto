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

    private List<ResourceDiscount> discounts = new ArrayList<>();
    private Map<ResourceType, Integer> whiteTo = new HashMap<>();
    private List<Production> extraProductions = new ArrayList<>();

    private Map<Integer,Map<ResourceType, Integer>> pickedResource = new HashMap<>();

    protected void setOrder(int order){
        this.order=order;
    }

    /**
     *pick up a resource from a deposit of warehouse
     * @param id is the id of the deposit
     * @throws ResourcesNotAvailableException if the resource isn't available
     */
    protected void pickUpResourceFromWarehouse(Integer id) throws ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException {
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

    protected void pickUpResourceFromStrongbox(ResourceType res) throws ResourcesNotAvailableException {
        ResourceType removed=getBoard().removeResourceFromStrongbox(res);
        if(removed!=ResourceType.EMPTY){
            pickedResource.get(0).replace(removed,pickedResource.get(0).get(removed)+1);
        }else throw new ResourcesNotAvailableException();
    }
    /**
     * deposit back all the resources in the pickedUp map
     */
    protected void revertPickUp() throws FullSpaceException, IllegalResourceException {
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
                for (Map.Entry<ResourceType, Integer> entry2 : entry.getValue().entrySet())
                    for (int i = 0; i < entry2.getValue(); i++)
                        d.addResource(entry2.getKey());
            }
        }
        clearPickedUp();
    }

    /**
     * clear the pickedResource map
     */
    protected void clearPickedUp(){
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
    protected void initializePickedResource(){
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
    protected Player() {

    }

    /**
     * under implementation
     * @param nickname player name
     * */
    protected Player(String nickname) {
        this.nickname = nickname;
        this.order = -1;
        board = new Board();

        initializePickedResource();
    }

    protected String getNickname() {
        return nickname;
    }

    protected Integer getOrder(){return order;}

    protected Board getBoard() {
        return board;
    }

    protected List<LeaderCard> getLeadersInHand() {
        return leadersInHand;
    }

    protected List<LeaderCard> getLeadersInGame() {
        return leadersInGame;
    }

    protected List<ResourceDiscount> getDiscounts() {
        return discounts;
    }

    protected Map<ResourceType, Integer> getWhiteTo() {
        return whiteTo;
    }

    protected List<Production> getExtraProductions() {
        return extraProductions;
    }

    /**
     * calculates the points from the leader cards played and sums them to the board's points
     * @return the player's total points
     */
    protected Integer countPoints(){
        Integer temp = 0;
        for (LeaderCard l : leadersInGame){
            temp += l.getPoints();
        }
        return temp + board.countBoardsPoints();
    }

    /**
     * Given a list of 2 leader cards, adds them to the leadersInHand list
     * @param leaderCards list of leader cards chosen
     * @throws IllegalLeaderCardsException thrown if the list in input doesn't contain exactly two cards
     * @throws NonEmptyException thrown if the player already has leader cards in their hand
     */
    void chooseLeaders(List<LeaderCard> leaderCards) throws IllegalLeaderCardsException, NonEmptyException {
        if(leaderCards.size()!=2 || leaderCards.get(0).equals(leaderCards.get(1))){
            throw new IllegalLeaderCardsException();
        }
        if (!leadersInHand.isEmpty())
            throw new NonEmptyException();
        leadersInHand.addAll(leaderCards);
    }

    /**
     * if the card passed as parameter is available and its requirements are met,
     * all its abilities get activated, then the card is removed
     * from the leadersInHand list and added to the leadersInGame list
     * @param leaderCard card that is activated
     * @throws CardNotAvailableException thrown if leaderCard is not in the leadersInHand list
     */
    void playLeader(LeaderCard leaderCard) throws CardNotAvailableException, RequirementNotMetException, IllegalResourceException {
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
     * discard a leader card in hand to gain 1 faith point
     * @param ld is the leadercard
     * @throws CardNotAvailableException if the leader card is not available
     */
    protected void discardLeader(LeaderCard ld) throws CardNotAvailableException {
        if(leadersInHand.contains(ld)){
            leadersInHand.remove(ld);
            getBoard().getFaithPath().addToPosition(1);
        }else throw new CardNotAvailableException();
    }

    /**
     * Modifies the map discounts:
     *  if the resource type is already present, increments its value by 1
     *  if the resource type is not present, it gets added to the map with value 1
     * @param resourceType discount added
     */
    protected void addDiscount(ResourceType resourceType) {
        boolean flag=true;
        for(ResourceDiscount rd : discounts){
            if(rd.getRes()==resourceType){
                rd.addQuantity();
                flag=false;
            }
        }
        if(flag){
            discounts.add(new ResourceDiscount(resourceType));
        }
    }

    /**
     * toggle a discount of the selected resource type
     * @param res is the resource type
     */
    protected void toggleDiscount(ResourceType res) throws DiscountNotFoundException {
        boolean find=false;
        for(ResourceDiscount rd : discounts){
            if(rd.getRes()==res){
                rd.toggle();
                find=true;
                break;
            }
        }
        if(!find) throw new DiscountNotFoundException();
    }

    /**
     * Modifies the map for the white marble conversion:
     *  if the resource type is already present, increments its value by 1
     *  if the resource type is not present, it gets added to the map with value 1
     * @param resourceType conversion added
     */
    protected void addWhiteTo(ResourceType resourceType) {
        whiteTo.merge(resourceType, 1, Integer::sum);
    }

    /**
     * adds the new production to the extra productions list
     * @param production added
     */
    protected void addExtraProduction(Production production) {
        extraProductions.add(production);
    }

    /**
     * checks every available production, creating a collective map of all inputs and outputs of selected productions,
     * then if the resources in the map input are available, passes the map output to the strongbox to be added,
     * and passes the map input to the board to be removed
     * @throws ResourcesNotAvailableException if the resources in the map input are not available
     * @throws IllegalResourceException if the output contains illegal resources
     * @throws TooManyResourcesException if the resources in pickedResources are too many for the input of the big production
     */
    protected void activateProductions() throws ResourcesNotAvailableException, IllegalResourceException, TooManyResourcesException, UnknownFoundException {
        Map<ResourceType, Integer> input = new HashMap<>();
        Map<ResourceType, Integer> output = new HashMap<>();
        for (Production production : extraProductions) {
            if (production.checkSelected()) {
                Utilities.mergeResourceTypeMaps(input, production.getInput());
                Utilities.mergeResourceTypeMaps(output, production.getOutput());
                production.toggleSelected();
            }
        }
        for (Stack<DevelopmentCard> stack : board.getSlots().values()) {
            for (DevelopmentCard developmentCard : stack) {
                Production production = developmentCard.getProd();
                if (production.checkSelected()) {
                    Utilities.mergeResourceTypeMaps(input, production.getInput());
                    Utilities.mergeResourceTypeMaps(output, production.getOutput());
                    production.toggleSelected();
                }
            }
        }
        Production production = board.getBaseProduction();
        if (production.checkSelected()) {
            Utilities.mergeResourceTypeMaps(input, production.getInput());
            Utilities.mergeResourceTypeMaps(output, production.getOutput());
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
    protected void elaborateOutput(Map<ResourceType,Integer> output) throws IllegalResourceException {
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
    protected void checkPickedResourcesForProduction(Production p) throws ResourcesNotAvailableException, TooManyResourcesException {
        Map<ResourceType, Integer> resourcesAvailable=new HashMap<>();
        for (Map.Entry<Integer, Map<ResourceType,Integer>> entry : pickedResource.entrySet()) {
            Utilities.mergeResourceTypeMaps(resourcesAvailable,entry.getValue());
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
     *
     * @return the number of activated discounts
     */
    protected int countActivatedDiscounts(){
        int cont=0;
        for(ResourceDiscount rd : discounts){
            if(rd.isActivated())
                cont++;
        }
        return cont;
    }
    /**
     * method that tells the discount of a certain resource
     * @param res is the resourceType
     * @return the number of discounted resources
     */
    protected Integer checkDiscount(ResourceType res){
        Integer discount=0;
        for(ResourceDiscount rd : discounts){
            if(res==rd.getRes() && rd.isActivated()){
                discount=rd.getQuantity();
            }
        }
        return discount;
    }
    /**
     * check in pickedResource map if you have enough resources for buying a certain davelopment card
     * @param d is the development card
     * @throws ResourcesNotAvailableException if you have picked up less resources than the needed
     * @throws TooManyResourcesException if you have picked up more resources than the needed
     */
    protected void checkPickedResourceForDevelopmentCard(DevelopmentCard d) throws ResourcesNotAvailableException, TooManyResourcesException {
        Map<ResourceType, Integer> resourcesAvailable=new HashMap<>();
        for (Map.Entry<Integer, Map<ResourceType,Integer>> entry : pickedResource.entrySet()) {
            Utilities.mergeResourceTypeMaps(resourcesAvailable,entry.getValue());
        }
        int totResReq=0;
        int totResPick=0;
        for (ResourceRequirement rs : d.getCost()){
            int discount=checkDiscount(rs.getResource());
            int discounted;
            if(discount>rs.getQuantity())
                discounted=0;
            else discounted=rs.getQuantity()-discount;
            totResReq+=discounted;
            if (resourcesAvailable.get(rs.getResource()) < discounted){
                throw new ResourcesNotAvailableException();
            }
            if (resourcesAvailable.get(rs.getResource()) > discounted){
                throw new TooManyResourcesException();
            }
        }
        //check if there are other resources not considered
        for (Map.Entry<ResourceType, Integer> entry : resourcesAvailable.entrySet()) {
            totResPick += entry.getValue();
        }
        if(totResPick != totResReq) throw new TooManyResourcesException();
    }

    /**
     * buy the selected development card and push it on the board
     * @param d is the development card
     * @param slot is the id of the slots map on the board
     * @throws IllegalSlotException if you can't put the card in that slot
     */
    protected void buyCard(DevelopmentCard d,Integer slot) throws IllegalSlotException, TooManyResourcesException, ResourcesNotAvailableException {
        checkPickedResourceForDevelopmentCard(d);
        getBoard().pushDCard(slot,d);
        clearPickedUp();
    }

    //----------------------------------------------------

    /**
     * Transform the white resource in pending in the resource contained in whiteTo
     * @param res is the ResourceType
     */
    protected void transformWhiteIn(ResourceType res) throws NoWhiteResourceException, IllegalResourceException {
        if (whiteTo.containsKey(res)){
            int n=whiteTo.get(res);
            getBoard().getWarehouse().replaceWhiteFromPending(res,n);
        } else throw new IllegalResourceException();
    }

    /**
     * count the total amount of selected productions
     * @return the number of selected productions
     */
    protected Integer countSelectedProductions() {
        Integer cont=0;
        if (board.getBaseProduction().checkSelected()) cont++;

        for (Production production : extraProductions){
            if (production.checkSelected()) cont++;
        }

        for(Stack<DevelopmentCard> stack : board.getSlots().values()) {
            if (stack.size() > 0)
                if (stack.peek().getProd().checkSelected())
                    cont++;
        }

        return cont;
    }

    /**
     * resets all player's productions, putting back unknowns where they were
     */
    protected void resetProductions() {
        for (Production production : extraProductions) {
            production.resetProduction();
        }

        for (Stack<DevelopmentCard> stack : board.getSlots().values()) {
            for (DevelopmentCard developmentCard : stack) {
                developmentCard.getProd().resetProduction();
            }
        }

        board.getBaseProduction().resetProduction();
    }
}
