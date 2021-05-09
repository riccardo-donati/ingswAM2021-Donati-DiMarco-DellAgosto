package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class WarehousePickUpCommand implements Command{
    int index;

    public WarehousePickUpCommand(int index) {
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
                game.pickUpResourceFromWarehouse(index);
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException | ResourcesNotAvailableException | DepositNotExistingException | NonEmptyException e) {
                return false;
            }
            return true;
        } else return false;
    }

    @Override
    public boolean check() {
        return index >= 0;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
