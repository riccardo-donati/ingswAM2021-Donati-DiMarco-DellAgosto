package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class DiscardResourceCommand implements Command{
    ResourceType resourceType;

    public DiscardResourceCommand(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public void doAction(Controller c, String nickname) throws NotYourTurnException, IllegalResourceException, DepositableResourceException, IllegalActionException, IllegalCommandException, WaitingReconnectionsException {
        if(check()) c.discardResource(resourceType,nickname);
        else throw new IllegalCommandException();
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
