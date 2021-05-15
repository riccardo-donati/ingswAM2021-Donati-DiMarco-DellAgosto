package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.ControllerTOELIMINATE;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class DepositResourceCommand implements Command{
    private ResourceType resourceType;
    private int id;

    public DepositResourceCommand(ResourceType resourceType, int id) {
        this.resourceType = resourceType;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    @Override
    public void doAction(Controller c, String nickname) throws NotYourTurnException, IllegalResourceException, IllegalActionException, FullSpaceException, IllegalCommandException {
        if(check()) c.depositResource(id,resourceType,nickname);
        else throw new IllegalCommandException();
    }

    @Override
    public boolean check() {
        return id >= 0;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
