package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.ResourceType;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.model.enums.Color.YELLOW;
import static org.junit.jupiter.api.Assertions.*;

class LeaderCardTest {

    LeaderCard lc1 = new LeaderCard();

    @Test
    public void testInitialization() {
        assertTrue(lc1.getRequirements().isEmpty());
        assertTrue(lc1.getSpecialAbilities().isEmpty());
    }

     @Test
    public void testAbilitiesList() {
        assertTrue(lc1.getSpecialAbilities().isEmpty());
        lc1.addSpecialAbility(new Discount(ResourceType.BLUE));
        assertEquals(1, lc1.getSpecialAbilities().size());
        lc1.getSpecialAbilities().remove(0);
        assertTrue(lc1.getSpecialAbilities().isEmpty());
    }

    @Test
    public void testRequirementsList() {
        assertTrue(lc1.getRequirements().isEmpty());
        lc1.addRequirement(new LevelCardRequirement(YELLOW, 2, 3));
        lc1.addRequirement(new CardRequirement(Color.GREEN, 3));
        assertEquals(2, lc1.getRequirements().size());
        lc1.getRequirements().remove(0);
        lc1.getRequirements().remove(0);
        assertTrue((lc1.getRequirements().isEmpty()));
    }
}