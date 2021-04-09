package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.CardNotAvailableException;
import it.polimi.ingsw.model.exceptions.RequirementNotMetException;
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
    void playLeaderCardRequirement() {
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(ResourceType.GREY, 1));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 3));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 1, Color.BLUE, new Production(), 2));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 1, Color.YELLOW, new Production(), 2));

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
    void playLeaderLevelCardRequirement() {
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(ResourceType.GREY, 1));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 3));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 2, Color.BLUE, new Production(), 2));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 2));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 2, Color.VIOLET, new Production(), 2));
        player.getBoard().pushDCard(1, new DevelopmentCard(requirements, 3, Color.YELLOW, new Production(), 2));

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
}