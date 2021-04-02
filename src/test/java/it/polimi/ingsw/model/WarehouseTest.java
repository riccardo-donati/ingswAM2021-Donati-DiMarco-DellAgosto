package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {
    Warehouse wh;
    @BeforeEach
    public void initiliaze(){
        wh=new Warehouse();
    }

    @Test
    public void TestaddIllegalResource(){
        assertThrows(IllegalResourceException.class,
                ()->wh.addResourceInPending(ResourceType.RED));
        assertDoesNotThrow(
                ()->wh.addResourceInPending(ResourceType.BLUE)
        );
    }
    @Test
    public void TestaddIllegalExtraDepot(){
        assertThrows(IllegalResourceException.class,
                ()->wh.addExtraDepot(ResourceType.RED));
        assertDoesNotThrow(
                ()->wh.addExtraDepot(ResourceType.BLUE)
        );
    }
    @Test
    public void TestExtraDepCountWarehousePoints() throws IllegalResourceException {
        wh.addExtraDepot(ResourceType.BLUE);
        wh.addExtraDepot(ResourceType.YELLOW);
        wh.addExtraDepot(ResourceType.VIOLET);

        wh.getExtradepots().get(0).addResource(ResourceType.BLUE,0);
        wh.getExtradepots().get(0).addResource(ResourceType.BLUE,1);

        wh.getExtradepots().get(1).addResource(ResourceType.YELLOW,0);
        wh.getExtradepots().get(1).addResource(ResourceType.YELLOW,1);

        wh.getExtradepots().get(2).addResource(ResourceType.VIOLET,0);
        wh.getExtradepots().get(2).addResource(ResourceType.VIOLET,1);

        assertEquals(1,wh.countWarehousePoints());

    }
    @Test
    public void TestMainDepCountWarehousePoints()  {
        //to do
    }
    @Test
    public void TestMixedDepCountWarehousePoints(){
        //to do
    }
    @Test
    public void TestAddPending(){
        //to do
    }
}