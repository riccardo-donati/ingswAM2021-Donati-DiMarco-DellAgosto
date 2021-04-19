package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    Player player = new Player("test");

    @Test
    void chooseLeader() {
        List<LeaderCard> leaderCards = new ArrayList<>();
        leaderCards.add(new LeaderCard());
        leaderCards.add(new LeaderCard());

        assertTrue(player.getLeadersInHand().isEmpty());
        assertDoesNotThrow(()->player.chooseLeaders(leaderCards));
        assertEquals(2, player.getLeadersInHand().size());
    }

    @Test
    void chooseLeaderException() {
        player.getLeadersInHand().add(new LeaderCard());
        List<LeaderCard> leaderCards = new ArrayList<>();
        leaderCards.add(new LeaderCard());
        leaderCards.add(new LeaderCard());

        assertThrows(Exception.class, ()->player.chooseLeaders(leaderCards));
    }

    @Test
    void chooseLeaderSameCardsException() {
        LeaderCard leaderCard = new LeaderCard();
        List<LeaderCard> leaderCards = new ArrayList<>();
        leaderCards.add(leaderCard);
        leaderCards.add(leaderCard);

        assertThrows(Exception.class, ()->player.chooseLeaders(leaderCards));
    }

    @Test
    void playLeaderResourceRequirement() {
        Map<ResourceType, Integer> map = new HashMap<>();
        map.put(ResourceType.GREY, 5);
        map.put(ResourceType.BLUE, 2);
        player.getBoard().depositInStrongbox(map);

        LeaderCard leaderCard1 = new LeaderCard();
        LeaderCard leaderCard2 = new LeaderCard();
        leaderCard1.addRequirement(new ResourceRequirement(ResourceType.GREY, 5));
        leaderCard2.addRequirement(new ResourceRequirement(ResourceType.BLUE, 3));

        player.getLeadersInHand().add(leaderCard1);
        player.getLeadersInHand().add(leaderCard2);
        assertTrue(player.getLeadersInGame().isEmpty());
        assertEquals(2, player.getLeadersInHand().size());

        assertDoesNotThrow(()->player.playLeader(leaderCard1));
        assertThrows(RequirementNotMetException.class, ()->player.playLeader(leaderCard2));
        assertEquals(1, player.getLeadersInHand().size());
        assertEquals(1, player.getLeadersInGame().size());
    }

    @Test
    void playLeaderFaithRequirement() {
        player.getBoard().getFaithPath().addToPosition(17);

        LeaderCard leaderCard1 = new LeaderCard();
        LeaderCard leaderCard2 = new LeaderCard();
        leaderCard1.addRequirement(new FaithRequirement(17));
        leaderCard2.addRequirement(new FaithRequirement(18));

        player.getLeadersInHand().add(leaderCard1);
        player.getLeadersInHand().add(leaderCard2);
        assertTrue(player.getLeadersInGame().isEmpty());
        assertEquals(2, player.getLeadersInHand().size());

        assertDoesNotThrow(()->player.playLeader(leaderCard1));
        assertThrows(RequirementNotMetException.class, ()->player.playLeader(leaderCard2));
        assertEquals(1, player.getLeadersInHand().size());
        assertEquals(1, player.getLeadersInGame().size());
    }

    @Test
    void playLeaderCardRequirement() throws IllegalSlotException {
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(ResourceType.GREY, 1));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 3));
        player.getBoard().pushDCard(2, new DevelopmentCard(requirements, 1, Color.BLUE, new Production(), 2));
        player.getBoard().pushDCard(3, new DevelopmentCard(requirements, 1, Color.YELLOW, new Production(), 2));

        LeaderCard leaderCard1 = new LeaderCard();
        LeaderCard leaderCard2 = new LeaderCard();
        leaderCard1.addRequirement(new CardRequirement(Color.BLUE, 1));
        leaderCard1.addRequirement(new CardRequirement(Color.GREEN, 1));
        leaderCard2.addRequirement(new CardRequirement(Color.YELLOW, 1));
        leaderCard2.addRequirement(new CardRequirement(Color.GREEN, 2));

        player.getLeadersInHand().add(leaderCard1);
        player.getLeadersInHand().add(leaderCard2);
        assertTrue(player.getLeadersInGame().isEmpty());
        assertEquals(2, player.getLeadersInHand().size());

        assertDoesNotThrow(()->player.playLeader(leaderCard1));
        assertThrows(RequirementNotMetException.class, ()->player.playLeader(leaderCard2));
        assertEquals(1, player.getLeadersInHand().size());
        assertEquals(1, player.getLeadersInGame().size());
    }

    @Test
    void playLeaderLevelCardRequirement() throws IllegalSlotException {
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(ResourceType.GREY, 1));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 3));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 2, Color.BLUE, new Production(), 2));
        player.getBoard().pushDCard(2, new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 2));
        player.getBoard().pushDCard(2, new DevelopmentCard(requirements, 2, Color.VIOLET, new Production(), 2));
        player.getBoard().pushDCard(2, new DevelopmentCard(requirements, 3, Color.YELLOW, new Production(), 2));

        LeaderCard leaderCard1 = new LeaderCard();
        LeaderCard leaderCard2 = new LeaderCard();
        leaderCard1.addRequirement(new LevelCardRequirement(Color.YELLOW, 1, 3));
        leaderCard1.addRequirement(new LevelCardRequirement(Color.GREEN, 2, 1));
        leaderCard2.addRequirement(new LevelCardRequirement(Color.BLUE, 1, 2));
        leaderCard2.addRequirement(new LevelCardRequirement(Color.VIOLET, 2, 2));

        player.getLeadersInHand().add(leaderCard1);
        player.getLeadersInHand().add(leaderCard2);
        assertTrue(player.getLeadersInGame().isEmpty());
        assertEquals(2, player.getLeadersInHand().size());

        assertDoesNotThrow(()->player.playLeader(leaderCard1));
        assertThrows(RequirementNotMetException.class, ()->player.playLeader(leaderCard2));
        assertEquals(1, player.getLeadersInHand().size());
        assertEquals(1, player.getLeadersInGame().size());
    }

    @Test
    void playLeaderCardNotAvailableException() {
        LeaderCard leaderCard = new LeaderCard();
        assertThrows(CardNotAvailableException.class, ()->player.playLeader(leaderCard));
        assertEquals(0, player.getLeadersInHand().size());
        assertEquals(0, player.getLeadersInGame().size());
    }

    @Test
    void addDiscount() {
        Discount discount = new Discount(ResourceType.BLUE);
        discount.activate(player);
        assertEquals(1, player.getDiscounts().get(ResourceType.BLUE));
        discount.activate(player);
        assertEquals(2, player.getDiscounts().get(ResourceType.BLUE));
        discount = new Discount(ResourceType.GREY);
        discount.activate(player);
        assertEquals(1, player.getDiscounts().get(ResourceType.GREY));
    }

    @Test
    void addWhiteTo() {
        WhiteTo whiteTo = new WhiteTo(ResourceType.GREY);
        whiteTo.activate(player);
        assertEquals(1, player.getWhiteTo().get(ResourceType.GREY));
        whiteTo.activate(player);
        assertEquals(2, player.getWhiteTo().get(ResourceType.GREY));
        whiteTo = new WhiteTo(ResourceType.BLUE);
        whiteTo.activate(player);
        assertEquals(1, player.getWhiteTo().get(ResourceType.BLUE));
    }

    @Test
    void addProduction() throws IllegalResourceException {
        ExtraProduction extraProduction = new ExtraProduction(ResourceType.YELLOW);
        extraProduction.activate(player);
        assertEquals(1, player.getExtraProductions().size());
        extraProduction.activate(player);
        assertEquals(2, player.getExtraProductions().size());
    }

    @Test
    void mergeProductionMaps() {
        Map<ResourceType, Integer> map1 = new HashMap<>();
        Map<ResourceType, Integer> map2 = new HashMap<>();
        Map<ResourceType, Integer> map3 = new HashMap<>();

        map1.put(ResourceType.BLUE, 2);
        map1.put(ResourceType.GREY, 5);
        map2.put(ResourceType.YELLOW, 4);
        map2.put(ResourceType.GREY, 1);
        map3.put(ResourceType.BLUE, 2);
        map3.put(ResourceType.GREY, 6);
        map3.put(ResourceType.YELLOW, 4);
        Utilities.mergeResourceTypeMaps(map1, map2);
        assertEquals(map1, map3);
    }

    @Test
    public void Test1PickUpResourceWarehouse() throws FullSpaceException, IllegalResourceException {
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);

        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.BLUE);
        player.getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.GREY);
        int nres=player.getBoard().getWarehouse().countWarehouseResource();
        assertDoesNotThrow(
                ()->{
                    player.pickUpResourceFromWarehouse(2);
                    player.pickUpResourceFromWarehouse(3);
                }
        );
        assertEquals(nres-2,player.getBoard().getWarehouse().countWarehouseResource());
        player.getBoard().getWarehouse().visualize();

        player.revertPickUp();
        assertEquals(nres,player.getBoard().getWarehouse().countWarehouseResource());
        player.getBoard().getWarehouse().visualize();
    }

    @Test
    public void Test2PickUpResourceWarehouse() throws IllegalResourceException, FullSpaceException {
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);

        player.getBoard().getWarehouse().addExtraDepot(ResourceType.GREY);

        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.BLUE);
        player.getBoard().getWarehouse().addResourceInDeposit(4,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(4,ResourceType.GREY);
        int nres=player.getBoard().getWarehouse().countWarehouseResource();
        player.getBoard().getWarehouse().visualize();
        assertDoesNotThrow(
                ()->{
                    player.pickUpResourceFromWarehouse(2);
                    player.pickUpResourceFromWarehouse(4);
                    player.pickUpResourceFromWarehouse(4);
                    player.pickUpResourceFromWarehouse(1);
                }
        );
        assertThrows(ResourcesNotAvailableException.class,
                ()->player.pickUpResourceFromWarehouse(3));
        assertEquals(nres-4,player.getBoard().getWarehouse().countWarehouseResource());
        player.getBoard().getWarehouse().visualize();

        player.revertPickUp();
        assertEquals(nres,player.getBoard().getWarehouse().countWarehouseResource());
        player.getBoard().getWarehouse().visualize();
    }

    @Test
    public void Test1PickUpResourceStrongbox() throws IllegalResourceException, ResourcesNotAvailableException, FullSpaceException {
        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.getBoard().addResourceInStrongbox(ResourceType.GREY);
        player.getBoard().addResourceInStrongbox(ResourceType.BLUE);
        player.getBoard().addResourceInStrongbox(ResourceType.BLUE);
        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);


        assertThrows(ResourcesNotAvailableException.class,
                ()->player.pickUpResourceFromStrongbox(ResourceType.VIOLET) );
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.GREY);
        assertEquals(1,player.getBoard().getStrongBox().get(ResourceType.YELLOW));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.GREY));
        assertEquals(2,player.getBoard().getStrongBox().get(ResourceType.BLUE));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.VIOLET));

        player.revertPickUp();
        assertEquals(2,player.getBoard().getStrongBox().get(ResourceType.YELLOW));
        assertEquals(1,player.getBoard().getStrongBox().get(ResourceType.GREY));
        assertEquals(2,player.getBoard().getStrongBox().get(ResourceType.BLUE));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.VIOLET));
    }

    @Test
    public void TestPickUpResourceMixed() throws IllegalResourceException, FullSpaceException, ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException {
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);

        player.getBoard().getWarehouse().addExtraDepot(ResourceType.GREY);

        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.VIOLET);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.BLUE);
        player.getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(4,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(4,ResourceType.GREY);

        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.getBoard().addResourceInStrongbox(ResourceType.GREY);
        player.getBoard().addResourceInStrongbox(ResourceType.BLUE);
        player.getBoard().addResourceInStrongbox(ResourceType.BLUE);
        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);

        int nres=player.getBoard().getWarehouse().countWarehouseResource();

        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromWarehouse(1);
        assertThrows(ResourcesNotAvailableException.class,
                ()->{
                    player.pickUpResourceFromWarehouse(1);
                    player.pickUpResourceFromStrongbox(ResourceType.VIOLET);
                });
        player.pickUpResourceFromStrongbox(ResourceType.GREY);
        player.pickUpResourceFromStrongbox(ResourceType.BLUE);
        player.pickUpResourceFromWarehouse(4);
        player.pickUpResourceFromWarehouse(4);

        assertEquals(1,player.getBoard().getStrongBox().get(ResourceType.YELLOW));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.GREY));
        assertEquals(1,player.getBoard().getStrongBox().get(ResourceType.BLUE));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.VIOLET));
        assertEquals(nres-3,player.getBoard().getWarehouse().countWarehouseResource());
        player.getBoard().getWarehouse().visualize();

        player.revertPickUp();
        assertEquals(2,player.getBoard().getStrongBox().get(ResourceType.YELLOW));
        assertEquals(1,player.getBoard().getStrongBox().get(ResourceType.GREY));
        assertEquals(2,player.getBoard().getStrongBox().get(ResourceType.BLUE));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.VIOLET));
        assertEquals(nres,player.getBoard().getWarehouse().countWarehouseResource());
        player.getBoard().getWarehouse().visualize();
    }

    @Test
    public void TestBuyLv1() throws IllegalResourceException, FullSpaceException, ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException {
        List<ResourceRequirement> cost=new ArrayList<>();
        cost.add(new ResourceRequirement(ResourceType.YELLOW,2));
        cost.add(new ResourceRequirement(ResourceType.GREY,1));
        DevelopmentCard d=new DevelopmentCard(cost,1,Color.GREEN,new Production(),5);

        player.getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);

        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.VIOLET);

        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);

        player.pickUpResourceFromWarehouse(1);
        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);

        assertDoesNotThrow(
                ()->player.buyCard(d,1)
        );
        assertEquals(d,player.getBoard().getSlots().get(1).get(0));
    }

    @Test
    public void TestIllegalBuyLv1() throws FullSpaceException, IllegalResourceException, ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException {
        List<ResourceRequirement> cost=new ArrayList<>();
        cost.add(new ResourceRequirement(ResourceType.YELLOW,2));
        cost.add(new ResourceRequirement(ResourceType.GREY,1));
        DevelopmentCard d=new DevelopmentCard(cost,1,Color.GREEN,new Production(),5);

        player.getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);

        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.VIOLET);

        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);

        player.pickUpResourceFromWarehouse(1);
        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);

        player.getBoard().getSlots().get(1).push(d);
        assertThrows(IllegalSlotException.class,
                ()->player.buyCard(d,1)
        );
    }

    @Test
    public void TestBuyLv2() throws IllegalResourceException, ResourcesNotAvailableException, FullSpaceException, DepositNotExistingException, NonEmptyException {
        List<ResourceRequirement> cost=new ArrayList<>();
        cost.add(new ResourceRequirement(ResourceType.YELLOW,2));
        cost.add(new ResourceRequirement(ResourceType.GREY,1));
        DevelopmentCard d1=new DevelopmentCard(cost,1,Color.GREEN,new Production(),5);
        DevelopmentCard d2=new DevelopmentCard(cost,2,Color.VIOLET,new Production(),10);

        player.getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);

        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.VIOLET);

        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);

        player.pickUpResourceFromWarehouse(1);
        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);

        player.getBoard().getSlots().get(1).push(d1);
        assertThrows(IllegalSlotException.class,
                ()-> player.buyCard(d2,2));
        assertDoesNotThrow(
                ()-> player.buyCard(d2,1));
        assertEquals(d2,player.getBoard().getSlots().get(1).get(1));
    }

    @Test
    public void TestBuyLv3() throws IllegalResourceException, ResourcesNotAvailableException, FullSpaceException, DepositNotExistingException, NonEmptyException {
        List<ResourceRequirement> cost=new ArrayList<>();
        cost.add(new ResourceRequirement(ResourceType.YELLOW,2));
        cost.add(new ResourceRequirement(ResourceType.GREY,1));
        DevelopmentCard d11=new DevelopmentCard(cost,1,Color.GREEN,new Production(),5);
        DevelopmentCard d12=new DevelopmentCard(cost,1,Color.BLUE,new Production(),5);
        DevelopmentCard d2=new DevelopmentCard(cost,2,Color.VIOLET,new Production(),10);
        DevelopmentCard d3=new DevelopmentCard(cost,3,Color.YELLOW,new Production(),15);

        player.getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);

        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.VIOLET);

        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);

        player.pickUpResourceFromWarehouse(1);
        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);

        assertThrows(IllegalSlotException.class,
                ()->player.buyCard(d3,1));
        player.getBoard().getSlots().get(1).push(d11);
        assertThrows(IllegalSlotException.class,
                ()->player.buyCard(d3,1));
        player.getBoard().getSlots().get(3).push(d12);
        player.getBoard().getSlots().get(3).push(d2);
        assertDoesNotThrow(
                ()->player.buyCard(d3,3));
        assertEquals(d3,player.getBoard().getSlots().get(3).get(2));
        assertEquals(d3,player.getBoard().getSlots().get(3).pop());
    }

    @Test
    public void TestBuyCostTooFew() throws IllegalResourceException, FullSpaceException, ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException {
        List<ResourceRequirement> cost=new ArrayList<>();
        cost.add(new ResourceRequirement(ResourceType.VIOLET,1));
        cost.add(new ResourceRequirement(ResourceType.YELLOW,1));
        cost.add(new ResourceRequirement(ResourceType.BLUE,1));
        DevelopmentCard d1=new DevelopmentCard(cost,1,Color.GREEN,new Production(),5);

        player.getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);

        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);

        player.getBoard().getWarehouse().addExtraDepot(ResourceType.BLUE);

        player.getBoard().getWarehouse().addResourceInDeposit(4,ResourceType.BLUE);

        player.getBoard().addResourceInStrongbox(ResourceType.VIOLET);

        assertThrows(ResourcesNotAvailableException.class,
                ()->player.buyCard(d1,1));
        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromWarehouse(4);
        player.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        assertDoesNotThrow(
                ()->player.buyCard(d1,1));
        assertEquals(d1,player.getBoard().getSlots().get(1).get(0));
    }

    @Test
    public void TestBuyCostTooMany1() throws ResourcesNotAvailableException, FullSpaceException, IllegalResourceException, DepositNotExistingException, NonEmptyException {
        List<ResourceRequirement> cost=new ArrayList<>();
        cost.add(new ResourceRequirement(ResourceType.VIOLET,1));
        cost.add(new ResourceRequirement(ResourceType.YELLOW,1));
        cost.add(new ResourceRequirement(ResourceType.BLUE,1));
        DevelopmentCard d1=new DevelopmentCard(cost,1,Color.GREEN,new Production(),5);

        player.getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);

        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);

        player.getBoard().getWarehouse().addExtraDepot(ResourceType.BLUE);

        player.getBoard().getWarehouse().addResourceInDeposit(4,ResourceType.BLUE);

        player.getBoard().addResourceInStrongbox(ResourceType.VIOLET);

        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromWarehouse(4);
        player.pickUpResourceFromWarehouse(1);
        player.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        assertThrows(TooManyResourcesException.class,
                ()->player.buyCard(d1,3));
        player.revertPickUp();
        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromWarehouse(4);
        player.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        assertDoesNotThrow(
                ()->player.buyCard(d1,3));
        assertEquals(d1,player.getBoard().getSlots().get(3).get(0));
    }

    @Test
    public void TestBuyCostTooMany2() throws ResourcesNotAvailableException, FullSpaceException, IllegalResourceException, DepositNotExistingException, NonEmptyException {
        List<ResourceRequirement> cost=new ArrayList<>();
        cost.add(new ResourceRequirement(ResourceType.GREY,2));
        cost.add(new ResourceRequirement(ResourceType.VIOLET,2));
        DevelopmentCard d1=new DevelopmentCard(cost,1,Color.GREEN,new Production(),5);

        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);

        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.GREY);
        player.getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.YELLOW);

        player.getBoard().getWarehouse().addExtraDepot(ResourceType.VIOLET);

        player.getBoard().getWarehouse().addResourceInDeposit(4,ResourceType.VIOLET);

        player.getBoard().addResourceInStrongbox(ResourceType.VIOLET);
        player.getBoard().addResourceInStrongbox(ResourceType.VIOLET);
        player.getBoard().addResourceInStrongbox(ResourceType.GREY);
        player.getBoard().addResourceInStrongbox(ResourceType.GREY);

        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromWarehouse(4);
        player.pickUpResourceFromWarehouse(1);
        player.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        assertThrows(TooManyResourcesException.class,
                ()->player.buyCard(d1,3));
        player.revertPickUp();
        player.pickUpResourceFromStrongbox(ResourceType.GREY);
        player.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        player.pickUpResourceFromStrongbox(ResourceType.GREY);
        player.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        assertDoesNotThrow(
                ()->player.buyCard(d1,3));
        assertEquals(d1,player.getBoard().getSlots().get(3).get(0));
    }

    @Test
    public void TestPickedForProd() throws IllegalResourceException, FullSpaceException, ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException {
        Production p=new Production();
        p.addInput(ResourceType.YELLOW,2);
        p.addInput(ResourceType.BLUE,1);
        p.addOutput(ResourceType.RED,3);

        player.getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);

        player.getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        player.getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.BLUE);
        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.getBoard().addResourceInStrongbox(ResourceType.VIOLET);
        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);

        assertThrows(ResourcesNotAvailableException.class,
                ()->player.checkPickedResourcesForProduction(p));
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromWarehouse(3);
        assertThrows(TooManyResourcesException.class,
                ()->player.checkPickedResourcesForProduction(p));
        player.revertPickUp();
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromWarehouse(2);
        player.pickUpResourceFromWarehouse(3);
        assertThrows(TooManyResourcesException.class,
                ()->player.checkPickedResourcesForProduction(p));
        player.revertPickUp();
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromWarehouse(3);
        assertDoesNotThrow(
                ()->player.checkPickedResourcesForProduction(p));
    }

    @Test
    public void TestActivate1CardProduction() throws IllegalResourceException, ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException, UnknownFoundException {
        List<ResourceRequirement> cost=new ArrayList<>();
        cost.add(new ResourceRequirement(ResourceType.GREY,1));
        Production p=new Production();
        p.addInput(ResourceType.GREY,1);
        p.addInput(ResourceType.BLUE,1);
        p.addOutput(ResourceType.RED,2);
        p.addOutput(ResourceType.YELLOW,3);
        DevelopmentCard d1=new DevelopmentCard(cost,1,Color.GREEN,p,5);

        player.getBoard().addResourceInStrongbox(ResourceType.GREY);
        player.pickUpResourceFromStrongbox(ResourceType.GREY);
        player.buyCard(d1,1);

        player.getBoard().addResourceInStrongbox(ResourceType.GREY);
        player.getBoard().addResourceInStrongbox(ResourceType.BLUE);

        player.pickUpResourceFromStrongbox(ResourceType.GREY);
        player.pickUpResourceFromStrongbox(ResourceType.BLUE);

        player.getBoard().getSlots().get(1).get(0).getProd().toggleSelected();

        player.activateProductions();
        assertEquals(2,player.getBoard().getFaithPath().getPosition());
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.BLUE));
        assertEquals(3,player.getBoard().getStrongBox().get(ResourceType.YELLOW));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.GREY));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.VIOLET));

    }

    @Test
    public void TestActivateBaseProduction() throws IllegalResourceException, ResourcesNotAvailableException, TooManyResourcesException, UnknownNotFoundException, UnknownFoundException {
        assertThrows(UnknownFoundException.class,
                ()->player.getBoard().getBaseProduction().toggleSelected());
        player.getBoard().getBaseProduction().replaceUnknownInput(ResourceType.YELLOW);
        player.getBoard().getBaseProduction().replaceUnknownInput(ResourceType.YELLOW);
        player.getBoard().getBaseProduction().replaceUnknownOutput(ResourceType.GREY);
        assertDoesNotThrow(
                ()->player.getBoard().getBaseProduction().toggleSelected());

        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);

        player.activateProductions();
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.BLUE));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.YELLOW));
        assertEquals(1,player.getBoard().getStrongBox().get(ResourceType.GREY));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.VIOLET));

    }

    @Test
    public void TestActivateExtraProduction() throws IllegalResourceException, ResourcesNotAvailableException, TooManyResourcesException, UnknownNotFoundException, UnknownFoundException {
        Production p=new Production();
        p.addInput(ResourceType.YELLOW,1);
        p.addOutput(ResourceType.RED,1);
        p.addOutput(ResourceType.UNKNOWN,1);
        player.addExtraProduction(p);
        assertThrows(UnknownFoundException.class,
                ()->player.getExtraProductions().get(0).toggleSelected());
        player.getExtraProductions().get(0).replaceUnknownOutput(ResourceType.BLUE);
        assertDoesNotThrow(
                ()->player.getExtraProductions().get(0).toggleSelected());

        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);

        player.activateProductions();
        assertEquals(1,player.getBoard().getFaithPath().getPosition());
        assertEquals(1,player.getBoard().getStrongBox().get(ResourceType.BLUE));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.YELLOW));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.GREY));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.VIOLET));
    }

    @Test
    public void TestActivateBaseAndCardAndExtraProd() throws ResourcesNotAvailableException, IllegalResourceException, UnknownFoundException, TooManyResourcesException, IllegalSlotException, UnknownNotFoundException {
        //----Card----
        List<ResourceRequirement> cost=new ArrayList<>();
        cost.add(new ResourceRequirement(ResourceType.GREY,1));
        Production p=new Production();
        p.addInput(ResourceType.BLUE,1);
        p.addInput(ResourceType.BLUE,1);
        p.addOutput(ResourceType.RED,2);
        p.addOutput(ResourceType.VIOLET,3);
        DevelopmentCard d1=new DevelopmentCard(cost,1,Color.GREEN,p,5);

        player.getBoard().addResourceInStrongbox(ResourceType.GREY);
        player.pickUpResourceFromStrongbox(ResourceType.GREY);
        player.buyCard(d1,1);

        player.getBoard().addResourceInStrongbox(ResourceType.BLUE);
        player.getBoard().addResourceInStrongbox(ResourceType.BLUE);

        player.pickUpResourceFromStrongbox(ResourceType.BLUE);
        player.pickUpResourceFromStrongbox(ResourceType.BLUE);

        player.getBoard().getSlots().get(1).get(0).getProd().toggleSelected();
        //----Base----
        assertThrows(UnknownFoundException.class,
                ()->player.getBoard().getBaseProduction().toggleSelected());
        player.getBoard().getBaseProduction().replaceUnknownInput(ResourceType.YELLOW);
        player.getBoard().getBaseProduction().replaceUnknownInput(ResourceType.BLUE);
        player.getBoard().getBaseProduction().replaceUnknownOutput(ResourceType.GREY);
        assertDoesNotThrow(
                ()->player.getBoard().getBaseProduction().toggleSelected());

        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.getBoard().addResourceInStrongbox(ResourceType.BLUE);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.BLUE);

        //----Extra----
        Production p2=new Production();
        p2.addInput(ResourceType.YELLOW,1);
        p2.addOutput(ResourceType.RED,1);
        p2.addOutput(ResourceType.UNKNOWN,1);
        player.addExtraProduction(p2);
        assertThrows(UnknownFoundException.class,
                ()->player.getExtraProductions().get(0).toggleSelected());
        player.getExtraProductions().get(0).replaceUnknownOutput(ResourceType.BLUE);
        assertDoesNotThrow(
                ()->player.getExtraProductions().get(0).toggleSelected());

        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);

        player.activateProductions();
        assertEquals(3,player.getBoard().getFaithPath().getPosition());
        assertEquals(1,player.getBoard().getStrongBox().get(ResourceType.BLUE));
        assertEquals(0,player.getBoard().getStrongBox().get(ResourceType.YELLOW));
        assertEquals(1,player.getBoard().getStrongBox().get(ResourceType.GREY));
        assertEquals(3,player.getBoard().getStrongBox().get(ResourceType.VIOLET));

    }

    @Test
    public void TestCountSelectedProd() throws UnknownNotFoundException, UnknownFoundException, IllegalResourceException, ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException {
        player.getBoard().getBaseProduction().replaceUnknownInput(ResourceType.YELLOW);
        player.getBoard().getBaseProduction().replaceUnknownInput(ResourceType.BLUE);
        player.getBoard().getBaseProduction().replaceUnknownOutput(ResourceType.YELLOW);
        player.getBoard().getBaseProduction().toggleSelected();
        player.getExtraProductions().add(new Production());
        player.getExtraProductions().add(new Production());
        //player.getExtraProductions().get(0).toggleSelected();
        player.getExtraProductions().get(1).toggleSelected();
        List<ResourceRequirement> l=new ArrayList<>();
        l.add(new ResourceRequirement(ResourceType.YELLOW,1));
        DevelopmentCard d=new DevelopmentCard(l,1,Color.BLUE,new Production(),5);
        DevelopmentCard d2=new DevelopmentCard(l,2,Color.BLUE,new Production(),10);
        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.buyCard(d,1);
        player.getBoard().addResourceInStrongbox(ResourceType.YELLOW);
        player.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        player.buyCard(d2,1);
        player.getBoard().getSlots().get(1).get(0).getProd().toggleSelected();
        player.getBoard().getSlots().get(1).get(1).getProd().toggleSelected();

        assertEquals(3,player.countSelectedProductions());
    }

    @Test
    public void TestTotalResources() throws FullSpaceException, IllegalResourceException {
        Player player = new Player("Riki001");
        Utilities.fillDeposits(player, true);
        assertEquals(4003, player.getBoard().countTotalResources());
    }
}