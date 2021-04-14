package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PublicInterfaceTest {
    Game game;

    @Test
    public void TestSetUpTurnMultiplayer() throws FullGameException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, NonEmptyException, UnknownNotFindException, FullSpaceException {
        game=new Multiplayer();
        game.addPlayer("AAA");
        game.addPlayer("BBB");
        game.addPlayer("CCC");
        game.startGame();
        List<List<LeaderCard>> list=game.divideLeaderCards();
        List<LeaderCard> choosen1=new ArrayList<>();
        choosen1.add(list.get(0).get(2));
        choosen1.add(list.get(0).get(1));
        assertThrows(IllegalActionException.class,
                ()->game.passTurn());
        game.chooseLeader(choosen1);
        assertThrows(UnknownNotFindException.class,
                ()->game.chooseResourceToDeposit(1, ResourceType.YELLOW));
        game.passTurn();
        List<LeaderCard> choosen2=new ArrayList<>();
        choosen2.add(list.get(1).get(3));
        choosen2.add(list.get(1).get(0));
        assertThrows(IllegalActionException.class,
                ()->game.passTurn());
        game.chooseLeader(choosen2);
        assertThrows(IllegalActionException.class,
                ()->game.passTurn());
        assertDoesNotThrow(
                ()->game.chooseResourceToDeposit(1,ResourceType.YELLOW));
        game.passTurn();
        List<LeaderCard> choosen3=new ArrayList<>();
        choosen3.add(list.get(2).get(0));
        choosen3.add(list.get(2).get(1));
        game.chooseLeader(choosen3);
        game.chooseResourceToDeposit(1,ResourceType.BLUE);
        assertThrows(UnknownNotFindException.class,
                ()->game.chooseResourceToDeposit(2,ResourceType.YELLOW));
        assertEquals(1,game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        game.passTurn();
        assertEquals(GamePhase.ONGOING,game.getGamePhase());
        assertEquals(TurnPhase.STARTTURN,game.getTurnPhase());
        assertTrue(game.getCurrPlayer().getOrder()==1);
    }
    @Test
    public void TestSetUpTurnSingleplayer() throws FullGameException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, NonEmptyException {
        game=new Singleplayer();
        game.addPlayer("Mario");
        game.startGame();
        List<List<LeaderCard>> list=game.divideLeaderCards();
        List<LeaderCard> choosen1=new ArrayList<>();
        choosen1.add(list.get(0).get(2));
        choosen1.add(list.get(0).get(1));
        assertThrows(IllegalActionException.class,
                ()->game.passTurn());
        game.chooseLeader(choosen1);
        assertThrows(UnknownNotFindException.class,
                ()->game.chooseResourceToDeposit(1, ResourceType.YELLOW));
        game.passTurn();
        assertEquals(GamePhase.ONGOING,game.getGamePhase());
        assertEquals(TurnPhase.STARTTURN,game.getTurnPhase());
        assertTrue(game.getCurrPlayer().getOrder()==1);
    }
    @Test
    public void TestTurnMultiplayer() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException {
        TestSetUpTurnMultiplayer();
        assertEquals(1,game.getCurrPlayer().getOrder());
        game.buyAtMarketInterface('c',0); //is random -> can't test
        //manual addition of resource in pending
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);

        game.depositResource(1,ResourceType.GREY);
        game.moveResource(1,2);
        assertThrows(DepositableResourceException.class,
                ()->game.discardResource(ResourceType.VIOLET));
        game.depositResource(1,ResourceType.VIOLET);
        game.moveResource(1,2);
        game.depositResource(3,ResourceType.YELLOW);
        assertDoesNotThrow(
                ()->game.discardResource(ResourceType.BLUE));
        assertEquals(ResourceType.GREY,game.getCurrPlayer().getBoard().getWarehouse().getMaindepot().get(0).getSpace()[0]);
        assertEquals(ResourceType.VIOLET,game.getCurrPlayer().getBoard().getWarehouse().getMaindepot().get(1).getSpace()[0]);

    }
}
