package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class ActivateProductionsCommand implements Command {

    @Override
    public boolean doAction(Controller c, String nickname) {
        Game game = c.getGame();
        if (game.getCurrentNickname().equals(nickname)) {
            try {
                game.activateProductions();
            } catch (IllegalActionException | UnknownFoundException | IndexOutOfBoundsException | NullPointerException | IllegalResourceException | ResourcesNotAvailableException | TooManyResourcesException e) {
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
