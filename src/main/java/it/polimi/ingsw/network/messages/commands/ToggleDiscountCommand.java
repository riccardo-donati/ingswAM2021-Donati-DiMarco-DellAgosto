package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.DiscountNotFoundException;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class ToggleDiscountCommand implements Command{
    ResourceType resourceType;

    public ToggleDiscountCommand(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public void doAction(Controller c, String nickname) throws IllegalCommandException, NotYourTurnException, IllegalActionException, DiscountNotFoundException {
        if(check()) c.toggleDiscount(resourceType,nickname);
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
