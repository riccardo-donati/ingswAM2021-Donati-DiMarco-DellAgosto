package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.DiscountNotFoundException;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.UnknownNotFoundException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class ToggleDiscountCommand implements Command{
    ResourceType resourceType;

    public ToggleDiscountCommand(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public boolean doAction(Controller c, String nickname) {
        Game game = c.getGame();
        if (game.getCurrentNickname().equals(nickname)) {
            try {
                game.toggleDiscount(resourceType);
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException | DiscountNotFoundException e) {
                return false;
            }
            return true;
        } else return false;
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
