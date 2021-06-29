package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Utilities;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.network.server.VirtualClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControllerTest {
    Controller c;

    @Test
    public void TestInitializeSingle(){
        VirtualClient vc=new VirtualClient("a",null);
        c=new Controller(1,vc);

        assertTrue(c.isFull());

        c.start();
    }
    @Test
    public void TestInitializeMulti(){
        VirtualClient vc=new VirtualClient("a",null);
        c=new Controller(4,vc);
        VirtualClient vc2=new VirtualClient("b",null);
        VirtualClient vc3=new VirtualClient("c",null);
        VirtualClient vc4=new VirtualClient("d",null);

        c.addPlayerInLobby(vc2);
        c.addPlayerInLobby(vc3);
        c.addPlayerInLobby(vc4);
        c.removePlayer(vc4);
        c.addPlayerInLobby(vc4);
        assertTrue(c.isFull());

        c.start();

    }
    @Test
    public void TestEndGameTriggered() throws IOException, CardNotAvailableException, NotYourTurnException, IllegalActionException, WaitingReconnectionsException {
        Server s=new Server(1234);
        Controller c=new Controller(1,new VirtualClient("Mario",null),s);
        s.getLobbies().add(c);
        Game endingGame= Utilities.loadGame("23FaithPointsSingle",'s');
        endingGame.addExternalObserver(c);

        c.setGame(endingGame);
        c.discardLeader(0,"Mario");
    }

    @Test
    public void TestCommands() throws NonEmptyException, IllegalLeaderCardsException, IllegalActionException, WaitingReconnectionsException, NotYourTurnException, IllegalCommandException, IllegalResourceException, FullSpaceException, UnknownNotFoundException, RequirementNotMetException, CardNotAvailableException {
        TestInitializeSingle();
        String nick=c.getCurrentNickname();
        List<String> lead=c.getPlayerLeaderCardList(c.getCurrentNickname());
        lead.remove(0);
        lead.remove(0);
        c.chooseLeader(lead,c.getCurrentNickname());
        assertThrows(UnknownNotFoundException.class,
                ()-> c.chooseResourceToDeposit(1, ResourceType.VIOLET,c.getCurrentNickname()));
        c.passTurn(nick);
        c.buyAtMarketInterface('r',1,nick);
    }

    @Test
    public void TestDisconnectReconnect(){
        TestInitializeMulti();
        c.disconnectAllPlayers();
        c.reconnectPlayer("a");
        c.reconnectPlayer("b");
        assertEquals(2,c.getActivePlayers().size());

        c.setDisconnected(true);
        c.setDisconnected(false);
    }
    @Test
    public void TestLobbyInfo(){
        TestInitializeMulti();
        c.setGameObservers();
        assertTrue(c.getNames().contains("a"));

        Map<String,List<ClientDeposit>> warehouses=c.getAllWarehouses();
        assertNotNull(warehouses);

        Map<Resource,Integer> strongbox=c.getCurrentStrongbox();
        assertNotNull(strongbox);

        Map<String,Map<Resource,Integer>> stronboxes=c.getAllStrongboxes();
        assertNotNull(stronboxes);

        List<ClientDeposit> warehouse=c.getCurrentWarehouse();
        assertNotNull(warehouse);


    }
}
