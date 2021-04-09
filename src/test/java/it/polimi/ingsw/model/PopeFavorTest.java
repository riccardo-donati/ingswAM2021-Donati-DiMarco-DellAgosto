package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.PopeFavorState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PopeFavorTest {

    Random ran = new Random();
    int random1 = ran.nextInt(15);
    int random2 = ran.nextInt(15) + random1;
    PopeFavor popeFavor = new PopeFavor(PopeFavorState.UNACTIVE, 2, random1, random2);

    @Test
    void testChangeState() {
        assertEquals(PopeFavorState.UNACTIVE, popeFavor.getState());
        popeFavor.changeState(PopeFavorState.ACTIVE);
        assertEquals(PopeFavorState.ACTIVE, popeFavor.getState());
        popeFavor.changeState(PopeFavorState.DISCARDED);
        assertEquals(PopeFavorState.DISCARDED, popeFavor.getState());
    }

    @Test
    void testCheckInside() {
        Random ran = new Random();
        assertTrue(popeFavor.checkInside(random1));
        assertTrue(popeFavor.checkInside(random2));
        assertFalse(popeFavor.checkInside(random1 - 1));
    }
}