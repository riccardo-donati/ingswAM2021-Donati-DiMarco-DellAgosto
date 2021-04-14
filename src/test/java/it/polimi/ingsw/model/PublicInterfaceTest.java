package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        List<LeaderCard> chosen1=new ArrayList<>();
        chosen1.add(list.get(0).get(2));
        chosen1.add(list.get(0).get(1));
        assertThrows(IllegalActionException.class,
                ()->game.passTurn());
        game.chooseLeader(chosen1);
        assertThrows(UnknownNotFindException.class,
                ()->game.chooseResourceToDeposit(1, ResourceType.YELLOW));
        game.passTurn();

        List<LeaderCard> chosen2=new ArrayList<>();
        chosen2.add(list.get(1).get(3));
        chosen2.add(list.get(1).get(0));
        assertThrows(IllegalActionException.class,
                ()->game.passTurn());
        game.chooseLeader(chosen2);
        assertThrows(IllegalActionException.class,
                ()->game.passTurn());
        assertDoesNotThrow(
                ()->game.chooseResourceToDeposit(1,ResourceType.YELLOW));
        game.passTurn();

        List<LeaderCard> chosen3=new ArrayList<>();
        chosen3.add(list.get(2).get(0));
        chosen3.add(list.get(2).get(1));
        game.chooseLeader(chosen3);
        game.chooseResourceToDeposit(1,ResourceType.BLUE);
        assertThrows(UnknownNotFindException.class,
                ()->game.chooseResourceToDeposit(2,ResourceType.YELLOW));
        assertEquals(1,game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        game.passTurn();

        assertEquals(GamePhase.ONGOING,game.getGamePhase());
        assertEquals(TurnPhase.STARTTURN,game.getTurnPhase());
        assertEquals(1, (int) game.getCurrPlayer().getOrder());
    }
    @Test
    public void TestSetUpTurnSingleplayer() throws FullGameException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, NonEmptyException {
        game=new Singleplayer();
        game.addPlayer("Mario");
        game.startGame();
        List<List<LeaderCard>> list=game.divideLeaderCards();

        List<LeaderCard> chosen1=new ArrayList<>();
        chosen1.add(list.get(0).get(2));
        chosen1.add(list.get(0).get(1));
        assertThrows(IllegalActionException.class,
                ()->game.passTurn());
        game.chooseLeader(chosen1);
        assertThrows(UnknownNotFindException.class,
                ()->game.chooseResourceToDeposit(1, ResourceType.YELLOW));
        game.passTurn();

        assertEquals(GamePhase.ONGOING,game.getGamePhase());
        assertEquals(TurnPhase.STARTTURN,game.getTurnPhase());
        assertEquals(1, (int) game.getCurrPlayer().getOrder());
    }

    // probably better to test this with smaller tests
    /*@Test
    public void TestTurnMultiplayer() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException {
        TestSetUpTurnMultiplayer();
        assertEquals(1,game.getCurrPlayer().getOrder());
        game.buyAtMarketInterface('c',0); //is random -> can't test
        //manual addition of resource in pending
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        //System.out.println(game.getCurrPlayer().getBoard().getWarehouse().getPendingResources());

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
    }*/

    @Test
    public void TestOneWhiteTo() throws NonEmptyException, EmptyPlayersException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, IllegalResourceException, FullGameException {
        TestSetUpTurnMultiplayer();
        // forcing conversion special ability
        game.getCurrPlayer().getWhiteTo().put(ResourceType.BLUE, 1);
        game.setMarket(new Market());            // market not initialized (known structure)
        game.buyAtMarketInterface('c', 0);       // from market: 1 GREY, 1 VIOLET, 1 BLUE (white converted)
        assertEquals(GamePhase.ONGOING, game.getGamePhase());
        assertEquals(TurnPhase.DEPOSITPHASE, game.getTurnPhase());

        game.depositResource(1, ResourceType.GREY);
        game.moveResource(1, 2);
        assertThrows(DepositableResourceException.class, ()->game.discardResource(ResourceType.BLUE));
        game.depositResource(1, ResourceType.BLUE);
        game.depositResource(3, ResourceType.VIOLET);
        assertDoesNotThrow(()->game.passTurn());
    }

    @Test
    public void TestTwoWhiteTo() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException, NoWhiteResourceException {
        TestSetUpTurnMultiplayer();
        // forcing conversion special ability
        game.getCurrPlayer().getWhiteTo().put(ResourceType.BLUE, 1);
        game.getCurrPlayer().getWhiteTo().put(ResourceType.GREY, 1);

        game.setMarket(new Market());           // market not initialized (known structure)
        game.buyAtMarketInterface('c', 0);       // from market: 1 GREY, 1 VIOLET, 1 WHITE (white not converted)
        assertEquals(GamePhase.ONGOING, game.getGamePhase());
        assertEquals(TurnPhase.DEPOSITPHASE, game.getTurnPhase());

        game.depositResource(1, ResourceType.GREY);
        game.depositResource(3, ResourceType.VIOLET);
        assertThrows(IllegalResourceException.class, ()->game.depositResource(3, ResourceType.BLUE));
        game.transformWhiteIn(ResourceType.BLUE);
        assertDoesNotThrow(()->game.depositResource(2, ResourceType.BLUE));
        assertDoesNotThrow(()->game.passTurn());
    }

    @Test
    public void TestBaseProduction() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException, ResourcesNotAvailableException {
        TestSetUpTurnMultiplayer();

        //artificially adding resources to player's warehouse
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(2, ResourceType.BLUE);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(2, ResourceType.BLUE);

        //set up base production
        assertThrows(UnknownFindException.class, ()->game.toggleBaseProd());
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInOutputBaseProduction(ResourceType.GREY);
        assertDoesNotThrow(()->game.toggleBaseProd());

        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);

        assertDoesNotThrow(()->game.activateProductions());
        assertDoesNotThrow(()->game.passTurn());
    }

}
