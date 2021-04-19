package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.PopeFavorState;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MultiplayerTest {
    Game game;

    @Test
    public void TestListPlayers() throws FullGameException, IllegalPlayersNumberException {
        assertThrows(IllegalPlayersNumberException.class,
                ()-> game=new Multiplayer(1));
        assertThrows(IllegalPlayersNumberException.class,
                ()-> game=new Multiplayer(0));
        assertThrows(IllegalPlayersNumberException.class,
                ()-> game=new Multiplayer(5));
        game = new Multiplayer(3);
        game.addPlayer("Pluto");
        game.addPlayer("Pippo");
        game.addPlayer("Topotizio");
        assertThrows(FullGameException.class,
                ()->game.addPlayer("sda"));
        assertEquals("Pluto", game.getPlayers().get(0).getNickname());
        assertEquals("Pippo", game.getPlayers().get(1).getNickname());
        assertEquals("Topotizio", game.getPlayers().get(2).getNickname());

    }

    @Test
    public void TestNotifyPopeFavor() throws FullGameException, IllegalPlayersNumberException {
        game=new Multiplayer(3);
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

/*    @Test
    public void TestWinner() throws FullGameException {
        game=new Multiplayer();
        game.addPlayer("Tizio forte");
        game.addPlayer("Tizio mediamente forte");
        game.addPlayer("Tizio scarso");
        game.addPlayer("Tizio scarso forte");
        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(5);
        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(8);

        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(3);
        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(2);
        game.getPlayers().get(2).getBoard().getFaithPath().addToPosition(12);
        game.getPlayers().get(3).getBoard().getFaithPath().addToPosition(4);

        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(5);
        game.getPlayers().get(3).getBoard().getFaithPath().addToPosition(2);

        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(10);   //points: 12 + 2 + 3 = 17
        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(11);   // 20 + 2 + 3 + 4 = 29 winner
        game.getPlayers().get(2).getBoard().getFaithPath().addToPosition(39);   // 6
        game.getPlayers().get(3).getBoard().getFaithPath().addToPosition(19);   // 2

        assertTrue(game.isEndGameTrigger());
    }*/

    @Test
    public void TestWinner2() throws FullGameException, IllegalResourceException, FullSpaceException, IllegalSlotException, IllegalPlayersNumberException {
        game =new Multiplayer(2);
        game.addPlayer("Jack");     //11 Dcards + 3 storage + 3 leader + 5 pf + 12 faith = 34
        game.addPlayer("Io");       //26 points

        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(ResourceType.GREY, 1));
        game.getPlayers().get(0).getBoard().pushDCard(1, new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 3));
        game.getPlayers().get(0).getBoard().pushDCard(1, new DevelopmentCard(requirements, 2, Color.BLUE, new Production(), 2));
        game.getPlayers().get(0).getBoard().pushDCard(2, new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 2));
        game.getPlayers().get(0).getBoard().pushDCard(2, new DevelopmentCard(requirements, 2, Color.VIOLET, new Production(), 2));
        game.getPlayers().get(0).getBoard().pushDCard(2, new DevelopmentCard(requirements, 3, Color.YELLOW, new Production(), 2));

        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.VIOLET);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.BLUE);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.BLUE);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.BLUE);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInDeposit(1,ResourceType.YELLOW);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.VIOLET);
        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.VIOLET);
        Map<ResourceType, Integer> res1 = new HashMap<>();
        res1.put(ResourceType.GREY,3);
        res1.put(ResourceType.YELLOW,4);
        res1.put(ResourceType.BLUE,5);
        game.getPlayers().get(0).getBoard().depositInStrongbox(res1);

        game.getPlayers().get(0).getLeadersInGame().add(new LeaderCard(2));
        game.getPlayers().get(0).getLeadersInGame().add(new LeaderCard(1));

        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(6);
        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(8);
        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(12);
        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(20);

        assertTrue(game.isEndGameTrigger());
    }

    @Test
    public void TestSamePointsWinner() throws FullGameException, IllegalResourceException, FullSpaceException, IllegalSlotException, IllegalPlayersNumberException {
        game = new Multiplayer(4);
        game.addPlayer("Qiqi"); //2+1+25    28 points 1 resources -> winner
        game.addPlayer("Eula"); //3+1+24    28 points 1 resources -> winner
        game.addPlayer("Keqing"); //3+1+24  27 points 2 resources
        game.addPlayer("Jean"); //3+1+24    28 points 0 resources

        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(ResourceType.GREY, 1));
        game.getPlayers().get(0).getBoard().pushDCard(2, new DevelopmentCard(requirements, 1, Color.GREEN, new Production(), 3));
        game.getPlayers().get(1).getBoard().pushDCard(2, new DevelopmentCard(requirements, 1, Color.YELLOW, new Production(), 8));
        game.getPlayers().get(2).getBoard().pushDCard(2, new DevelopmentCard(requirements, 1, Color.YELLOW, new Production(), 7));
        game.getPlayers().get(3).getBoard().pushDCard(2, new DevelopmentCard(requirements, 1, Color.YELLOW, new Production(), 4));

        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInPending(ResourceType.BLUE);
        game.getPlayers().get(1).getBoard().getWarehouse().addResourceInPending(ResourceType.YELLOW);
        game.getPlayers().get(2).getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);
        game.getPlayers().get(2).getBoard().getWarehouse().addResourceInPending(ResourceType.GREY);

        game.getPlayers().get(0).getBoard().getWarehouse().addResourceInDeposit(3,ResourceType.BLUE);
        game.getPlayers().get(1).getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.YELLOW);
        game.getPlayers().get(2).getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.GREY);
        game.getPlayers().get(2).getBoard().getWarehouse().addResourceInDeposit(2,ResourceType.GREY);

        game.getPlayers().get(0).getBoard().getFaithPath().addToPosition(21); //+21+4
        game.getPlayers().get(1).getBoard().getFaithPath().addToPosition(21); //+16+4
        game.getPlayers().get(2).getBoard().getFaithPath().addToPosition(21); //+16+4
        game.getPlayers().get(3).getBoard().getFaithPath().addToPosition(24); //+24 ->triggers endgame

        Result result = game.endGame();
        assertEquals(2, result.getWinner().size());
        assertTrue(result.getWinner().contains("Qiqi"));
        assertTrue(result.getWinner().contains("Eula"));
    }
}