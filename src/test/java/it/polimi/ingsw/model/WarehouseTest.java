package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {
    Warehouse wh=new Warehouse();

    @Test
    public void TestAddIllegalResource(){
        assertThrows(IllegalResourceException.class,
                ()->wh.addResourceInPending(ResourceType.RED));
        assertDoesNotThrow(
                ()->wh.addResourceInPending(ResourceType.BLUE)
        );
    }

    @Test
    public void TestAddIllegalExtraDepot(){
        assertThrows(IllegalResourceException.class,
                ()->wh.addExtraDepot(ResourceType.RED));
        assertDoesNotThrow(
                ()->wh.addExtraDepot(ResourceType.BLUE)
        );
    }

    @Test
    public void TestExtraDepCountWarehousePoints() throws IllegalResourceException, FullSpaceException {
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.VIOLET);

        assertEquals(0,wh.countWarehouseResource());
        wh.addExtraDepot(ResourceType.BLUE);
        wh.addExtraDepot(ResourceType.YELLOW);
        wh.addExtraDepot(ResourceType.VIOLET);

        wh.addResourceInDeposit(4,ResourceType.BLUE);
        wh.addResourceInDeposit(4,ResourceType.BLUE);

        wh.addResourceInDeposit(5,ResourceType.YELLOW);
        wh.addResourceInDeposit(5,ResourceType.YELLOW);

        assertEquals(4,wh.countWarehouseResource());
        wh.addResourceInDeposit(6,ResourceType.VIOLET);
        wh.addResourceInDeposit(6,ResourceType.VIOLET);

        assertEquals(6,wh.countWarehouseResource());
        wh.visualize();

    }

    @Test
    public void TestDoubleSwap() throws IllegalResourceException, FullSpaceException {
        wh.addResourceInPending(ResourceType.GREY);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.VIOLET);

        wh.addResourceInDeposit(1,ResourceType.GREY);
        wh.addResourceInDeposit(2,ResourceType.YELLOW);
        wh.addResourceInDeposit(3,ResourceType.VIOLET);
        wh.addResourceInDeposit(3,ResourceType.VIOLET);

        assertDoesNotThrow(
                ()->wh.moveResource(2,3)
        );
        assertEquals(ResourceType.GREY,wh.getMaindepot().get(0).getSpace()[0]);
        assertEquals(ResourceType.VIOLET ,wh.getMaindepot().get(1).getSpace()[0]);
        assertEquals(ResourceType.VIOLET ,wh.getMaindepot().get(1).getSpace()[1]);
        assertEquals(ResourceType.YELLOW ,wh.getMaindepot().get(2).getSpace()[0]);
        wh.visualize();


    }

    @Test
    public void TestMainDepCountWarehousePoints() throws IllegalResourceException, FullSpaceException {
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.VIOLET);

        wh.addResourceInDeposit(3,ResourceType.BLUE);
        wh.addResourceInDeposit(3,ResourceType.BLUE);
        wh.addResourceInDeposit(3,ResourceType.BLUE);
        wh.addResourceInDeposit(1,ResourceType.YELLOW);
        wh.addResourceInDeposit(2,ResourceType.VIOLET);
        wh.addResourceInDeposit(2,ResourceType.VIOLET);

        assertEquals(6,wh.countWarehouseResource());
        wh.visualize();

    }

    @Test
    public void TestMixedDepCountWarehousePoints() throws IllegalResourceException, FullSpaceException {
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
        wh.addResourceInDeposit(6,ResourceType.BLUE);

        assertEquals(10,wh.countWarehouseResource());
        wh.visualize();
    }

    @Test
    public void TestRemove() throws IllegalResourceException, FullSpaceException, DepositNotExistingException, NonEmptyException {
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
        wh.addResourceInDeposit(6,ResourceType.BLUE);

        wh.removeResourceFromDeposit(5);
        wh.removeResourceFromDeposit(4);
        wh.removeResourceFromDeposit(1);

        assertEquals(ResourceType.VIOLET,wh.getExtradepots().get(1).getType());
        assertEquals(ResourceType.YELLOW,wh.getExtradepots().get(0).getType());
        assertEquals(ResourceType.EMPTY,wh.getMaindepot().get(0).getType());
        wh.visualize();
    }

    @Test
    public void TestAddFullSpaceMain() throws IllegalResourceException, FullSpaceException {
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.GREY);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addResourceInDeposit(1,ResourceType.YELLOW);
        wh.addResourceInDeposit(2,ResourceType.GREY);

        assertThrows(FullSpaceException.class,
                ()->wh.addResourceInDeposit(1,ResourceType.YELLOW));
        wh.visualize();
    }

    @Test
    public void TestSwapMain() throws IllegalResourceException, FullSpaceException, NonEmptyException {
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.GREY);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addResourceInDeposit(1,ResourceType.YELLOW);
        wh.addResourceInDeposit(2,ResourceType.GREY);
        wh.moveResource(1,2);
        assertDoesNotThrow(
                ()->wh.addResourceInDeposit(2,ResourceType.YELLOW));
        assertEquals(ResourceType.GREY,wh.getMaindepot().get(0).getSpace()[0]);
        assertEquals(ResourceType.YELLOW,wh.getMaindepot().get(1).getSpace()[0]);
        assertEquals(ResourceType.YELLOW,wh.getMaindepot().get(1).getSpace()[1]);
        wh.visualize();

    }

    @Test
    public void TestIllegalResource() throws FullSpaceException, IllegalResourceException {
        assertThrows(IllegalResourceException.class,
                ()->wh.addResourceInPending(ResourceType.RED));
        assertThrows(IllegalResourceException.class,
                ()->wh.addResourceInDeposit(1,ResourceType.RED));
        assertThrows(IllegalResourceException.class,
                ()->wh.addResourceInDeposit(1,ResourceType.YELLOW));
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInDeposit(1,ResourceType.YELLOW);
        assertThrows(IllegalResourceException.class,
                ()->wh.addResourceInDeposit(1,ResourceType.YELLOW));
        wh.visualize();
    }

    @Test
    public void TestAddExtraDeposit() throws IllegalResourceException {
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.GREY);

        wh.addExtraDepot(ResourceType.YELLOW);
        wh.addExtraDepot(ResourceType.GREY);

        assertDoesNotThrow(
                ()->wh.addResourceInDeposit(4,ResourceType.YELLOW));
        assertDoesNotThrow(
                ()->wh.addResourceInDeposit(5,ResourceType.GREY));
        assertEquals(ResourceType.YELLOW,wh.getExtradepots().get(0).getSpace()[0]);
        assertEquals(ResourceType.GREY,wh.getExtradepots().get(1).getSpace()[0]);
        wh.visualize();
    }

    @Test
    public void TestAddFullSpaceExtra() throws IllegalResourceException {
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addExtraDepot(ResourceType.YELLOW);
        assertDoesNotThrow(
                ()->wh.addResourceInDeposit(4,ResourceType.YELLOW));
        assertDoesNotThrow(
                ()->wh.addResourceInDeposit(4,ResourceType.YELLOW));
        assertThrows(FullSpaceException.class,
                ()->wh.addResourceInDeposit(4,ResourceType.YELLOW));
        wh.visualize();
    }

    @Test
    public void TestMoveExtra() throws IllegalResourceException {
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addExtraDepot(ResourceType.YELLOW);

        assertDoesNotThrow(
                ()->wh.addResourceInDeposit(1,ResourceType.YELLOW));
        assertDoesNotThrow(
                ()->wh.addResourceInDeposit(4,ResourceType.YELLOW));
        assertDoesNotThrow(
                ()->wh.moveResource(1,4));
        assertEquals(ResourceType.YELLOW,wh.getExtradepots().get(0).getSpace()[0]);
        assertEquals(ResourceType.EMPTY,wh.getMaindepot().get(0).getSpace()[0]);
        wh.visualize();
    }

    @Test
    public void TestMoveToEmptyExtra() throws IllegalResourceException, FullSpaceException {
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInDeposit(1,ResourceType.VIOLET);

        wh.addExtraDepot(ResourceType.VIOLET);
        wh.addExtraDepot(ResourceType.YELLOW);

        assertDoesNotThrow(
                ()->wh.moveResource(1,2));
        assertThrows(IllegalResourceException.class,
                ()->wh.moveResource(2,5));
        assertDoesNotThrow(
                ()->wh.moveResource(2,4));
        assertEquals(ResourceType.VIOLET,wh.getExtradepots().get(0).getSpace()[0]);
        assertEquals(ResourceType.EMPTY,wh.getMaindepot().get(0).getSpace()[0]);
        assertEquals(ResourceType.EMPTY,wh.getMaindepot().get(1).getSpace()[0]);
        wh.visualize();
    }

    @Test
    public void TestMoveToEmptyMain() throws FullSpaceException, IllegalResourceException, NonEmptyException {
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addResourceInDeposit(1,ResourceType.VIOLET);
        wh.moveResource(1,2);

        wh.addResourceInDeposit(1,ResourceType.YELLOW);
        assertEquals(ResourceType.YELLOW,wh.getMaindepot().get(0).getSpace()[0]);
        assertEquals(ResourceType.VIOLET,wh.getMaindepot().get(1).getSpace()[0]);
        wh.visualize();

    }

    @Test
    public void SingleSwapExtra() throws IllegalResourceException, FullSpaceException, NonEmptyException {
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addExtraDepot(ResourceType.YELLOW);

        wh.addResourceInDeposit(2,ResourceType.YELLOW);
        wh.addResourceInDeposit(2,ResourceType.YELLOW);

        wh.moveResource(2,4);
        assertEquals(ResourceType.YELLOW,wh.getMaindepot().get(1).getSpace()[0]);
        assertEquals(ResourceType.EMPTY,wh.getMaindepot().get(1).getSpace()[1]);
        assertEquals(ResourceType.YELLOW,wh.getExtradepots().get(0).getSpace()[0]);
        assertEquals(ResourceType.EMPTY,wh.getExtradepots().get(0).getSpace()[1]);

        wh.moveResource(2,4);
        assertEquals(ResourceType.EMPTY,wh.getMaindepot().get(1).getSpace()[0]);
        assertEquals(ResourceType.EMPTY,wh.getMaindepot().get(1).getSpace()[1]);
        assertEquals(ResourceType.YELLOW,wh.getExtradepots().get(0).getSpace()[0]);
        assertEquals(ResourceType.YELLOW,wh.getExtradepots().get(0).getSpace()[1]);
        wh.visualize();
    }

    @Test
    public void TestDiscardExtraDep() throws IllegalResourceException, FullSpaceException, DepositableResourceException {
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.GREY);
        wh.addResourceInPending(ResourceType.GREY);
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addExtraDepot(ResourceType.GREY);
        wh.addExtraDepot(ResourceType.YELLOW);

        wh.addResourceInDeposit(1,ResourceType.VIOLET);
        wh.addResourceInDeposit(2,ResourceType.GREY);
        wh.discardResource(ResourceType.VIOLET);
        wh.addResourceInDeposit(4,ResourceType.GREY);
        wh.addResourceInDeposit(5,ResourceType.YELLOW);
        assertThrows(DepositableResourceException.class,
                ()->wh.discardResource(ResourceType.YELLOW));
        assertThrows(DepositableResourceException.class,
                ()->wh.discardResource(ResourceType.BLUE));


    }

    @Test
    public void TestDiscard() throws IllegalResourceException, FullSpaceException {
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.GREY);
        wh.addResourceInPending(ResourceType.BLUE);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addResourceInDeposit(1,ResourceType.GREY);
        wh.addResourceInDeposit(2,ResourceType.BLUE);
        wh.addResourceInDeposit(3,ResourceType.YELLOW);
        assertThrows(DepositableResourceException.class,
                ()->wh.discardResource(ResourceType.YELLOW));
        assertDoesNotThrow(
                ()->wh.discardResource(ResourceType.VIOLET));
        assertThrows(IllegalResourceException.class,
                ()->wh.discardResource(ResourceType.VIOLET));
        wh.visualize();
    }

    @Test
    public void TestRemoveAddExtra() throws IllegalResourceException, FullSpaceException, DepositNotExistingException, NonEmptyException {
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addExtraDepot(ResourceType.VIOLET);
        wh.addResourceInDeposit(4,ResourceType.VIOLET);
        wh.removeResourceFromDeposit(4);
        assertThrows(IllegalResourceException.class,
                ()-> wh.addResourceInDeposit(4,ResourceType.YELLOW));
        wh.visualize();

    }

    @Test
    public void TestSameResourceMain() throws IllegalResourceException, FullSpaceException {
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInPending(ResourceType.YELLOW);
        wh.addResourceInDeposit(1,ResourceType.YELLOW);
        assertThrows(IllegalResourceException.class,
                ()->wh.addResourceInDeposit(2,ResourceType.YELLOW));
        wh.visualize();

    }
}