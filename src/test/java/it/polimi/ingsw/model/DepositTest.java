package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullSpaceException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NonEmptyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

class DepositTest {

    Deposit depo;
    @Test
    public void TestInitialize(){
        assertThrows(IllegalResourceException.class,
                ()-> depo=new Deposit(1,ResourceType.WHITE,5));
        assertThrows(NegativeArraySizeException.class,
                ()-> depo=new Deposit(2,ResourceType.VIOLET,-1));
        assertDoesNotThrow(
                ()-> depo=new Deposit(3,ResourceType.VIOLET,5));
    }
    @Test
    public void TestAddFullResource() throws IllegalResourceException{
            depo=new Deposit(1,ResourceType.VIOLET,5);
            for(int i=0;i<5;i++){
                assertDoesNotThrow(
                        ()->depo.addResource(depo.getType()));
            }
            assertThrows(FullSpaceException.class,
                    ()->depo.addResource(depo.getType()));
    }
    @Test
    public void TestAddWrongResource() throws IllegalResourceException {
        depo=new Deposit(1,ResourceType.EMPTY,5);
        assertThrows(IllegalResourceException.class,
                ()->depo.addResource(ResourceType.WHITE));
        assertThrows(IllegalResourceException.class,
                ()->depo.addResource(ResourceType.RED));
        depo.visualize();
        assertDoesNotThrow(()->depo.addResource(ResourceType.YELLOW));
        assertThrows(IllegalResourceException.class,
                ()->depo.addResource(ResourceType.GREY));
        depo.visualize();
    }
    @Test
    public void TestRemoveType() throws IllegalResourceException, FullSpaceException {
        depo=new Deposit(1,ResourceType.EMPTY,5);
        assertEquals(depo.getType(),ResourceType.EMPTY);
        depo.addResource(ResourceType.VIOLET);
        assertEquals(depo.getType(),ResourceType.VIOLET);
        assertEquals(depo.removeResource(),ResourceType.VIOLET);
        assertEquals(depo.getType(),ResourceType.EMPTY);

    }
    @Test
    public void TestRemoveEmpty() throws IllegalResourceException {
        depo=new Deposit(1,ResourceType.EMPTY,1);
        assertEquals(depo.removeResource(),ResourceType.EMPTY);
    }
    @Test
    public void changeType() throws IllegalResourceException, NonEmptyException, FullSpaceException {
        depo=new Deposit(1,ResourceType.VIOLET,2);
        depo.changeType(ResourceType.YELLOW);
        assertEquals(depo.getType(),ResourceType.YELLOW);

        depo.addResource(ResourceType.YELLOW);
        assertThrows(NonEmptyException.class,
                ()->depo.changeType(ResourceType.VIOLET));
    }
    @Test
    public void TestFreeSpaces() throws IllegalResourceException, FullSpaceException {
        depo=new Deposit(1,ResourceType.GREY,4);
        assertEquals(4, depo.freeSpaces());
        depo.addResource(ResourceType.GREY);
        depo.addResource(ResourceType.GREY);
        assertEquals(2, depo.freeSpaces());
        depo.addResource(ResourceType.GREY);
        depo.addResource(ResourceType.GREY);
        assertEquals(0, depo.freeSpaces());
        depo.removeResource();
        assertEquals(1, depo.freeSpaces());
    }


}