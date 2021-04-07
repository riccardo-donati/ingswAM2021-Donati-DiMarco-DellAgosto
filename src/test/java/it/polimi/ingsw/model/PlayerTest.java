package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import org.junit.jupiter.api.Test;

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
}