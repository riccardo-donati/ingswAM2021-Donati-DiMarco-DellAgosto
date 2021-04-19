package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.PopeFavorState;
import it.polimi.ingsw.model.exceptions.FullGameException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.interfaces.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingleplayerTest {
    Game s;


    @Test
    public void TestJSONDevelopmentCard() {
        s = new Singleplayer();
        assertEquals(s.getTokens().size(), 7);
        for (Token t : s.getTokens()) {
            assertNotNull(t);
        }
    }

    @Test
    public void TestPath() throws FullGameException, IllegalResourceException {
        s = new Singleplayer();
        s.addPlayer("Riccardo Ricevuti");
        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(5);
        s.pushBlackCross(9);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState(), PopeFavorState.ACTIVE);

        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(5);
        s.pushBlackCross(7);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.DISCARDED);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(2).getState(), PopeFavorState.UNACTIVE);
    }

    @Test
    public void TestPath2() throws FullGameException, IllegalResourceException {                                                        //la bCross raggiunge prima la fine, ma il player è nella zona quindi deve essere attivo
        s = new Singleplayer();                                                     //il player raggiung eil secondo stato e si attiva, ma la croce raggiunge lo stato finale
        s.addPlayer("Giacomo del Luglio");
        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(6);
        s.pushBlackCross(8);
        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(11);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState(), PopeFavorState.ACTIVE);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.ACTIVE);

        s.pushBlackCross(17);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(2).getState(), PopeFavorState.DISCARDED);
    }

    @Test
    public void TestPath3() throws FullGameException, IllegalResourceException {
        s = new Singleplayer();
        s.addPlayer("Davide di Davide");            //mi assicuro che anche se il player passi sulle caselle lo stato non cambi
        s.pushBlackCross(23);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState(), PopeFavorState.DISCARDED);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.DISCARDED);
        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(8);
        assertFalse(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState().equals(PopeFavorState.ACTIVE));
        assertFalse(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState().equals(PopeFavorState.UNACTIVE));
        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(15);
        assertFalse(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState().equals(PopeFavorState.ACTIVE));
        assertFalse(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState().equals(PopeFavorState.UNACTIVE));
        s.pushBlackCross(3);                                //la bcross vince ma il player è nella zona
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(2).getState(), PopeFavorState.ACTIVE);
    }

    @Test
    public void TestCheckWinner4() throws FullGameException, IllegalResourceException {                   //expected antonio and score 20 faith + 9 Pope = 29
        s = new Singleplayer();
        s.addPlayer("Antoniolo Papillo");
        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(16);
        s.pushBlackCross(23);
        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(32);
    }

}