package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalSlotException;
import it.polimi.ingsw.model.exceptions.UnknownFoundException;
import it.polimi.ingsw.model.exceptions.WaitingReconnectionsException;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class ToggleProductionCommand implements Command{
    int slot;

    public ToggleProductionCommand(int slot) {
        this.slot = slot;
    }

    @Override
    public void doAction(Controller c, String nickname) throws IllegalCommandException, UnknownFoundException, IllegalActionException, NotYourTurnException, IllegalSlotException, WaitingReconnectionsException {
        if(check()){
            if(slot==0)c.toggleBaseProd(nickname);
            else c.toggleCardProd(slot,nickname);
        }else throw new IllegalCommandException();
    }

    @Override
    public boolean check() {
        return slot >= 0 && slot<4;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
