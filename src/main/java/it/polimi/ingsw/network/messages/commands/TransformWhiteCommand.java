package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NoWhiteResourceException;
import it.polimi.ingsw.model.exceptions.ResourcesNotAvailableException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class TransformWhiteCommand implements Command{
    ResourceType resourceType;

    public TransformWhiteCommand(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public boolean doAction(Controller c, String nickname) {
        Game game = c.getGame();
        if (game.getCurrentNickname().equals(nickname)) {
            try {
                game.transformWhiteIn(resourceType);
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException | IllegalResourceException | NoWhiteResourceException e) {
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
