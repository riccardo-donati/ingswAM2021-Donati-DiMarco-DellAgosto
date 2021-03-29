package it.polimi.ingsw.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketTest {
    Market m1;
    Market m2;
    @BeforeEach
    public void create(){
        m1=new Market();
        m2=new Market();
    }
    @Test
    public void testEquals(){
        assertEquals(m2,m1);
    }
    @Test
    public void testInitialize() {
        m1.initilizeMarket();
        m2.initilizeMarket();
        assertNotEquals(m1,m2);
    }
    @Test
    public void testExtractOutOfBoundRow(){
        assertThrows(IndexOutOfBoundsException.class,
                ()->m1.getRow(-1,new Player()));
        assertThrows(IndexOutOfBoundsException.class,
                ()->m1.getRow(3,new Player()));

    }
    @Test
    public void testExtractOutOfBoundColumn(){
        assertThrows(IndexOutOfBoundsException.class,
                ()->m1.getColumn(-1,new Player()));
        assertThrows(IndexOutOfBoundsException.class,
                ()->m1.getColumn(4,new Player()));
    }
    @Test
    public void testExtractRow(){
        for(int i=0;i<4;i++){
            m1.getRow(0,new Player());
        }
        assertEquals(m1,m2);
    }
    @Test
    public void testExtractColumn(){
        for(int i=0;i<3;i++){
            m1.getColumn(0,new Player());
        }
        assertEquals(m1,m2);
    }

}