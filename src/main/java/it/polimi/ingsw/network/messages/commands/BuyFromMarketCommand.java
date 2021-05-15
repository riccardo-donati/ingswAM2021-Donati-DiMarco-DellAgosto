package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.ControllerTOELIMINATE;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

public class BuyFromMarketCommand implements Command{
    char line;
    int position;

    public BuyFromMarketCommand(char line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public void doAction(Controller c, String nickname) throws IllegalActionException, NotYourTurnException, IllegalCommandException {
        if(check()) c.buyAtMarketInterface(line,position-1,nickname);
        else throw new IllegalCommandException();
    }

    @Override
    public boolean check() {
        if(line=='r' && position>0 && position<4) return true;
        else if(line=='c' && position>0 && position<5) return true;
        else return false;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
