package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NoWhiteResourceException;
import it.polimi.ingsw.model.exceptions.WaitingReconnectionsException;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class TransformWhiteCommand implements Command{
    ResourceType resourceType;

    public TransformWhiteCommand(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public void doAction(Controller c, String nickname) throws NoWhiteResourceException, NotYourTurnException, IllegalResourceException, IllegalActionException, IllegalCommandException, WaitingReconnectionsException {
        if(check())c.transformWhiteIn(resourceType,nickname);
        else throw new IllegalCommandException();
    }

    @Override
    public boolean check() {
        return resourceType != null;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
