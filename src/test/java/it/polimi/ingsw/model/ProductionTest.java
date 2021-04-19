package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.UnknownFoundException;
import it.polimi.ingsw.model.exceptions.UnknownNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductionTest {
    Production production;

    @BeforeEach
    public void initialize(){
        production = new Production();
    }

    @Test
    public void checkToggle() throws UnknownFoundException {
        assertFalse(production.checkSelected());
        production.toggleSelected();
        assertTrue(production.checkSelected());
    }

    @Test
    public void unknownFound() throws IllegalResourceException {
        production.addInput(ResourceType.UNKNOWN, 2);
        production.addOutput(ResourceType.BLUE, 1);
        assertThrows(UnknownFoundException.class, ()->production.toggleSelected());
    }

    @Test
    public void checkValidityAndReset() throws UnknownNotFoundException, IllegalResourceException {
        production.addInput(ResourceType.UNKNOWN, 2);
        production.addOutput(ResourceType.UNKNOWN, 1);
        assertFalse(production.checkValidity());
        production.replaceUnknownInput(ResourceType.GREY);
        production.replaceUnknownInput(ResourceType.GREY);
        production.replaceUnknownOutput(ResourceType.BLUE);
        assertTrue(production.checkValidity());
        production.resetProduction();
        assertFalse(production.checkValidity());
    }

    @Test
    public void unknownNotFound() throws IllegalResourceException {
        production.addInput(ResourceType.GREY, 2);
        production.addOutput(ResourceType.BLUE, 1);
        assertThrows(UnknownNotFoundException.class, ()->production.replaceUnknownInput(ResourceType.YELLOW));
    }
}