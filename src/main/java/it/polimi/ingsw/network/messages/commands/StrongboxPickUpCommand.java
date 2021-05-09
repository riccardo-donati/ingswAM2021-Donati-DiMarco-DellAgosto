package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.DepositNotExistingException;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.NonEmptyException;
import it.polimi.ingsw.model.exceptions.ResourcesNotAvailableException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class StrongboxPickUpCommand implements Command{
    ResourceType resourceType;

    public StrongboxPickUpCommand(ResourceType resourceType) {
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
                game.pickUpResourceFromStrongbox(resourceType);
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException | ResourcesNotAvailableException e) {
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
