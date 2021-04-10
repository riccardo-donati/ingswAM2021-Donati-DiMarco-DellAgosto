package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.PopeFavorState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiplayerTest {
    Game game;

    @Test
    public void TestListPlayers() {
        game = new Multiplayer();
        game.addPlayer("Pluto");
        game.addPlayer("Pippo");
        game.addPlayer("Topotizio");
        for (Player p : game.getPlayers()){
            System.out.println(p.getNickname());
        }
    }

    @Test
    public void TestNotifyPopeFavor() {
        game=new Multiplayer();
        game.addPlayer("Pluto");
        game.addPlayer("Pippo");
        game.addPlayer("Topotizio");
        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(5);
        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(8);

        assertEquals(game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState(), PopeFavorState.ACTIVE);
        assertEquals(game.getPlayers().get(1).getBoard().getFaithPath().getPopeFavorList().get(0).getState(), PopeFavorState.ACTIVE);
        assertEquals(game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(0).getState(), PopeFavorState.DISCARDED);

        assertEquals(game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.UNACTIVE);
        assertEquals(game.getPlayers().get(1).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.UNACTIVE);
        assertEquals(game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.UNACTIVE);

        game.getPlayers().get(2).getBoard().getFaithPath().addToPosition(18);

        assertEquals(game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.DISCARDED);
        assertEquals(game.getPlayers().get(1).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.DISCARDED);
        assertEquals(game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(1).getState(), PopeFavorState.ACTIVE);

        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(12);
        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(30);

        assertEquals(game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(2).getState(), PopeFavorState.ACTIVE);
        assertEquals(game.getPlayers().get(1).getBoard().getFaithPath().getPopeFavorList().get(2).getState(), PopeFavorState.ACTIVE);
        assertEquals(game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(2).getState(), PopeFavorState.DISCARDED);

    }


}