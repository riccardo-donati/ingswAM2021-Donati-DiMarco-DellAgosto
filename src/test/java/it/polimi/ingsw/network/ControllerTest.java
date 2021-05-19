package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Utilities;
import it.polimi.ingsw.model.exceptions.CardNotAvailableException;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.VirtualClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ControllerTest {
    @Test
    public void TestEndGameTriggered() throws IOException, CardNotAvailableException, NotYourTurnException, IllegalActionException {
        Controller c=new Controller(1,new VirtualClient("Mario",null));
        Game endingGame= Utilities.loadGame("23FaithPointsSingle",'s');
        endingGame.addExternalObserver(c);

        c.setGame(endingGame);
        c.discardLeader(0,"Mario");

    }
}
