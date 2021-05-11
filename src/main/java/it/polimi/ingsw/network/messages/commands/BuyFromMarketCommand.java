package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalSlotException;
import it.polimi.ingsw.model.exceptions.UnknownFoundException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class BuyFromMarketCommand implements Command{
    char line;
    int position;

    public BuyFromMarketCommand(char line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public boolean doAction(Controller c, String nickname) {
        Game game = c.getGame();
        if (check() && game.getCurrentNickname().equals(nickname)) {
            try {
                game.buyAtMarketInterface(line, position - 1);
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException e) {
                return false;
            }
            return true;
        } else return false;
    }

    @Override
    public boolean check() {
        return (line == 'r' || line == 'c') && position >= 1;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
