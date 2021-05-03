package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class PingResponse implements ServerMessage {
    @Override
    public String getMessage() {
        return "Pong";
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}
