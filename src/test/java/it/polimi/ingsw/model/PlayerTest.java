package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.CardNotAvailableException;
import it.polimi.ingsw.model.exceptions.RequirementNotMetException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    Player player = new Player("test", true);

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

    /*@Test
    void playLeader(){
        Map<ResourceType, Integer> map = new HashMap();
        map.put(ResourceType.GREY, 5);
        map.put(ResourceType.BLUE, 2);
        player.getBoard().depositInStrongbox(map);

        LeaderCard leaderCard1 = new LeaderCard();
        LeaderCard leaderCard2 = new LeaderCard();
        leaderCard1.addRequirement(new ResourceRequirement(ResourceType.GREY, 5));
        leaderCard2.addRequirement(new ResourceRequirement(ResourceType.BLUE, 3));

        player.getLeadersInHand().add(leaderCard1);
        player.getLeadersInHand().add(leaderCard2);
        player.playLeader(leaderCard1);
        player.playLeader(leaderCard2);
    }*/
}