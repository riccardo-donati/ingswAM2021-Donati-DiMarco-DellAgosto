package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    Player player = new Player("test", true);

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
    void playLeaderCardRequirement() throws NonEmptyException, InvalidPushException {
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
    void playLeaderLevelCardRequirement() throws NonEmptyException, InvalidPushException {
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
    void addProduction() {
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
        Player.mergeResourceTypeMaps(map1, map2);
        assertEquals(map1, map3);
    }

    @Test
    public void Test1PickUpResourceWarehouse() throws FullGameException, FullSpaceException, IllegalResourceException {
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
    public void Test1PickUpResourceStrongbox() throws IllegalResourceException, ResourcesNotAvailableException {
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
    public void TestPickUpResourceMixed() throws IllegalResourceException, FullSpaceException, ResourcesNotAvailableException {
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


}