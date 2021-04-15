package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
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
    public void TestBaseProduction() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException, ResourcesNotAvailableException, DepositNotExistingException {
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

    @Test
    public void TestExtraProduction() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException, ResourcesNotAvailableException, DepositNotExistingException {
        TestSetUpTurnMultiplayer();

        //artificially adding resources to player's warehouse
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(3, ResourceType.GREY);

        //artificially adding extra production
        ExtraProduction extraProduction = new ExtraProduction(ResourceType.GREY);
        extraProduction.activate(game.getCurrPlayer());

        assertThrows(UnknownFindException.class, ()->game.toggleExtraProd(0));
        game.substituteUnknownInOutputExtraProduction(0, ResourceType.YELLOW);
        assertDoesNotThrow(()->game.toggleExtraProd(0));

        game.pickUpResourceFromWarehouse(3);

        assertDoesNotThrow(()->game.activateProductions());
        assertDoesNotThrow(()->game.passTurn());
    }

    @Test
    public void TestRegularProduction() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException, InvalidPushException {
        TestSetUpTurnMultiplayer();

        //artificially adding resources to player's strongbox
        Map<ResourceType, Integer> depositInStrongbox = new HashMap<>();
        depositInStrongbox.put(ResourceType.GREY, 3);
        game.getCurrPlayer().getBoard().depositInStrongbox(depositInStrongbox);

        //artificially adding development card
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(ResourceType.GREY, 1));
        Production production = new Production();
        production.addInput(ResourceType.GREY, 1);
        production.addOutput(ResourceType.RED, 4);
        game.getCurrPlayer().getBoard().pushDCard(1, new DevelopmentCard(requirements, 1, Color.GREEN, production, 3));

        assertDoesNotThrow(()->game.toggleCardProd(1));
        assertThrows(ResourcesNotAvailableException.class, ()->game.pickUpResourceFromStrongbox(ResourceType.YELLOW));
        assertDoesNotThrow(()->game.pickUpResourceFromStrongbox(ResourceType.GREY));
        assertDoesNotThrow(()->game.activateProductions());
        assertEquals(4, game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        assertDoesNotThrow(()->game.passTurn());
    }

    @Test
    public void TestBuyDevelopmentCard() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException, ResourcesNotAvailableException {
        TestSetUpTurnMultiplayer();

        //artificially adding resources to player's strongbox
        Map<ResourceType, Integer> depositInStrongbox = new HashMap<>();
        depositInStrongbox.put(ResourceType.YELLOW, 1);
        game.getCurrPlayer().getBoard().depositInStrongbox(depositInStrongbox);

        //artificially adding a development card on the stack in position [0][0]
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(ResourceType.YELLOW, 1));
        DevelopmentCard developmentCard = new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 3);
        game.getCardMatrix()[0][0].push(developmentCard);

        assertThrows(IllegalActionException.class, ()->game.buyCard(0, 0, 1));
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        assertDoesNotThrow(()->game.buyCard(0, 0, 1));
        assertEquals(TurnPhase.ENDTURN, game.getTurnPhase());
        assertDoesNotThrow(()->game.passTurn());
    }

    @Test
    public void TestPlayAndDiscardLeader() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException {
        TestSetUpTurnMultiplayer();

        game.getCurrPlayer().getLeadersInHand().remove(0);
        game.getCurrPlayer().getLeadersInHand().remove(0);

        LeaderCard leaderCard = new LeaderCard();
        leaderCard.addRequirement(new ResourceRequirement(ResourceType.YELLOW, 3));
        game.getCurrPlayer().getLeadersInHand().add(leaderCard);
        game.getCurrPlayer().getLeadersInHand().add(new LeaderCard());

        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(2, ResourceType.YELLOW);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(2, ResourceType.YELLOW);
        Map<ResourceType, Integer> depositInStrongbox = new HashMap<>();
        depositInStrongbox.put(ResourceType.YELLOW, 1);
        game.getCurrPlayer().getBoard().depositInStrongbox(depositInStrongbox);

        assertDoesNotThrow(()->game.playLeader(0));
        assertEquals(TurnPhase.STARTTURN, game.getTurnPhase());

        assertDoesNotThrow(()->game.discardLeader(0));
        assertEquals(1, game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        assertEquals(TurnPhase.STARTTURN, game.getTurnPhase());
    }
    public void fillDeposits(Player p,boolean warehouse) throws IllegalResourceException, FullSpaceException {
        Map<ResourceType, Integer> resMap=new HashMap<>();
        resMap.put(ResourceType.YELLOW,999);
        resMap.put(ResourceType.BLUE,999);
        resMap.put(ResourceType.GREY,999);
        resMap.put(ResourceType.VIOLET,999);
        p.getBoard().depositInStrongbox(resMap);

        if(warehouse) {
            p.getBoard().getWarehouse().getMaindepot().get(0).addResource(ResourceType.YELLOW);
            p.getBoard().getWarehouse().getMaindepot().get(1).addResource(ResourceType.BLUE);
            p.getBoard().getWarehouse().getMaindepot().get(1).addResource(ResourceType.BLUE);
            p.getBoard().getWarehouse().getMaindepot().get(2).addResource(ResourceType.GREY);
            p.getBoard().getWarehouse().getMaindepot().get(2).addResource(ResourceType.GREY);
            p.getBoard().getWarehouse().getMaindepot().get(2).addResource(ResourceType.GREY);

            p.getBoard().getWarehouse().addExtraDepot(ResourceType.VIOLET);
            p.getBoard().getWarehouse().addExtraDepot(ResourceType.YELLOW);
            p.getBoard().getWarehouse().addExtraDepot(ResourceType.GREY);
            p.getBoard().getWarehouse().addExtraDepot(ResourceType.BLUE);

            p.getBoard().getWarehouse().getExtradepots().get(0).addResource(ResourceType.VIOLET);
            p.getBoard().getWarehouse().visualize();
        }
    }
    @Test
    public void TestSingleRevert() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullGameException, FullSpaceException, ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException, UnknownNotFindException, UnknownFindException, DepositNotExistingException {
        TestSetUpTurnSingleplayer();
        fillDeposits(game.getCurrPlayer(),true);
        game.initializeCardMatrixForTests();
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.revertPickUp();
        assertEquals(TurnPhase.STARTTURN,game.getTurnPhase());
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        assertEquals(TurnPhase.PICKUPPHASE,game.getTurnPhase());
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.pickUpResourceFromWarehouse(2); //blue
        game.substituteUnknownInOutputBaseProduction(ResourceType.YELLOW);
        game.toggleBaseProd();
        game.activateProductions();
        assertEquals(998,game.getCurrPlayer().getBoard().getStrongBox().get(ResourceType.BLUE));
        assertEquals(1000,game.getCurrPlayer().getBoard().getStrongBox().get(ResourceType.YELLOW));
        game.passTurn();
    }
    @Test
    public void TestSingleEvolution() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullGameException, FullSpaceException, CardNotAvailableException, RequirementNotMetException, ResourcesNotAvailableException, DepositNotExistingException {
        TestSetUpTurnSingleplayer();
        fillDeposits(game.getCurrPlayer(),true);
        game.discardLeader(0);
        assertEquals(1,game.getBlackCrossFaithPath().getPosition());
        LeaderCard ld=new LeaderCard(5);
        ld.addSpecialAbility(new ExtraDeposit(ResourceType.YELLOW));
        game.getCurrPlayer().getLeadersInHand().add(ld);
        game.getCurrPlayer().playLeader(game.getCurrPlayer().getLeadersInHand().get(1));
        game.moveResource(1,8);
        game.getCurrPlayer().getBoard().getWarehouse().visualize();
        game.pickUpResourceFromWarehouse(8);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.revertPickUp();
        game.setMarket(new Market());
        game.buyAtMarketInterface('r',0);
        game.passTurn();
        //aggiungi token ordinati
    }
    @Test
    public void TestMultiEvolution() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFindException, FullGameException, ResourcesNotAvailableException {
        TestSetUpTurnMultiplayer();
        fillDeposits(game.getCurrPlayer(),false);
        Market m =new Market();
        game.setMarket(m);
        game.buyAtMarketInterface('c',1);
        game.depositResource(1,ResourceType.GREY);
        game.depositResource(3,ResourceType.VIOLET);
        game.moveResource(3,1);
        game.moveResource(1,2);
        game.getCurrPlayer().getBoard().getWarehouse().visualize();
        game.passTurn();
        assertEquals(2,game.getCurrPlayer().getOrder());

    }



}



