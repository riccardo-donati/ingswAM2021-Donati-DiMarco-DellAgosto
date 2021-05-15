package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.ControllerTOELIMINATE;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.ResourcesNotAvailableException;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class StrongboxPickUpCommand implements Command{
    ResourceType resourceType;

    public StrongboxPickUpCommand(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public void doAction(Controller c, String nickname) throws IllegalCommandException, ResourcesNotAvailableException, NotYourTurnException, IllegalActionException {
        if(check()) c.pickUpResourceFromStrongbox(resourceType,nickname);
        else throw new IllegalCommandException();
    }

    @Override
    public boolean check() {
        if(resourceType!=null) return true;
        else return false;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
