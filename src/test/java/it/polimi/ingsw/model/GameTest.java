package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.PopeFavorState;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullGameException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NoWhiteResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game game;

    @Test
    public void TestJSONDevelopmentCard(){
        game=new Singleplayer();
        assertEquals(game.getDevelopmentCards().size(),48);
        for(DevelopmentCard dc : game.getDevelopmentCards()){
            assertNotNull(dc.getColor());
            assertNotNull(dc.getLevel());
            assertNotNull(dc.getPoints());
            assertNotNull(dc.getProd());
            assertNotNull(dc.getCost());
        }
    }
    @Test
    public void TestJSONLeaderCard(){
        game=new Singleplayer();
        assertEquals(game.getLeaderCards().size(),16);
        for(LeaderCard lc : game.getLeaderCards()){
            assertNotNull(lc.getRequirements());
            assertNotNull(lc.getSpecialAbilities());
            assertNotNull(lc.getPoints());
        }
    }

    @Test
    public void TestCardMatrixDimension(){
        game=new Singleplayer();
        Stack<DevelopmentCard>[][] matrix=game.getCardMatrix();
        for(int r=0;r<3;r++){
            for(int c=0;c<4;c++){
                assertEquals(4,matrix[r][c].size());
            }
        }
    }
    @Test
    public void TestCardMatrixRowsColumns(){
        game=new Singleplayer();
        Stack<DevelopmentCard>[][] matrix=game.getCardMatrix();
        for(int r=0;r<3;r++){
            for(int c=0;c<4;c++){
                for(DevelopmentCard dp : matrix[r][c]){
                    assertEquals(r+1,dp.getLevel());
                    assertEquals(c,dp.getColor().ordinal());
                }
            }
        }
    }
    @Test
    public void TestDiscardColor(){
        game=new Multiplayer();
        for(int i=0;i<12;i++){
            game.discardColor(Color.GREEN);
        }
        //trigger endgame
    }


    @Test
    public void TestNotifyEndGame() throws FullGameException {
        game=new Multiplayer();
        game.addPlayer("Carlo");
        game.addPlayer("Giuseppe");
        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(22);
        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(23);
        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(2);
    }

    @Test
    void testEndGame() throws FullGameException {
        game = new Singleplayer();
        game.addPlayer("Giancarlo Magalli");
        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(27);
        assertEquals(PopeFavorState.ACTIVE, game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(0).getState());
        assertEquals(PopeFavorState.ACTIVE, game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(1).getState());
        assertEquals(PopeFavorState.ACTIVE, game.getPlayers().get(0).getBoard().getFaithPath().getPopeFavorList().get(2).getState());
    }

    //-----------------------------------------
    @Test
    public void TestBuyAtMarket() throws FullGameException {
        game=new Multiplayer();
        game.addPlayer("Beppe");
        game.addPlayer("Carlo");
        game.startGame();

        game.buyAtMarket('r',0);
        game.buyAtMarket('r',1);
        game.buyAtMarket('r',2);
        assertTrue(game.getCurrPlayer().getBoard().getWarehouse().countPendingResources()>0);
        assertTrue(game.getCurrPlayer().getBoard().getFaithPath().getPosition()>0);
        assertTrue(game.getCurrPlayer().getBoard().getWarehouse().getPendingResources().get(ResourceType.WHITE)==0);
    }
    @Test
    public void TestBuyAtMarketWithOneWhiteTo() throws FullGameException {
        game=new Multiplayer();
        game.addPlayer("Floriano");
        game.startGame();

        game.getCurrPlayer().addWhiteTo(ResourceType.BLUE);

        game.buyAtMarket('r',0);
        game.buyAtMarket('r',1);
        game.buyAtMarket('r',2);
        game.buyAtMarket('c',3);
        assertTrue(game.getCurrPlayer().getBoard().getWarehouse().getPendingResources().get(ResourceType.WHITE)==0);
        assertTrue(game.getCurrPlayer().getBoard().getWarehouse().countPendingResources()>0);
    }
    @RepeatedTest(10)
    public void TestBuyAtMarketWithTwoWhiteTo() throws FullGameException {
        game=new Multiplayer();
        game.addPlayer("Floriano");
        game.startGame();

        game.getCurrPlayer().addWhiteTo(ResourceType.BLUE);
        game.getCurrPlayer().addWhiteTo(ResourceType.YELLOW);

        game.buyAtMarket('c',3);
        game.buyAtMarket('r',0);
        game.buyAtMarket('r',1);
        game.buyAtMarket('r',2);


        assertTrue(game.getCurrPlayer().getBoard().getWarehouse().getPendingResources().get(ResourceType.WHITE)>=4);
        assertTrue(game.getCurrPlayer().getBoard().getWarehouse().countPendingResources()>0);
        int nWhite=game.getCurrPlayer().getBoard().getWarehouse().getPendingResources().get(ResourceType.WHITE);
        assertDoesNotThrow(
                ()-> {
                    game.getCurrPlayer().transformWhiteIn(ResourceType.BLUE);
                    game.getCurrPlayer().transformWhiteIn(ResourceType.YELLOW);
                    game.getCurrPlayer().transformWhiteIn(ResourceType.BLUE);
                    game.getCurrPlayer().transformWhiteIn(ResourceType.YELLOW);
                }
        );
        assertThrows(IllegalResourceException.class,
                ()-> game.getCurrPlayer().transformWhiteIn(ResourceType.GREY));
        assertEquals(nWhite-4,game.getCurrPlayer().getBoard().getWarehouse().getPendingResources().get(ResourceType.WHITE));

    }

    @Test
    public void TestObserverDiscardMulti() throws IllegalResourceException, FullGameException {
        game=new Multiplayer();
        game.addPlayer("Giacomo");
        game.addPlayer("Aldo");
        game.startGame();
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getCurrPlayer().getBoard().getWarehouse().discardResource(ResourceType.YELLOW);
        for(Player p : game.getPlayers()){
            if(p.equals(game.getCurrPlayer())){
                assertEquals(0,p.getBoard().getFaithPath().getPosition());
            }
            else assertEquals(1,p.getBoard().getFaithPath().getPosition());
        }
    }
    @Test
    public void TestObserverDiscardSingle() throws IllegalResourceException, FullGameException {
        game=new Singleplayer();
        game.addPlayer("Giacomo");
        game.startGame();
        game.getCurrPlayer().getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getCurrPlayer().getBoard().getWarehouse().discardResource(ResourceType.YELLOW);

        game.pushBlackCross(7);
        assertEquals(0,game.getCurrPlayer().getBoard().getFaithPath().getPosition());
        assertEquals(8,game.getBlackCrossFaithPath().getPosition());
    }
    @Test
    public void TestAddPlayer() throws FullGameException {
        game=new Multiplayer();
        assertThrows(IllegalArgumentException.class,
                ()-> game.addPlayer(""));
        game.addPlayer("Carlo");
        assertThrows(IllegalArgumentException.class,
                ()-> game.addPlayer("Carlo"));
        assertEquals(1,game.getPlayers().size());

    }
    @Test
    public void FullGameTest(){
        game=new Singleplayer();
        assertDoesNotThrow(
                ()-> game.addPlayer("Aldo")
        );
        assertThrows(FullGameException.class,
                ()->game.addPlayer("Giovanni"));
        assertEquals(1,game.getPlayers().size());
    }
    @Test
    public void FullGameTestMulti(){
        game=new Multiplayer();
        assertDoesNotThrow(
                ()-> {
                    game.addPlayer("Aldo");
                    game.addPlayer("Giovanni");
                    game.addPlayer("Giacomo");
                    game.addPlayer("Giuseppe");
                }
        );
        assertThrows(FullGameException.class,
                ()->game.addPlayer("Giovanni"));
        assertEquals(4,game.getPlayers().size());
    }
}