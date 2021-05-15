package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.exceptions.CardNotAvailableException;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class DiscardLeaderCommand implements Command{
    int index;

    public DiscardLeaderCommand(int index) {
        this.index = index;
    }

    @Override
    public void doAction(Controller c, String nickname) throws IllegalCommandException, CardNotAvailableException, NotYourTurnException, IllegalActionException {
        if(check()) c.discardLeader(index,nickname);
        else throw new IllegalCommandException();
    }

    @Override
    public boolean check() {
        return index >= 0;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
