package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class MoveResourceCommand implements Command{
    int source;
    int destination;

    public MoveResourceCommand(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public boolean doAction(Controller c, String nickname) {
        Game game = c.getGame();
        if (check() && game.getCurrentNickname().equals(nickname)) {
            try {
                game.moveResource(source, destination);
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException | NonEmptyException | IllegalResourceException | FullSpaceException e) {
                return false;
            }
            return true;
        } else return false;
    }

    @Override
    public boolean check() {
        return source >= 0 && destination >= 0;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
