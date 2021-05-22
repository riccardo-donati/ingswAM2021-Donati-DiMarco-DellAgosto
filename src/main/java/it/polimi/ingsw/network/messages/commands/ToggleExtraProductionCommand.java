package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.UnknownFoundException;
import it.polimi.ingsw.model.exceptions.WaitingReconnectionsException;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class ToggleExtraProductionCommand implements Command {
    int index;

    public ToggleExtraProductionCommand(int index) {
        this.index = index;
    }

    @Override
    public void doAction(Controller c, String nickname) throws IllegalCommandException, NotYourTurnException, IllegalActionException, UnknownFoundException, WaitingReconnectionsException {
        if(check())c.toggleExtraProd(index-1,nickname);
        else throw new IllegalCommandException();
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
