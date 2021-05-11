package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.FullSpaceException;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NonEmptyException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class RevertPickUpCommand implements Command{

    @Override
    public boolean doAction(Controller c, String nickname) {
        Game game = c.getGame();
        if (game.getCurrentNickname().equals(nickname)) {
            try {
                game.revertPickUp();
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException | IllegalResourceException | FullSpaceException e) {
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
