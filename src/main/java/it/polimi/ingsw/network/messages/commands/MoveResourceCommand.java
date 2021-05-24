package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class MoveResourceCommand implements Command{
    int source;
    int destination;

    public MoveResourceCommand(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    public int getDestination() {
        return destination;
    }

    public int getSource() {
        return source;
    }

    @Override
    public void doAction(Controller c, String nickname) throws IllegalCommandException, IllegalResourceException, NotYourTurnException, IllegalActionException, NonEmptyException, FullSpaceException, WaitingReconnectionsException {
        if(check()) c.moveResource(source,destination,nickname);
        else throw new IllegalCommandException();
    }

    @Override
    public boolean check() {
        return source >= 0 && destination >= 0;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
