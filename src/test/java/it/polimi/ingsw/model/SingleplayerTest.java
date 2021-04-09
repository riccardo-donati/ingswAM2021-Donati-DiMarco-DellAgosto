package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.PopeFavorState;
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
    public void TestPath(){
        s = new Singleplayer();
        s.addPlayer("Riccardo Fenati");
        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(5);
        s.pushBlackCross(9);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState(), PopeFavorState.ACTIVE);

        s.getPlayers().get(0).getBoard().getFaithPath().addToPosition(5);
        s.pushBlackCross(7);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.DISCARDED);
        assertEquals(s.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(2).getState(), PopeFavorState.UNACTIVE);
    }
}