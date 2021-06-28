package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Utilities;
import it.polimi.ingsw.model.exceptions.CardNotAvailableException;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.WaitingReconnectionsException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.network.server.VirtualClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class ControllerTest {
    Controller c;
    @BeforeEach
    @Test
    public void TestInitialize(){
        VirtualClient vc=new VirtualClient("a",null);
        c=new Controller(4,vc);
        c.initializeGame(4);
        VirtualClient vc2=new VirtualClient("b",null);
        VirtualClient vc3=new VirtualClient("b",null);
        VirtualClient vc4=new VirtualClient("b",null);

        c.addPlayerInLobby(vc2);
        c.addPlayerInLobby(vc3);
        c.addPlayerInLobby(vc4);
        assertTrue(c.isFull());

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
    public void TestStart(){
        c.addPlayers(c.getListNickname());
        c.start();
    }
}
