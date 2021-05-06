package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.messages.ServerMessage;

public interface Command extends ServerMessage {
    boolean doAction(Game g,String nickname);
    boolean check();
}
