package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaderDepositTest {
    LeaderDeposit ld;

    @BeforeEach
    public void initialize(){
        ld=new LeaderDeposit(ResourceType.BLUE);
    }
    @Test
    public void insertWrongType(){
        assertThrows(IllegalResourceException.class,
                ()->ld.addResource(ResourceType.GREY,0));
    }
    @Test
    public void insertRightType(){
        assertDoesNotThrow(
                ()->ld.addResource(ResourceType.BLUE,0));
    }
    @Test
    public void insertOutOfBound(){
        assertThrows(IndexOutOfBoundsException.class,
                ()->ld.addResource(ResourceType.BLUE,-1));
        assertThrows(IndexOutOfBoundsException.class,
                ()->ld.addResource(ResourceType.BLUE,3));
    }


}