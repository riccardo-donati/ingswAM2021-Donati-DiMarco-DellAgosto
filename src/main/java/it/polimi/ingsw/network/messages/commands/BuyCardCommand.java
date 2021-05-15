package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
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
    public void doAction(Controller c, String nickname) throws NotYourTurnException, IllegalActionException, ResourcesNotAvailableException, TooManyResourcesException, IllegalSlotException, IllegalCommandException {
        if(check())c.buyCard(row,column,slot,nickname);
        else throw new IllegalCommandException();
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
