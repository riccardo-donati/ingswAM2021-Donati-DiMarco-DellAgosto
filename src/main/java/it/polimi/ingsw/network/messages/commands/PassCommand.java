package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class PassCommand implements Command {
    @Override
    public void doAction(Controller c, String nickname) throws IllegalCommandException, IllegalActionException, NotYourTurnException {
        if(check()) c.passTurn(nickname);
        else throw new IllegalCommandException();
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
