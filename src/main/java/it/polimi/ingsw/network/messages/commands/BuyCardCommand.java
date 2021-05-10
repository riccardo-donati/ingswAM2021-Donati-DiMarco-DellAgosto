package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class BuyCardCommand implements Command{
    int row;
    int column;
    int slot;

    public BuyCardCommand(int row, int column, int slot) {
        this.row = row;
        this.column = column;
        this.slot = slot;
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
                game.buyCard(row, column, slot - 1);
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException | ResourcesNotAvailableException | IllegalSlotException | TooManyResourcesException e) {
                return false;
            }
            return true;
        } else return false;
    }

    @Override
    public boolean check() {
        return row >= 0 && column >= 0 && slot >= 0;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
