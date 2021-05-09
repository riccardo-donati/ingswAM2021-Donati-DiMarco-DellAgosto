package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.UnknownFoundException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class ToggleExtraProductionCommand implements Command {
    int index;

    public ToggleExtraProductionCommand(int index) {
        this.index = index;
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
                game.toggleExtraProd(index - 1);
            } catch (IllegalActionException | UnknownFoundException | IndexOutOfBoundsException | NullPointerException e) {
                return false;
            }
            return true;
        } else return false;
    }

    @Override
    public boolean check() {
        return index >= 1;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
