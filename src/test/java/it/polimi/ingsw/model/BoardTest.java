package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    Board b = new Board();
    Map<ResourceType, Integer> res1 = new HashMap<>();
    Map<ResourceType, Integer> res2 = new HashMap<>();
    Map<ResourceType, Integer> s1 = new HashMap<>();


    @Test
    void compareDepositResource() {
        res1.put(ResourceType.GREY,3);
        res1.put(ResourceType.YELLOW,1);
        res1.put(ResourceType.BLUE,2);

        res2.put(ResourceType.VIOLET,1);
        res2.put(ResourceType.BLUE,1);

        b.depositInStrongbox(res1);
        b.depositInStrongbox(res2);

        s1.put(ResourceType.BLUE, 3);
        s1.put(ResourceType.GREY, 3);
        s1.put(ResourceType.VIOLET,1);
        s1.put(ResourceType.YELLOW, 1);

        assertEquals(s1,(b.getStrongBox()));
    }

    @Test
    void BoardCounterPoints() throws IllegalResourceException, FullSpaceException,  IllegalSlotException {
        List<ResourceRequirement> c = new ArrayList<>();
        ResourceRequirement r = new ResourceRequirement(ResourceType.BLUE, 2);
        c.add(r);

        FaithPath f = b.getFaithPath();

        Warehouse wh = b.getWarehouse();

        DevelopmentCard dev = new DevelopmentCard(c,1,Color.VIOLET,null,3);  // result 3
        b.pushDCard(1, dev);

        f.addToPosition(16);                                                                     //result 9

        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.VIOLET);

        wh.addExtraDepot(ResourceType.YELLOW);
        wh.addExtraDepot(ResourceType.VIOLET);
        wh.addExtraDepot(ResourceType.BLUE);

        wh.addResourceInDeposit(3,ResourceType.BLUE);
        wh.addResourceInDeposit(3,ResourceType.BLUE);
        wh.addResourceInDeposit(3,ResourceType.BLUE);
        wh.addResourceInDeposit(1,ResourceType.YELLOW);
        wh.addResourceInDeposit(2,ResourceType.VIOLET);
        wh.addResourceInDeposit(2,ResourceType.VIOLET);

        wh.addResourceInDeposit(4,ResourceType.YELLOW);
        wh.addResourceInDeposit(4,ResourceType.YELLOW);
        wh.addResourceInDeposit(5,ResourceType.VIOLET);
        wh.addResourceInDeposit(6,ResourceType.BLUE);                 // result 2

        assertEquals(14, b.countBoardsPoints());                //somma pari alle dcards + faithpath + warehouse
    }

    @Test
    public void TestBaseProd() throws ResourcesNotAvailableException, UnknownFindException {
        assertFalse(b.getBaseProduction().checkSelected() || b.getBaseProduction().checkValidity());
        b.getBaseProduction().removeInput(ResourceType.UNKNOWN,2);
        b.getBaseProduction().removeOutput(ResourceType.UNKNOWN,1);
        assertTrue(b.getBaseProduction().checkValidity());
        b.getBaseProduction().addInput(ResourceType.YELLOW,1);
        b.getBaseProduction().toggleSelected();
        assertTrue(b.getBaseProduction().checkSelected() && b.getBaseProduction().checkValidity());

    }
}