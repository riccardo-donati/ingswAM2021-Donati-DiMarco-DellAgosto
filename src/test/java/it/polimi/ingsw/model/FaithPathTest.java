package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FaithPathTest {

    FaithPath faithPath = new FaithPath();

    @Test
    void testInitialization() {
        assertEquals(0, faithPath.getPosition());
    }

    @Test
    void testVictoryPointsWithoutPopeFavor() {
        int position = faithPath.getPosition();
        if(position >= 0 && position <= 2)
            assertEquals(0, faithPath.countFaithPoints());
        else if(position >= 3 && position <= 5)
            assertEquals(1, faithPath.countFaithPoints());
        else if(position >= 6 && position <= 8)
            assertEquals(2, faithPath.countFaithPoints());
        else if(position >= 9 && position <= 11)
            assertEquals(4, faithPath.countFaithPoints());
        else if(position >= 12 && position <= 14)
            assertEquals(6, faithPath.countFaithPoints());
        else if(position >= 15 && position <= 17)
            assertEquals(9, faithPath.countFaithPoints());
        else if(position >= 18 && position <= 20)
            assertEquals(12, faithPath.countFaithPoints());
        else if(position >= 21 && position <= 23)
            assertEquals(16, faithPath.countFaithPoints());
        else assertEquals(20, faithPath.countFaithPoints());
    }

    @Test
    void testChangePosition() {
        assertEquals(0, faithPath.getPosition());
        Random ran = new Random();
        int random = ran.nextInt(5);
        faithPath.addToPosition(random);
        assertEquals(random, faithPath.getPosition() );
    }

    @Test
    void testOutOfBoundsPosition() {
        assertEquals(0, faithPath.getPosition());
        faithPath.addToPosition(100);
        //assertEquals(24, faithPath.getPosition());
    }


    @Test
    void countFaithPoints() {
    }
}