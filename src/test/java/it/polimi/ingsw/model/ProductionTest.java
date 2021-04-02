package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductionTest {
    Production p;
    @BeforeEach
    public void initialize(){
        p=new Production();
    }
    @Test
    public void checkToggle(){
        assertFalse(p.checkSelected());
        p.toggleSelected();
        assertTrue(p.checkSelected());
    }
    @Test
    public void testInput(){
        p.getInput().put(ResourceType.RED,2);
        assertTrue(p.getInput().size()>0);
        p.getInput().remove(ResourceType.RED);
        assertTrue(p.getInput().size()==0);
    }
    @Test
    public void testSetOutput(){
        p.getOutput().put(ResourceType.RED,2);
        assertTrue(p.getOutput().size()>0);
        p.getOutput().remove(ResourceType.RED);
        assertTrue(p.getOutput().size()==0);

    }

}