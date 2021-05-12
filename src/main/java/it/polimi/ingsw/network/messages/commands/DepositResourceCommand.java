package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.ClientHandler;
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
    public boolean doAction(Controller c, String nickname) {
        Game game = c.getGame();
        if (check() && game.getCurrentNickname().equals(nickname)) {
            try {
                game.depositResource(id, resourceType);
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException | FullSpaceException | IllegalResourceException e) {
                return false;
            }
            return true;
        } else return false;
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
