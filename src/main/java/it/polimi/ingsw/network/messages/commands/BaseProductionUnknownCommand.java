package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NoWhiteResourceException;
import it.polimi.ingsw.model.exceptions.UnknownNotFoundException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class BaseProductionUnknownCommand implements Command{
    String target;
    ResourceType resourceType;

    public BaseProductionUnknownCommand(String target, ResourceType resourceType) {
        this.target = target;
        this.resourceType = resourceType;
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
                if (target.equals("input"))
                    game.substituteUnknownInInputBaseProduction(resourceType);
                else game.substituteUnknownInOutputBaseProduction(resourceType);
            } catch (IllegalActionException | IndexOutOfBoundsException | NullPointerException | IllegalResourceException | UnknownNotFoundException e) {
                return false;
            }
            return true;
        } else return false;
    }

    @Override
    public boolean check() {
        return target.equals("input") || target.equals("output");
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
