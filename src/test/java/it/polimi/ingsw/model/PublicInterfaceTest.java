package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.Token;
import org.junit.jupiter.api.Test;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PublicInterfaceTest {
    Game game;

    @Test
    public void TestSetUpTurnMultiplayer() throws FullGameException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, NonEmptyException, UnknownNotFoundException, FullSpaceException {
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
        assertThrows(UnknownNotFoundException.class,
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
        assertThrows(UnknownNotFoundException.class,
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
        assertThrows(UnknownNotFoundException.class,
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
    public void TestOneWhiteTo() throws NonEmptyException,  IllegalActionException, FullSpaceException, IllegalResourceException, IOException {
        game=Utilities.loadGame("setUpSingle",'s');
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
    public void TestTwoWhiteTo() throws  IllegalResourceException, IllegalActionException, FullSpaceException, NoWhiteResourceException, IOException {
        game=Utilities.loadGame("setUpMulti",'m');
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
    public void TestBaseProduction() throws NonEmptyException, IllegalResourceException, IllegalActionException, FullSpaceException, UnknownNotFoundException, ResourcesNotAvailableException, DepositNotExistingException, IOException {
        game=Utilities.loadGame("setUpMulti",'m');
        //artificially adding resources to player's warehouse
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(2, ResourceType.BLUE);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(2, ResourceType.BLUE);

        //set up base production
        assertThrows(UnknownFoundException.class, ()->game.toggleBaseProd());
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInOutputBaseProduction(ResourceType.GREY);
        assertDoesNotThrow(()->game.toggleBaseProd());

        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);

        assertDoesNotThrow(()->game.activateProductions());
        assertFalse(game.getCurrPlayer().getBoard().getBaseProduction().checkSelected());
        assertDoesNotThrow(()->game.passTurn());
    }

    @Test
    public void TestExtraProduction() throws NonEmptyException, IllegalResourceException,  IllegalActionException, FullSpaceException, UnknownNotFoundException, ResourcesNotAvailableException, DepositNotExistingException, IOException {
        game=Utilities.loadGame("setUpMulti",'m');

        //artificially adding resources to player's warehouse
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(3, ResourceType.GREY);

        //artificially adding extra production
        ExtraProduction extraProduction = new ExtraProduction(ResourceType.GREY);
        extraProduction.activate(game.getCurrPlayer());

        assertThrows(UnknownFoundException.class, ()->game.toggleExtraProd(0));
        game.substituteUnknownInOutputExtraProduction(0, ResourceType.YELLOW);
        assertDoesNotThrow(()->game.toggleExtraProd(0));

        game.pickUpResourceFromWarehouse(3);

        assertDoesNotThrow(()->game.activateProductions());
        assertFalse(game.getCurrPlayer().getExtraProductions().get(0).checkSelected());
        assertDoesNotThrow(()->game.passTurn());
    }

    @Test
    public void TestRegularProduction() throws IOException, IllegalSlotException, IllegalResourceException {
        game=Utilities.loadGame("setUpMulti",'m');

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
    public void TestBuyDevelopmentCard() throws  IllegalActionException, ResourcesNotAvailableException, IOException {
        game=Utilities.loadGame("setUpMulti",'m');

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
    public void TestPlayAndDiscardLeader() throws  IllegalResourceException, FullSpaceException,  IOException {
        game=Utilities.loadGame("setUpMulti",'m');

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


    @Test
    public void TestSingleRevert() throws NonEmptyException, IllegalResourceException, IllegalActionException, FullSpaceException, ResourcesNotAvailableException, TooManyResourcesException, UnknownNotFoundException, UnknownFoundException, DepositNotExistingException, IOException {
        game=Utilities.loadGame("setUpSingle",'s');
        Utilities.fillDeposits(game.getCurrPlayer(),true);
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
    public void TestSingleEvolutionDiscard() throws NonEmptyException, IllegalResourceException, IllegalActionException, FullSpaceException, CardNotAvailableException, RequirementNotMetException, ResourcesNotAvailableException, DepositNotExistingException, IOException {
        game=Utilities.loadGame("setUpSingle",'s');
        Utilities.fillDeposits(game.getCurrPlayer(),true);

        //i manually push a discard yellow token on top of the stack
        game.orderTokenStack();
        game.getTokenStack().push(game.getTokenStack().remove(0));

        game.discardLeader(0);
        assertEquals(1,game.getCurrPlayer().getBoard().getFaithPath().getPosition());
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
        assertEquals(2, game.getCardMatrix()[0][2].size());
        assertEquals(6,game.getTokenStack().size());
    }
    @Test
    public void TestSingleEvolutionPushShuffle() throws NonEmptyException, IllegalResourceException, IllegalActionException, FullSpaceException, CardNotAvailableException, RequirementNotMetException, ResourcesNotAvailableException, DepositNotExistingException, IOException {
        game=Utilities.loadGame("setUpSingle",'s');
        Utilities.fillDeposits(game.getCurrPlayer(),true);

        //i manually set a  pushShuffleToken on top of the stack
        game.orderTokenStack();

        game.discardLeader(0);
        assertEquals(1,game.getCurrPlayer().getBoard().getFaithPath().getPosition());
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
        assertEquals(1, game.getBlackCrossFaithPath().getPosition());
        assertEquals(7,game.getTokenStack().size());
    }
    @Test
    public void TestSingleEvolutionPush() throws NonEmptyException, IllegalResourceException, IllegalActionException, FullSpaceException, CardNotAvailableException, RequirementNotMetException, ResourcesNotAvailableException, DepositNotExistingException, IOException {
        game=Utilities.loadGame("setUpSingle",'s');
        Utilities.fillDeposits(game.getCurrPlayer(),true);

        //i manually set a  pushToken on top of the stack
        game.orderTokenStack();
        game.getTokenStack().push(game.getTokenStack().remove(5));

        game.discardLeader(0);
        assertEquals(1,game.getCurrPlayer().getBoard().getFaithPath().getPosition());
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
        assertEquals(2, game.getBlackCrossFaithPath().getPosition());
        assertEquals(6,game.getTokenStack().size());
    }

    @Test
    public void TestMultiEvolution() throws NonEmptyException, IllegalResourceException, IllegalActionException, FullSpaceException, IOException {
        game=Utilities.loadGame("setUpMulti",'m');
        Utilities.fillDeposits(game.getCurrPlayer(),false);
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
        assertEquals(TurnPhase.STARTTURN,game.getTurnPhase());
    }

    @Test
    public void TestSaveAndLoadGameStateMulti() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFoundException, FullGameException, IOException {
        TestSetUpTurnMultiplayer();
        game.saveGameStateOnJson("setUpMulti");
        Game g2=Utilities.loadGame("setUpMulti",'m');
        assertEquals(game.getMarket(),g2.getMarket());
        //should be the total equal but i have the override of the equals only in market
    }
    @Test
    public void TestSaveAndLoadGameStateSingle() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullGameException, IOException {
        TestSetUpTurnSingleplayer();
        game.saveGameStateOnJson("setUpSingle");
        Game g2=Utilities.loadGame("setUpSingle",'s');
        assertEquals(game.getMarket(),g2.getMarket());
        //should be the total equal but i have the override of the equals only in market

    }

    @Test
    public void TestSingleEndgameLose1() throws  IllegalResourceException, IllegalActionException, FullSpaceException,IOException {
        game=Utilities.loadGame("setUpSingle",'s');
        Utilities.fillDeposits(game.getCurrPlayer(),true);

        //manually adding a token that triggers endgame
        Token t=new TokenDiscard(Color.GREEN,14);
        game.getTokenStack().push(t);

        game.setMarket(new Market());
        game.buyAtMarketInterface('r',0);
        game.passTurn();
        assertEquals(GamePhase.ENDGAME,game.getGamePhase());
        assertThrows(IllegalActionException.class,
                ()->game.discardLeader(0));
    }
    @Test
    public void TestSingleEndgameLose2() throws  IllegalResourceException, IllegalActionException, FullSpaceException,  IOException {
        game=Utilities.loadGame("setUpSingle",'s');
        Utilities.fillDeposits(game.getCurrPlayer(),true);

        //manually adding a token that triggers endgame
        Token t=new TokenPush(26);
        game.getTokenStack().push(t);

        game.setMarket(new Market());
        game.buyAtMarketInterface('r',0);
        game.passTurn();
        assertEquals(GamePhase.ENDGAME,game.getGamePhase());
        assertThrows(IllegalActionException.class,
                ()->game.discardLeader(0));
    }
    @Test
    public void TestSingleEndgameLose3() throws  IllegalResourceException, IllegalActionException, FullSpaceException, ResourcesNotAvailableException, IOException, IllegalSlotException, TooManyResourcesException {
        game=Utilities.loadGame("setUpSingle",'s');
        Utilities.fillDeposits(game.getCurrPlayer(),true);
        game.initializeCardMatrixForTests();
        //manually ordering the tokenStack for not discarding card in the first 2 passes
        game.orderTokenStack();
        game.getTokenStack().pop();

        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.buyCard(0,1,1);
        game.passTurn();
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.buyCard(1,1,1);
        //manually adding a token that almost triggers endgame
        Token t=new TokenDiscard(Color.GREEN,11);
        game.getTokenStack().push(t);

        game.passTurn();
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.buyCard(2,0,1);
        assertEquals(GamePhase.ENDGAME,game.getGamePhase());
        assertThrows(IllegalActionException.class,
                ()->game.discardLeader(0));
    }
    @Test
    public void TestSingleInstaWin1() throws IOException, FullSpaceException, IllegalResourceException, IllegalActionException, ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException {
        //7 cards
        game=Utilities.loadGame("setUpSingle",'s');
        Utilities.fillDeposits(game.getCurrPlayer(),false);
        game.initializeCardMatrixForTests();
        //pushing at the top of the stacks 7 tokens that are passive on the cardMatrix
        for(int i=0;i<7;i++){
            game.getTokenStack().push(new TokenPush(1));
        }
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.buyCard(0,0,1);
        game.passTurn();
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.buyCard(0,1,2);
        game.passTurn();
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.buyCard(0,2,3);
        game.passTurn();
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.buyCard(1,0,1);
        game.passTurn();
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.buyCard(1,1,2);
        game.passTurn();
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.buyCard(1,2,3);
        game.passTurn();
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.buyCard(2,0,1);
        game.passTurn();
        assertTrue(game.isEndGameTrigger());
        assertEquals(GamePhase.ENDGAME,game.getGamePhase());
    }
    @Test
    public void TestSingleInstaWin2() throws IllegalActionException, ResourcesNotAvailableException, IllegalResourceException, TooManyResourcesException, IOException, UnknownFoundException {
        //24 faith points with productions
        game=Utilities.loadGame("setUpSingle",'s');
        Production p=new Production();
        p.addOutput(ResourceType.RED,24);
        game.getCurrPlayer().getExtraProductions().add(p);
        game.toggleExtraProd(0);
        game.activateProductions();
        assertTrue(game.isEndGameTrigger());
        assertEquals(GamePhase.ENDGAME,game.getGamePhase());
    }
    @Test
    public void TestSingleInstaWin3() throws  IllegalActionException,   IOException {
        //24 faith points with market
        game=Utilities.loadGame("setUpSingle",'s');
        game.setMarket(new Market());
        //push 30 do nothing tokens
        for(int i=0;i<30;i++) {
            Token n = new TokenPush(0);
            game.getTokenStack().push(n);
        }
        //buy each turn the first row that is all white and red
        for(int i=0;i<30;i++){
            game.buyAtMarketInterface('r',0);
            game.passTurn();
        }
        assertTrue(game.isEndGameTrigger());
        assertEquals(GamePhase.ENDGAME,game.getGamePhase());
    }

}



