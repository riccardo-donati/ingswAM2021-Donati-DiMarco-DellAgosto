package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.Token;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingDeque;

import static org.junit.jupiter.api.Assertions.*;

public class PublicInterfaceTest {
    Game game;

    @Test
    public void TestSetUpTurnMultiplayer() throws FullGameException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, NonEmptyException, UnknownNotFoundException, FullSpaceException, IllegalPlayersNumberException, GameNotFullException {
        game=Game.createGame(3);
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
    public void TestSetUpTurnSingleplayer() throws FullGameException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, NonEmptyException, GameNotFullException, IllegalPlayersNumberException {
        game=Game.createGame(1);
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
        Utilities.fillDeposits(game.getCurrPlayer(),true,true);
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
        Utilities.fillDeposits(game.getCurrPlayer(),true,true);

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
        Utilities.fillDeposits(game.getCurrPlayer(),true,true);

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
        Utilities.fillDeposits(game.getCurrPlayer(),true,true);

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
        Utilities.fillDeposits(game.getCurrPlayer(),false,true);
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

    @Disabled
    @Test
    public void TestSaveAndLoadGameStateMulti() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullSpaceException, UnknownNotFoundException, FullGameException, IOException, IllegalPlayersNumberException, GameNotFullException {
        TestSetUpTurnMultiplayer();
        game.saveGameStateOnJson("setUpMulti");
        Game g2=Utilities.loadGame("setUpMulti",'m');
        assertEquals(game.getMarket(),g2.getMarket());
        //should be the total equal but i have the override of the equals only in market
    }
    @Disabled
    @Test
    public void TestSaveAndLoadGameStateSingle() throws NonEmptyException, EmptyPlayersException, IllegalResourceException, IllegalLeaderCardsException, IllegalActionException, FullGameException, IOException, GameNotFullException, IllegalPlayersNumberException {
        TestSetUpTurnSingleplayer();
        game.saveGameStateOnJson("setUpSingle");
        Game g2=Utilities.loadGame("setUpSingle",'s');
        assertEquals(game.getMarket(),g2.getMarket());
        //should be the total equal but i have the override of the equals only in market

    }

    @Test
    public void TestSingleEndgameLose1() throws  IllegalResourceException, IllegalActionException, FullSpaceException,IOException {
        game=Utilities.loadGame("setUpSingle",'s');
        Utilities.fillDeposits(game.getCurrPlayer(),true,true);

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
        Utilities.fillDeposits(game.getCurrPlayer(),true,true);

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
        Utilities.fillDeposits(game.getCurrPlayer(),true,true);
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
        Utilities.fillDeposits(game.getCurrPlayer(),false,true);
        game.initializeCardMatrixForTests();
        //pushing at the top of the stacks 6 tokens that are passive on the cardMatrix
        for(int i=0;i<6;i++){
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
    @Test
    public void TestDiscount1() throws IOException, FullSpaceException, IllegalResourceException, IllegalActionException, ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException, DiscountNotFoundException {
        game=Utilities.loadGame("setUpSingle",'s');
        game.initializeCardMatrixForTests();
        //1blue 1violet 1yellow r=0 c=3
        game.getCurrPlayer().addDiscount(ResourceType.YELLOW);
        game.getCurrPlayer().addDiscount(ResourceType.YELLOW);
        game.getCurrPlayer().addDiscount(ResourceType.BLUE);
        Utilities.fillDeposits(game.getCurrPlayer(),false,true);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.toggleDiscount(ResourceType.YELLOW);
        game.toggleDiscount(ResourceType.BLUE);
        game.buyCard(0,3,1);

        assertEquals(game.getCurrPlayer().getBoard().getSlots().get(1).size(), 1);
    }
    @Test
    public void TestDiscount2() throws IOException, IllegalActionException, ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException, DiscountNotFoundException {
        game=Utilities.loadGame("setUpSingle",'s');
        game.initializeCardMatrixForTests();
        //1blue 1violet 1yellow r=0 c=3
        game.getCurrPlayer().addDiscount(ResourceType.YELLOW);
        game.getCurrPlayer().addDiscount(ResourceType.VIOLET);
        game.getCurrPlayer().addDiscount(ResourceType.BLUE);

        game.toggleDiscount(ResourceType.YELLOW);
        game.toggleDiscount(ResourceType.VIOLET);
        game.toggleDiscount(ResourceType.BLUE);

        //trying to buy without picking up anything -> 3 discounts
        game.buyCard(0,3,1);

        assertEquals(game.getCurrPlayer().getBoard().getSlots().get(1).size(), 1);
    }

    @Test
    public void TestMultiFaithWin() throws IOException, IllegalActionException, FullSpaceException, IllegalResourceException, DepositableResourceException, NonEmptyException {
        //1: BBB leaders: WhiteTo YELLOW, ExtraDeposit BLUE
        //2: CCC leaders: ExtraDeposit YELLOW, WhiteTo BLUE
        //3: AAA leaders: ExtraProduction VIOLET, ExtraDeposit GREY
        game=Utilities.loadGame("setUpMulti",'m');
        game.initializeCardMatrixForTests();
        game.setMarket(new Market());
        while(game.getPlayers().get(0).getBoard().getFaithPath().getPosition()!=23 || game.getPlayers().get(1).getBoard().getFaithPath().getPosition()!=23 ||game.getPlayers().get(2).getBoard().getFaithPath().getPosition()!=23 ){
            game.buyAtMarketInterface('r',0);
            game.passTurn();
        }
        //all the players has 23 faith points
        Utilities.fillDeposits(game.getCurrPlayer(),true,false); //yellow | blue blue | grey grey grey
        game.buyAtMarketInterface('r',1);
        //currently he has just 1 blue in deposit 1
        game.discardResource(ResourceType.GREY);
        game.discardResource(ResourceType.GREY);
        game.discardResource(ResourceType.BLUE);
        game.discardResource(ResourceType.BLUE);
        game.passTurn();
        //AAA must finish his turn before the end
        game.buyAtMarketInterface('r',1);
        game.depositResource(2,ResourceType.GREY);
        game.moveResource(1,2);
        game.depositResource(2,ResourceType.BLUE);
        game.moveResource(2,3);
        game.depositResource(3,ResourceType.BLUE);
        game.passTurn();
        assertEquals(GamePhase.ENDGAME,game.getGamePhase());

    }
    @Test
    public void TestMultiInitialRounds() throws IOException, IllegalActionException, FullSpaceException, IllegalResourceException, NonEmptyException, DepositableResourceException, ResourcesNotAvailableException, IllegalSlotException, TooManyResourcesException, DepositNotExistingException {
        game=Utilities.loadGame("setUpMulti",'m');
        game.initializeCardMatrixForTests();
        game.setMarket(new Market());
        game.buyAtMarketInterface('c',3);
        //player 1 yellow and blue in pending
        game.depositResource(1,ResourceType.YELLOW);
        game.depositResource(2,ResourceType.BLUE);
        game.passTurn();
        //player 2  has 1 yellow in deposit 1
        game.buyAtMarketInterface('c',2);
        //player 2  yellow and blue in pending
        game.moveResource(1,2);
        game.depositResource(2,ResourceType.YELLOW);
        game.depositResource(1,ResourceType.BLUE);
        game.passTurn();
        //player 3 has 1 blue in deposit 1
        game.buyAtMarketInterface('r',1);
        //player 3 2 yellow and 2 grey in pending
        game.depositResource(3,ResourceType.YELLOW);
        game.depositResource(3,ResourceType.YELLOW);
        game.depositResource(2,ResourceType.GREY);
        game.depositResource(2,ResourceType.GREY);
        game.passTurn();
        game.buyAtMarketInterface('r',2);
        //player 1 2 violet in pending
        game.depositResource(3,ResourceType.VIOLET);
        game.depositResource(3,ResourceType.VIOLET);
        game.passTurn();
        game.buyAtMarketInterface('c',0);
        //player 2 1violet 1grey in pending
        game.depositResource(3,ResourceType.GREY);
        game.discardResource(ResourceType.VIOLET);
        game.passTurn();
        //player 3 can buy a card
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(3);
        game.buyCard(0,2,1);
        game.passTurn();
    }

    @Test       //works only if setUpMulti is not modified
    public void TestWholeGame() throws IllegalResourceException, IllegalActionException, NonEmptyException, UnknownNotFoundException, FullSpaceException, IOException, ResourcesNotAvailableException, UnknownFoundException, TooManyResourcesException, DepositNotExistingException, CardNotAvailableException, RequirementNotMetException, DepositableResourceException, IllegalSlotException{
        //------ SetUp------------
        game = Utilities.loadGame("setUpMulti", 'm');
        game.initializeCardMatrixForTests();

        //------Main game------
        //p1 gets resources from the market
        game.buyAtMarketInterface('r', 0);    //white-violet-grey-red
        game.depositResource(1, ResourceType.GREY);
        game.depositResource(2, ResourceType.VIOLET);
        assertThrows(IllegalResourceException.class, () -> game.depositResource(2, ResourceType.GREY));
        game.moveResource(1, 2);
        game.depositResource(2, ResourceType.GREY);
        assertEquals(0, game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 gets resources from the market
        game.buyAtMarketInterface('r', 0);    //violet-grey-red-white
        assertThrows(IllegalResourceException.class, () -> game.depositResource(3, ResourceType.YELLOW));
        game.moveResource(1, 3);
        game.depositResource(3, ResourceType.YELLOW);
        game.depositResource(2, ResourceType.GREY);
        game.depositResource(2, ResourceType.GREY);
        assertEquals(0, game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        assertEquals(ResourceType.YELLOW, game.getCurrPlayer().getBoard().getWarehouse().getMaindepot().get(2).getType());
        assertNotEquals(ResourceType.YELLOW, game.getCurrPlayer().getBoard().getWarehouse().getMaindepot().get(0).getType());
        game.passTurn();

        //p3 gets resources from market
        game.buyAtMarketInterface('r', 0);    //grey-red-white-white
        assertThrows(IllegalResourceException.class, () -> game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(1, ResourceType.BLUE));
        assertThrows(IllegalResourceException.class, () -> game.getCurrPlayer().getBoard().getWarehouse().addResourceInDeposit(2, ResourceType.BLUE));
        game.depositResource(3, ResourceType.YELLOW);
        game.depositResource(2, ResourceType.GREY);
        assertThrows(IllegalActionException.class, () -> game.passTurn());
        game.discardResource(ResourceType.VIOLET);
        assertEquals(1, game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        assertEquals(1, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(1, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p1 keeps getting resources to buy a card (needs 1 blue, 1 violet, 1 yellow now from market next from baseProd)
        game.buyAtMarketInterface('r', 2);   //blue-blue-yellow-yellow
        assertThrows(IllegalResourceException.class, () -> game.depositResource(2, ResourceType.YELLOW));
        assertThrows(FullSpaceException.class, () -> game.depositResource(1, ResourceType.VIOLET));
        game.moveResource(1, 3);
        game.depositResource(1, ResourceType.BLUE);
        game.depositResource(3, ResourceType.VIOLET); //Maindepot: Blue, 2Grey, 2Violet
        game.discardResource(ResourceType.YELLOW);
        assertEquals(2, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(2, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        assertEquals(1, game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 uses BaseProd to obtain a blue res, next turn he will acquire a violet res and be able to buy a violet card
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.substituteUnknownInOutputBaseProduction(ResourceType.BLUE);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        game.activateProductions();
        assertEquals(3, game.getCurrPlayer().getBoard().countTotalResources());
        game.passTurn();

        //p3 needs 5 grey or 5 yellow to use a LCard so buys from market
        game.buyAtMarketInterface('r', 0);      //Violet-grey-yellow-white
        assertThrows(IllegalResourceException.class, () -> game.depositResource(3, ResourceType.VIOLET));
        assertThrows(IllegalResourceException.class, () -> game.depositResource(2, ResourceType.VIOLET));
        assertThrows(IllegalResourceException.class, () -> game.depositResource(1, ResourceType.VIOLET));
        game.discardResource(ResourceType.VIOLET);
        assertEquals(2, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(3, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(2, game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        assertThrows(IllegalActionException.class, () -> game.passTurn());
        game.depositResource(2, ResourceType.GREY);
        game.depositResource(3, ResourceType.YELLOW);
        game.passTurn();

        //p1 transform 2 grey in 1 yellow, next turn can buy a card (violet?)
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.substituteUnknownInOutputBaseProduction(ResourceType.YELLOW);
        assertThrows(IllegalActionException.class, () -> game.activateProductions()); //try to activate prod with unknown
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        game.activateProductions();
        game.passTurn();

        //p2 acquires a violet res e next turn ready to buy a violet card
        game.buyAtMarketInterface('c', 1);  //red-violet-yellow
        assertThrows(IllegalResourceException.class, () -> game.depositResource(1, ResourceType.RED));
        assertEquals(4, game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        game.depositResource(3, ResourceType.YELLOW);
        game.depositResource(2, ResourceType.VIOLET);
        game.passTurn();

        //p3 use baseProd to acquire a yellow, next turn try to get something from the market to get to 5 yellow
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.substituteUnknownInOutputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        game.activateProductions();
        game.passTurn();

        //p1  buys his first DCard (Violet)
        assertThrows(IllegalActionException.class, () -> game.buyCard(0, 3, 1));
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        assertThrows(ResourcesNotAvailableException.class, () -> game.buyCard(0, 3, 1));
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(3);
        game.buyCard(0, 3, 1);
        game.passTurn();

        //p2 needs a second violet to acquire a violet card [0][3]
        game.buyAtMarketInterface('r', 2);
        game.depositResource(1, ResourceType.GREY);
        game.depositResource(2, ResourceType.VIOLET);
        game.passTurn();

        //p3 next turn need to produce a yellow res to have 5 and activate LCard
        game.buyAtMarketInterface('r', 0); //yellow-red-grey-blue
        game.depositResource(3, ResourceType.YELLOW);
        game.moveResource(1, 2);
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(1, ResourceType.GREY);
        assertEquals(2, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(4, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(3, game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p1  gets 2 blue now and the 3rd from the baseProd next Violet-Grey -> BLue to buy a green card
        game.buyAtMarketInterface('c', 2);
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(1, ResourceType.GREY);
        game.passTurn();

        //p2 buys a violet card with 2 violet res
        game.pickUpResourceFromWarehouse(2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> game.buyCard(3, 3, 1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> game.buyCard(2, 4, 1));
        assertThrows(ResourcesNotAvailableException.class, () -> game.buyCard(0, 3, 1));
        game.pickUpResourceFromWarehouse(2);
        game.buyCard(0, 3, 1);
        game.passTurn();

        //p3 now p3 has 5 yellow res, next turn he can activate a LCard
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInOutputBaseProduction(ResourceType.YELLOW);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(2);
        game.activateProductions();
        game.passTurn();

        //p1 uses baseProd 1 Violet and 1 Grey -> 1 Blue and buys a card next turn
        game.substituteUnknownInInputBaseProduction(ResourceType.VIOLET);
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.substituteUnknownInOutputBaseProduction(ResourceType.BLUE);
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(3);
        game.toggleBaseProd();
        game.activateProductions();
        assertEquals(3, game.getCurrPlayer().getBoard().countTotalResources());
        game.passTurn();

        //p2 needs Blue Violet and Grey to buy a green Card (2 from the top) Blue is in Strongbox, Grey in maindep
        game.buyAtMarketInterface('r', 2); //Violet-Yellow-Violet
        assertThrows(IllegalActionException.class, () -> game.passTurn());
        game.depositResource(2, ResourceType.VIOLET);
        assertThrows(IllegalResourceException.class, () -> game.depositResource(3, ResourceType.VIOLET));
        game.depositResource(2, ResourceType.VIOLET);
        game.discardResource(ResourceType.YELLOW);
        assertEquals(3, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(4, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(4, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p3 Activates the Lcard that requires 5 Yellow res, next turn buys a yellow card
        game.pickUpResourceFromWarehouse(3);
        assertThrows(IllegalActionException.class, () -> game.playLeader(1));
        game.revertPickUp();
        assertThrows(RequirementNotMetException.class, () -> game.playLeader(0)); //req 5 grey
        game.playLeader(1);
        game.buyAtMarketInterface('r', 0); //red-grey-blue
        game.depositResource(1, ResourceType.GREY);
        game.depositResource(2, ResourceType.BLUE);
        assertEquals(3, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(4, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(5, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p1 buys a green card
        assertThrows(IllegalActionException.class, () -> game.buyCard(0, 0, 2));
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        assertThrows(ResourcesNotAvailableException.class, () -> game.buyCard(0, 0, 2));
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        assertThrows(IllegalSlotException.class, () -> game.buyCard(0, 0, 1));
        game.buyCard(0, 0, 2);
        game.passTurn();

        //p2 ready to buy a Green card
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(2);
        game.buyCard(0, 0, 3);
        game.passTurn();

        //p3 has a lot of resources, buys a green card, later will test if someone can buy from an empty stack
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.buyCard(0, 0, 2);
        game.passTurn();

        //p1 checks the production and get the game a bit spicy, maybe begin to acquire res for a 2nd Violet card
        game.buyAtMarketInterface('r', 2);  //Yellow-violet-violet-blue
        game.depositResource(3, ResourceType.VIOLET);
        game.depositResource(1, ResourceType.YELLOW);
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(3, ResourceType.VIOLET);
        game.passTurn();

        //p2 Dcard are great but need 1 more green to activate LCard, next turn Baseprod into 1 blue
        game.buyAtMarketInterface('r', 2);  //red-violet-blue-yellow
        game.depositResource(2, ResourceType.VIOLET);
        game.depositResource(1, ResourceType.BLUE);
        game.discardResource(ResourceType.YELLOW);
        assertEquals(4, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(5, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(6, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p3 next turn will buy a DCard from (0,1,1/3), get a violet res from BaseProd
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInOutputBaseProduction(ResourceType.VIOLET);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.toggleBaseProd();
        game.activateProductions();
        game.passTurn();

        //p1 uses base prod to buy a level 1 Violet Card (req: 2 violet-2 grey)
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(2);
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInOutputBaseProduction(ResourceType.GREY);
        game.toggleBaseProd();
        game.activateProductions();
        game.passTurn();

        //p2 need 2 blue res to buy the last green card avaiable
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        assertThrows(ResourcesNotAvailableException.class, ()->game.buyCard(0,0,2));
        game.revertPickUp();
        game.substituteUnknownInInputBaseProduction(ResourceType.VIOLET);
        game.substituteUnknownInInputBaseProduction(ResourceType.VIOLET);
        game.substituteUnknownInOutputBaseProduction(ResourceType.BLUE);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.toggleBaseProd();
        assertThrows(ResourcesNotAvailableException.class, ()->game.activateProductions());
        game.revertPickUp();
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        game.toggleBaseProd();
        game.activateProductions();
        game.passTurn();

        //p3 buys a violet card
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.buyCard(0,1,1);
        game.passTurn();

        //p1 need a second grey res and he'll take from the market to buy a level 1 DCard Violet
        game.buyAtMarketInterface('c', 2); //white-red-grey
        game.depositResource(1, ResourceType.GREY);
        assertEquals(5, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(5, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(6, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 buys the last green card avaiable, now he can activate LCard
        assertThrows(RequirementNotMetException.class,()->game.playLeader(1));
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.buyCard(0,0,2);
        assertThrows(RequirementNotMetException.class, ()->game.playLeader(0));
        game.playLeader(1);
        game.passTurn();

        //p3 acquire 4 res from the market and next turn he'll try to use both the DCard, just 4 fun
        game.buyAtMarketInterface('r',2);
        game.depositResource(3,ResourceType.VIOLET);
        game.depositResource(3, ResourceType.VIOLET);
        assertThrows(IllegalActionException.class, ()->game.passTurn());
        game.depositResource(2, ResourceType.BLUE);
        assertThrows(IllegalActionException.class, ()->game.passTurn());
        game.depositResource(1, ResourceType.YELLOW);
        assertThrows(IllegalActionException.class, ()->game.buyCard(1,0,2));
        game.passTurn();

        //p1 ready to buy a 2nd violet card and activate a LCard
        assertThrows(RequirementNotMetException.class, ()->game.playLeader(0));
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        assertThrows(IllegalSlotException.class, ()->game.buyCard(0,3,2));
        game.buyCard(0,3,3);
        assertThrows(RequirementNotMetException.class, ()->game.playLeader(1));
        game.playLeader(0);
        game.passTurn();

        //p2 acquire some resources from the market, also to check whiteTo Blue
        game.pickUpResourceFromWarehouse(3);
        assertThrows(EmptyStackException.class,()->game.buyCard(0,0,2)); //there are no green cards left
        game.revertPickUp();
        game.buyAtMarketInterface('r', 0); //grey-Blue-Grey-WHITE(Blue)
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(2, ResourceType.BLUE);  //this is a white res that has been converted by the LCard
        assertThrows(DepositableResourceException.class, ()->game.discardResource(ResourceType.GREY));
        game.depositResource(1, ResourceType.GREY);
        game.discardResource(ResourceType.GREY);
        assertEquals(6, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(5, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(7, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p3 Use both BaseProd and DCard prod to acquire grey res (1 Violet->1 Grey and 1 Yellow+1Blue -> 1 Grey)
        game.toggleCardProd(1);
        assertThrows(ResourcesNotAvailableException.class,()->game.activateProductions());
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(2);
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInOutputBaseProduction(ResourceType.GREY);
        game.toggleCardProd(1);
        game.toggleBaseProd();
        game.activateProductions();
        game.passTurn();

        //p1 has no resources so try to acquire some due to the fact that has a LCard then whiteto Yellow
        game.buyAtMarketInterface('r', 2); //Blue-Yellow-Violet-WHITE
        game.depositResource(1,ResourceType.BLUE);
        game.depositResource(2, ResourceType.YELLOW);
        game.depositResource(2, ResourceType.YELLOW);
        game.depositResource(3, ResourceType.VIOLET);
        assertEquals(4, game.getCurrPlayer().getBoard().countTotalResources()); //check if worked WhiteTo
        game.passTurn();

        //p2 can use 1 yellow-> red and 1 grey->1 red from DCard production, and 2 Yellow -> blue (need 5 blue to 2nd LCard)
        assertThrows(UnknownFoundException.class, ()->game.toggleBaseProd());
        game.toggleCardProd(1); //grey -> red
        game.toggleCardProd(2); //yellow -> red
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInOutputBaseProduction(ResourceType.BLUE);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.activateProductions();                         //CREATES A RED FIELD IN STRONGBOX HASHMAP
        game.moveResource(2,3); //moves blue to the 3rd shelf
        assertEquals(6, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(7, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(7, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p3 can use 1 violet->1 grey but it's better a buyAtMarket, next turn base prod and DCard Prod to have 5 grey
        game.buyAtMarketInterface('r', 2);  //White-Yellow-Violet-Grey
        game.depositResource(1, ResourceType.YELLOW);
        game.depositResource(3, ResourceType.VIOLET);
        game.depositResource(2, ResourceType.GREY);
        game.passTurn();

        //p1 keep acquiring res from market to buy a lvl 2 card  (3 blue and 3 violet)
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInInputBaseProduction(ResourceType.VIOLET);
        game.substituteUnknownInOutputBaseProduction(ResourceType.BLUE);
        game.toggleCardProd(1);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(3);
        game.activateProductions();
        game.passTurn();

        //p2 i have 2 blue in warehouse and 1 in strongbox, so now i acquire from the market and next turn use prods
        game.buyAtMarketInterface('r', 0); //blue-grey-violet-white   also white to blue so i have to discard one
        game.depositResource(3, ResourceType.BLUE);
        game.depositResource(1,ResourceType.GREY);
        game.depositResource(2, ResourceType.VIOLET);
        assertThrows(IllegalActionException.class, ()->game.passTurn());
        game.discardResource(ResourceType.BLUE);
        assertEquals(7, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(7, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(8, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());  //activates pope favor
        //Faithpath p1
        assertEquals(PopeFavorState.ACTIVE,game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState());
        assertEquals(PopeFavorState.UNACTIVE,game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState());
        assertEquals(PopeFavorState.UNACTIVE,game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(2).getState());
        //Faithpath p2
        assertEquals(PopeFavorState.ACTIVE,game.getCurrPlayer().getBoard().getFaithPath().getPopeFavorList().get(0).getState());
        assertEquals(PopeFavorState.UNACTIVE,game.getCurrPlayer().getBoard().getFaithPath().getPopeFavorList().get(1).getState());
        assertEquals(PopeFavorState.UNACTIVE,game.getCurrPlayer().getBoard().getFaithPath().getPopeFavorList().get(2).getState());
        //Faithpath p3
        assertEquals(PopeFavorState.ACTIVE,game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(0).getState());
        assertEquals(PopeFavorState.UNACTIVE,game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(1).getState());
        assertEquals(PopeFavorState.UNACTIVE,game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(2).getState());
        game.passTurn();

        //p3 Dcard prod to activate the second LCard
        assertThrows(RequirementNotMetException.class, ()->game.playLeader(0));
        game.toggleCardProd(1);
        game.substituteUnknownInInputBaseProduction(ResourceType.VIOLET);
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInOutputBaseProduction(ResourceType.GREY);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(1);
        game.activateProductions();
        assertThrows(IllegalActionException.class, ()->game.buyAtMarketInterface('r',1));
        game.playLeader(0);
        game.passTurn();

        //p1 need 3 violet resources to buy a lvl 2 card, next turn some prods
        game.buyAtMarketInterface('r', 2);  //Violet-White(Yellow)-grey-blue
        assertThrows(IllegalResourceException.class,()->game.depositResource(1, ResourceType.VIOLET));
        game.moveResource(1,2);
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(1, ResourceType.YELLOW);
        game.depositResource(3, ResourceType.VIOLET);
        game.discardResource(ResourceType.GREY);
        assertEquals(7, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(8, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(9, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 get the resource needed to activate the second card leader, from now on only buy DCard
        assertThrows(RequirementNotMetException.class, ()->game.playLeader(0)); //eccezione card not avaiable non esiste, non verrà chiamato mai
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.substituteUnknownInInputBaseProduction(ResourceType.VIOLET);
        game.substituteUnknownInOutputBaseProduction(ResourceType.BLUE);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(2);
        game.activateProductions();
        game.playLeader(0);
        game.passTurn();

        //p3 to buy a card acquire from market, if lv1 or lv2
        game.buyAtMarketInterface('r',0); //grey-violet-yellow -> i choose this to try use the extradepot
        game.moveResource(2,1); //se il dep1 è vuoto nonn scambias risorse(?)
        assertThrows(FullSpaceException.class, ()->game.depositResource(1, ResourceType.GREY));
        game.depositResource(3,ResourceType.VIOLET);
        game.depositResource(2, ResourceType.YELLOW);
        assertThrows(IllegalActionException.class, ()->game.passTurn());
        game.depositResource(4,ResourceType.GREY);  //this is a extraDep
        game.moveResource(3,5); //try to use the second extraDep
        game.moveResource(1,4); //moving all grey to the extraDep
        game.passTurn();

        //p1 basically i lost a turn because i'm not so smart, next turn buy a 3blue-3violet card lvl2
        game.buyAtMarketInterface('r', 0); //White(Yellow)-Violet-violet-yellow
        game.depositResource(3, ResourceType.VIOLET);
        game.depositResource(3, ResourceType.VIOLET);
        game.discardResource(ResourceType.YELLOW);
        game.discardResource(ResourceType.YELLOW);
        assertEquals(7, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(10, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(11, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 card lv 2 needs 3 yellow, 3 blue [1][0], now acquires only one yellow, so idk how to acquire 2 more
        game.buyAtMarketInterface('r', 0);
        game.depositResource(2, ResourceType.YELLOW);
        game.depositResource(1, ResourceType.GREY);
        game.moveResource(2,4);     //trasfer yellow to extra or is forced to discard
        game.depositResource(2, ResourceType.VIOLET);
        game.depositResource(2, ResourceType.VIOLET);
        game.passTurn();

        //p3 acquire a lvl 2 because lvl 1 are complex, idk anyway get from market something
        game.buyAtMarketInterface('r', 0);  //yellow-violet-grey
        game.depositResource(2, ResourceType.YELLOW);
        game.depositResource(1, ResourceType.GREY);
        game.depositResource(3, ResourceType.VIOLET);
        game.passTurn();

        //p1 buys a card using 3 blue 3 violet, also need 5 violet to LCard, but used 3 now so rip
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        assertThrows(ResourcesNotAvailableException.class, ()->game.buyCard(1, 3,1));
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.buyCard(1, 3,1);
        game.passTurn();

        //p2 i basically have 1 lost turn because DCard are useless, so i try to use to get points
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInInputBaseProduction(ResourceType.VIOLET);
        game.substituteUnknownInOutputBaseProduction(ResourceType.YELLOW);
        game.toggleBaseProd();
        game.toggleCardProd(1);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(1);
        game.activateProductions();
        assertEquals(7, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(11, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(11, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p3 buys a lvl1 card to fill up slots
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        game.buyCard(0,1,3);
        game.moveResource(3,5); //frees a space in warehouse
        game.passTurn();

        //p1 acquire res from market to use some prod next turn
        game.buyAtMarketInterface('r', 0);  //Violet-Grey-Violet-white(Yellow)
        game.moveResource(1,2);
        game.depositResource(2, ResourceType.YELLOW);
        game.depositResource(1, ResourceType.GREY);
        game.depositResource(3, ResourceType.VIOLET);
        game.depositResource(3, ResourceType.VIOLET);
        game.passTurn();

        //p2 need 3 yellow-3 blue to buy a card
        game.buyAtMarketInterface('c', 1);
        game.depositResource(1, ResourceType.GREY);
        game.depositResource(4,ResourceType.YELLOW);
        game.depositResource(3, ResourceType.BLUE);
        game.passTurn();

        //p3 buycard next turn [1][1] needed 3 Yellow 3 Grey
        game.toggleCardProd(2); //1Grey+1Violet->2Yelow+1Red
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.substituteUnknownInInputBaseProduction(ResourceType.GREY);
        game.substituteUnknownInOutputBaseProduction(ResourceType.YELLOW);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(1);    //grey
        game.pickUpResourceFromWarehouse(4);    //grey
        game.pickUpResourceFromWarehouse(4);    //grey
        game.pickUpResourceFromWarehouse(5);    //violet
        game.activateProductions();
        game.passTurn();

        //p1 try to get to 5 violet resource using DCard and BaseProd so can activate 2nd LCard
        game.toggleCardProd(1);
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInOutputBaseProduction(ResourceType.VIOLET);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(1);
        game.activateProductions();
        game.playLeader(0);
        assertEquals(8, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(11, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(12, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 has res to buy a card lvl2
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(4);
        game.pickUpResourceFromWarehouse(4);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.buyCard(1,0,3);
        game.passTurn();

        //p3 buys a lvl2 card
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.buyCard(1,1,1);
        game.passTurn();

        //p1 will buy card [1][3] with 5 Violet 2nd lvl2 card
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        assertThrows(IllegalSlotException.class, ()->game.buyCard(1,3,1));
        game.buyCard(1,3,2);
        game.passTurn();

        //p2 acquire something from market from a [1][0] card that req 5 blue
        game.buyAtMarketInterface('r', 2); //White(Blue)-Violet-Blue-Blue
        game.depositResource(3, ResourceType.BLUE);
        game.depositResource(3, ResourceType.BLUE);
        game.depositResource(3, ResourceType.BLUE);
        game.depositResource(2, ResourceType.VIOLET);
        game.passTurn();

        //p3 lvl2 card 3Violet-2Yellow [1][3]
        game.buyAtMarketInterface('r',0);   //Grey-Violet-Yellow-Yellow
        game.depositResource(5,ResourceType.VIOLET);
        assertThrows(IllegalResourceException.class, ()->game.depositResource(5, ResourceType.VIOLET));
        game.depositResource(4,ResourceType.GREY);
        game.depositResource(3, ResourceType.YELLOW);
        game.depositResource(3, ResourceType.YELLOW);
        game.passTurn();

        //p1 want to acquire some grey res to use prods
        game.buyAtMarketInterface('r',1);   //White(Yellow)-White(Yellow)-grey-red
        assertThrows(IllegalResourceException.class,()->game.depositResource(1, ResourceType.RED));
        game.depositResource(2, ResourceType.YELLOW);
        game.depositResource(2, ResourceType.YELLOW);
        game.depositResource(3, ResourceType.GREY);
        assertEquals(9, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(11, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(12, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 buys a lvl2 card
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.buyCard(1, 0, 2);
        game.passTurn();

        //p3 keep acquiring from market because using prod wouldn't be as good as buying
        game.buyAtMarketInterface('r',2);   //Blue-Violet-Blue
        game.depositResource(1, ResourceType.VIOLET);
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(2, ResourceType.BLUE);
        game.passTurn();

        //p1 acquire more res to do a bigger prod next turn
        game.toggleCardProd(3); //1Blue-1Yellow -> 2Grey-1Red
        game.toggleCardProd(1); //1Grey -> 2Violet-1Red
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.activateProductions();
        assertEquals(11, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(11, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(12, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 acquire some res to use Prod next turn
        game.buyAtMarketInterface('r', 0); //Yellow-Yellow-Violet-White(Blue)
        assertThrows(FullSpaceException.class, ()->game.depositResource(2, ResourceType.VIOLET));
        game.moveResource(2,3);
        game.depositResource(3, ResourceType.VIOLET);
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(4, ResourceType.YELLOW);
        game.depositResource(4, ResourceType.YELLOW);
        game.passTurn();

        //p3 activates all the production and makes a looooot of points
        game.toggleCardProd(2); //1Grey-1Violet -> 2Yellow-1Red
        game.toggleCardProd(1); //1Violet -> 2Grey-1Red
        game.toggleCardProd(3); //1Blue -> 1Red
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(5);
        game.pickUpResourceFromWarehouse(4);
        game.activateProductions();
        assertEquals(11, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(11, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(15, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p1 need 3 violet and 3 grey for the card [1][2]
        game.buyAtMarketInterface('c',0);   //Grey-Violet-Blue
        game.depositResource(4, ResourceType.BLUE);
        game.depositResource(1, ResourceType.GREY);
        game.depositResource(3, ResourceType.VIOLET);
        game.passTurn();

        //p2 activate some prods and idk buy something next turn?
        game.toggleCardProd(1); //1Grey -> 1Red
        game.toggleCardProd(2); //2Yellow -> 2Grey-2Red
        game.pickUpResourceFromWarehouse(4);
        game.pickUpResourceFromWarehouse(4);
        game.pickUpResourceFromWarehouse(1);
        game.activateProductions();
        assertEquals(11, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(14, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(15, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p3 I forgot to buy a card 2 turns ago, so i need more res (sorry p3). Need 2 violet [1][3]
        game.buyAtMarketInterface('r', 0);  //Yellow-Grey-Violet
        game.depositResource(3, ResourceType.YELLOW);
        game.depositResource(4, ResourceType.GREY);
        game.depositResource(5, ResourceType.VIOLET);
        game.passTurn();

        //p1 buys a lvl2 card and fills all the lvl2 fields, he has 6 cards close to end the match boiii
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(3);
        assertThrows(IllegalSlotException.class, ()->game.buyCard(1,2,1));
        assertThrows(IllegalSlotException.class, ()->game.buyCard(1,2,2));
        game.buyCard(1,2,3);
        game.passTurn();

        //p2 I did something wrong so he'll be producing some res
        game.buyAtMarketInterface('c', 0);  //Yellow-Yellow-Blue
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(4,ResourceType.YELLOW);
        game.depositResource(4, ResourceType.YELLOW);
        game.passTurn();

        //p3 next turn buys a [1][3]
        game.moveResource(2,1);
        game.buyAtMarketInterface('r', 0);  //Blue-Violet-Violet
        game.depositResource(2, ResourceType.VIOLET);
        game.depositResource(2, ResourceType.VIOLET);
        game.discardResource(ResourceType.BLUE);
        assertEquals(12, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(15, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(15, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p1 acquire some res to prod next turn
        game.buyAtMarketInterface('r', 2);  //Blue-Grey-White(Yellow)-White(Yellow)
        game.moveResource(2,3);
        game.depositResource(3, ResourceType.YELLOW);
        game.depositResource(3, ResourceType.YELLOW);
        game.depositResource(4, ResourceType.BLUE);
        game.depositResource(1, ResourceType.GREY);
        game.passTurn();

        //p2 prod some res to buy [2[[2]4 Grey-4 Violet
        game.toggleCardProd(2); //2Yellow -> 2Grey-2Red
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInOutputBaseProduction(ResourceType.VIOLET);
        game.toggleBaseProd();
        game.pickUpResourceFromWarehouse(4);
        game.pickUpResourceFromWarehouse(4);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(2);
        game.activateProductions();
        assertEquals(12, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(17, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(15, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        //Faithpath p1
        assertEquals(PopeFavorState.ACTIVE,game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState());
        assertEquals(PopeFavorState.ACTIVE,game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState());    //PROBLEMA COL FATTO CHE IL POPEFAVORSTATE 2 AVEVA INITIAL POSITION SBAGLIATO E NONOSTANTE ORA SIA GIUSTO LA CARTA VIENE SCARTATA
        assertEquals(PopeFavorState.UNACTIVE,game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(2).getState());
        //Faithpath p2
        assertEquals(PopeFavorState.ACTIVE,game.getCurrPlayer().getBoard().getFaithPath().getPopeFavorList().get(0).getState());
        assertEquals(PopeFavorState.ACTIVE,game.getCurrPlayer().getBoard().getFaithPath().getPopeFavorList().get(1).getState());
        assertEquals(PopeFavorState.UNACTIVE,game.getCurrPlayer().getBoard().getFaithPath().getPopeFavorList().get(2).getState());
        //Faithpath p3
        assertEquals(PopeFavorState.ACTIVE,game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(0).getState());
        assertEquals(PopeFavorState.ACTIVE,game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(1).getState());
        assertEquals(PopeFavorState.UNACTIVE,game.getPlayers().get(2).getBoard().getFaithPath().getPopeFavorList().get(2).getState());
        game.passTurn();

        //p3 buys his 2 lvl2 card
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(5);
        game.pickUpResourceFromWarehouse(5);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.buyCard(1,3,3);
        game.passTurn();

        //p1 prods 1Grey -> 2Violet-1Red \ 1Blue ->2Yellow-1Red \ 2Grey -> 2Yellow-2Red
        game.pickUpResourceFromWarehouse(4);
        game.pickUpResourceFromWarehouse(1);
        game.toggleCardProd(1);
        game.toggleCardProd(3);
        game.activateProductions();
        assertEquals(14, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(17, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(15, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 buys a card lvl3
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        assertThrows(IllegalSlotException.class, ()->game.buyCard(2,2,1));
        game.buyCard(2,2,2);
        game.passTurn();

        //p3        1Violet -> 2Grey-1Red \ 1Grey-1Violet -> 2Yellow-1Red \ 1Yellow-1Violet -> 3Blue
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInInputBaseProduction(ResourceType.BLUE);
        game.substituteUnknownInOutputBaseProduction(ResourceType.GREY);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(3);
        game.toggleCardProd(1);
        game.toggleBaseProd();
        game.activateProductions();
        assertEquals(14, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(17, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(16, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p1 buy the last card, req: 4Blue-4Yellow
        game.buyAtMarketInterface('r', 2);  //blue-Yellow-Yellow-Blue
        game.depositResource(2, ResourceType.BLUE);
        game.depositResource(4, ResourceType.BLUE);
        game.discardResource(ResourceType.YELLOW);
        game.discardResource(ResourceType.YELLOW);
        assertEquals(14, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(19, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(18, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p2 acquire some res because p1 is going to end game soon
        game.buyAtMarketInterface('r', 1);  //White(Blue)-Yellow-Grey-Red
        game.depositResource(4, ResourceType.YELLOW);
        game.depositResource(1, ResourceType.BLUE);
        game.depositResource(2, ResourceType.GREY);
        assertEquals(14, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(20, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(18, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p3 buys a card with 7Grey res
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        game.pickUpResourceFromStrongbox(ResourceType.GREY);
        assertThrows(ResourcesNotAvailableException.class, ()->game.buyCard(2,2,1));
        game.pickUpResourceFromWarehouse(4);
        assertThrows(IllegalSlotException.class, ()->game.buyCard(2,2,2));
        game.buyCard(2,2,3);
        game.passTurn();

        //p1 need 1 more blue res, [2][0]
        game.substituteUnknownInInputBaseProduction(ResourceType.VIOLET);
        game.substituteUnknownInInputBaseProduction(ResourceType.VIOLET);
        game.substituteUnknownInOutputBaseProduction(ResourceType.BLUE);
        game.toggleBaseProd();
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.pickUpResourceFromStrongbox(ResourceType.VIOLET);
        game.activateProductions();
        game.passTurn();

        //p2 keep making faith points trough DCards
        game.toggleCardProd(2); //1Blue -> 1Grey-3Violet
        game.toggleCardProd(3); //1Yellow ->2Blue-1Red
        game.toggleCardProd(1); //1Grey -> 1Red
        game.pickUpResourceFromWarehouse(1);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(4);
        game.activateProductions();
        assertEquals(14, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(22, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(18, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //p3  no res for productions, acquire from market, there'll be another turn
        game.buyAtMarketInterface('r', 0);  //violet-violet-yellow
        game.depositResource(2, ResourceType.VIOLET);
        game.depositResource(2, ResourceType.VIOLET);
        game.depositResource(1, ResourceType.YELLOW);
        game.passTurn();

        //p1 4Blue-4Yellow form the last card in [2][0]
        game.pickUpResourceFromStrongbox(ResourceType.BLUE);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromStrongbox(ResourceType.YELLOW);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(3);
        game.pickUpResourceFromWarehouse(2);
        game.pickUpResourceFromWarehouse(4);
        game.pickUpResourceFromWarehouse(4);
        game.buyCard(2,0,3);
        assertTrue(game.isEndGameTrigger());
        game.passTurn();

        //p2 acquiring 4 res, he'll get to a total of 10 which are 2 points, better than produce something
        game.buyAtMarketInterface('r',0);   //Violet-Violet-Yellow-Yelllow
        game.depositResource(2, ResourceType.VIOLET);
        game.depositResource(2, ResourceType.VIOLET);
        game.depositResource(4, ResourceType.YELLOW);
        game.depositResource(4, ResourceType.YELLOW);
        game.passTurn();

        //p3 produce res to get Faithpoints (1Violet -> 2Grey-1Red \ 1Grey-1Violet -> 2Yellow-1Red)
        game.toggleCardProd(1);
        game.pickUpResourceFromWarehouse(2);
        game.activateProductions();
        assertEquals(14, game.getPlayers().get(0).getBoard().getFaithPath().getPosition());
        assertEquals(22, game.getPlayers().get(1).getBoard().getFaithPath().getPosition());
        assertEquals(19, game.getPlayers().get(2).getBoard().getFaithPath().getPosition());
        game.passTurn();

        //FINALLY ENDGAME
        //Player 1 = BBB points [44 DCards + 11 FaithPath + 8 LCard + 0 Deposit] = 63 BRUH VOLEVO FAR VINCERE P2
        //Player 2 = CCC points [31 DCards + 21 FaithPath + 8 LCard + 2 Deposit] = 62
        //Player 3 = AAA points [32 DCards + 17 FaithPath + 6 LCard + 1 Deposit] = 56
    }
    @Test
    public void TestSingleDiscard() throws IOException, IllegalActionException, FullSpaceException, IllegalResourceException, DepositableResourceException {
        game=Utilities.loadGame("setUpSingle",'s');
        game.setMarket(new Market());
        int pos=game.getBlackCrossFaithPath().getPosition();
        game.buyAtMarketInterface('r',2); //YELLOW YELLOW VIOLET VIOLET
        game.depositResource(3,ResourceType.YELLOW);
        game.depositResource(3,ResourceType.YELLOW);
        game.depositResource(2,ResourceType.VIOLET);
        game.depositResource(2,ResourceType.VIOLET);
        game.passTurn();
        game.buyAtMarketInterface('r',1);//GREY GREY BLUE BLUE
        game.depositResource(1,ResourceType.BLUE);
        game.discardResource(ResourceType.BLUE);
        assertEquals(pos+1,game.getBlackCrossFaithPath().getPosition());
    }
    @Test
    public void TestPickUpFromExtra() throws IllegalResourceException, IOException, IllegalActionException, FullSpaceException, ResourcesNotAvailableException, DepositNotExistingException, NonEmptyException, UnknownNotFoundException, UnknownFoundException, TooManyResourcesException {
        game=Utilities.loadGame("setUpsingle",'s');
        game.setMarket(new Market());

        game.getCurrPlayer().getBoard().getWarehouse().addExtraDepot(ResourceType.YELLOW);
        game.buyAtMarketInterface('r',2);//YELLOW YELLOW VIOLET VIOLET
        game.depositResource(4,ResourceType.YELLOW);
        game.depositResource(1,ResourceType.YELLOW);
        game.depositResource(3,ResourceType.VIOLET);
        game.depositResource(3,ResourceType.VIOLET);
        game.passTurn();

        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInInputBaseProduction(ResourceType.YELLOW);
        game.substituteUnknownInOutputBaseProduction(ResourceType.VIOLET);
        game.toggleBaseProd();

        game.pickUpResourceFromWarehouse(4);
        game.pickUpResourceFromWarehouse(1);

        game.activateProductions();
        assertEquals(ResourceType.YELLOW,game.getCurrPlayer().getBoard().getWarehouse().getExtradepots().get(0).getType());



    }
    @Test
    public void TestMoveBetweenExtraDeposit() throws IOException, IllegalActionException, IllegalResourceException, FullSpaceException, NonEmptyException {
        game=Utilities.loadGame("setUpsingle",'s');
        game.setMarket(new Market());

        game.getCurrPlayer().getBoard().getWarehouse().addExtraDepot(ResourceType.YELLOW);
        game.getCurrPlayer().getBoard().getWarehouse().addExtraDepot(ResourceType.YELLOW);

        game.buyAtMarketInterface('r',2);//YELLOW YELLOW VIOLET VIOLET

        game.depositResource(5,ResourceType.YELLOW);
        game.depositResource(5,ResourceType.YELLOW);

        game.moveResource(5,4);
        assertEquals(ResourceType.YELLOW,game.getCurrPlayer().getBoard().getWarehouse().getExtradepots().get(0).getSpace()[0]);
        assertEquals(ResourceType.YELLOW,game.getCurrPlayer().getBoard().getWarehouse().getExtradepots().get(1).getSpace()[0]);

        game.moveResource(5,4);
        assertEquals(ResourceType.YELLOW,game.getCurrPlayer().getBoard().getWarehouse().getExtradepots().get(0).getSpace()[0]);
        assertEquals(ResourceType.EMPTY,game.getCurrPlayer().getBoard().getWarehouse().getExtradepots().get(1).getSpace()[0]);

        game.getCurrPlayer().getBoard().getWarehouse().addExtraDepot(ResourceType.VIOLET);
        assertThrows(IllegalResourceException.class,
                ()->game.depositResource(5,ResourceType.VIOLET));


    }
}



