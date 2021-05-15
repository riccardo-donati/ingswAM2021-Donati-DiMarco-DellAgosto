package it.polimi.ingsw.controller;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    ControllerTOELIMINATE c=new ControllerTOELIMINATE();

    @Test
    public void TestInitializeMulti(){
        c.initializeGame(0); //illegal value
        c.initializeGame(4);
        c.initializeGame(2); //already initialized

        List<String> nicks=new ArrayList<>();
        nicks.add("a");
        nicks.add("b");
        nicks.add(""); //empty
        nicks.add("a"); //already taken
        nicks.add("c");
        nicks.add("d");
        nicks.add("e"); //'e' is not added
        c.addPlayers(nicks);

        assertEquals(4,c.getGame().getPlayers().size());
    }
    @Test
    public void TestInitializeSingle(){
        List<String> nicks=new ArrayList<>();
        nicks.add("a");
        nicks.add("b"); //'b' is not added
        c.addPlayers(nicks); //game not initialized
        c.initializeGame(1);
        c.addPlayers(nicks);

        assertEquals(1,c.getGame().getPlayers().size());
    }

    @Test
    public void TestStart(){
        c.start(); //not initialized
        c.initializeGame(3);
        List<String> nicks=new ArrayList<>();
        nicks.add("a");
        nicks.add("b");
        nicks.add("c");
        c.addPlayers(nicks);
        c.start();
        assertEquals(3,c.getNickOrderMap().size());
    }

    @Test
    public void TestCurrentPlayer(){
        c.getCurrentPlayer();
        c.initializeGame(1);
        List<String> nicks=new ArrayList<>();
        nicks.add("a");
        c.getCurrentPlayer();
        c.addPlayers(nicks);
        c.start();
        assertEquals("a",c.getCurrentPlayer());

    }

}
