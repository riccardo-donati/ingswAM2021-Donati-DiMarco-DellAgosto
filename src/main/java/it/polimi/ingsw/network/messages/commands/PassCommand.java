package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class PassCommand implements Command {
    @Override
    public boolean doAction(Controller c, String nickname) {
        Game g=c.getGame();
        if(check() && g.getCurrentNickname().equals(nickname)){
            try {
                g.passTurn();
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException e) {
                return false;
            }
            return true;
        }else return false;
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
