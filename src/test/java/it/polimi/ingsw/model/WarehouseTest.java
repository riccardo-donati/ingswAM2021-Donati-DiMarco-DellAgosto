package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullSpaceException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NonEmptyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {
    Warehouse wh;
    //WarehousePROVA wp; //da togliere
    @BeforeEach
    public void initiliaze(){
        wh=new Warehouse();
        //wp=new WarehousePROVA(); //da togliere
    }

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

        assertEquals(0,wh.countWarehousePoints());
        wh.addExtraDepot(ResourceType.BLUE);
        wh.addExtraDepot(ResourceType.YELLOW);
        wh.addExtraDepot(ResourceType.VIOLET);

        wh.addResourceInDeposit(4,ResourceType.BLUE);
        wh.addResourceInDeposit(4,ResourceType.BLUE);

        wh.addResourceInDeposit(5,ResourceType.YELLOW);
        wh.addResourceInDeposit(5,ResourceType.YELLOW);

        assertEquals(0,wh.countWarehousePoints());
        wh.addResourceInDeposit(6,ResourceType.VIOLET);
        wh.addResourceInDeposit(6,ResourceType.VIOLET);

        assertEquals(1,wh.countWarehousePoints());
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

        assertEquals(1,wh.countWarehousePoints());
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

        assertEquals(2,wh.countWarehousePoints());
        wh.visualize();
    }
    @Test
    public void TestAddFullSpaceMain() throws IllegalResourceException, FullSpaceException {
        //PROVA
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
        wh.visualize();
    }
    @Test
    public void TestMoveToEmptyMain() throws FullSpaceException, IllegalResourceException, NonEmptyException {
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.YELLOW);

        wh.addResourceInDeposit(1,ResourceType.VIOLET);
        wh.moveResource(1,2);

        wh.addResourceInDeposit(1,ResourceType.YELLOW);
        wh.visualize();

    }
    @Test
    public void TestDiscard() throws IllegalResourceException {
        wh.addResourceInPending(ResourceType.VIOLET);
        wh.addResourceInPending(ResourceType.YELLOW);

        assertDoesNotThrow(
                ()->wh.discardResource(ResourceType.VIOLET));
        assertThrows(IllegalResourceException.class,
                ()->wh.discardResource(ResourceType.VIOLET));
        wh.visualize();

    }
}