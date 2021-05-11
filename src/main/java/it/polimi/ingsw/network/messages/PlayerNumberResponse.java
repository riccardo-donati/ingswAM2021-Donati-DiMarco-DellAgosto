package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class PlayerNumberResponse implements ServerMessage {
    private int nPlayers;

    public PlayerNumberResponse(int nPlayers){
        this.nPlayers = nPlayers;
    }

    public int getNPlayers() {
        return nPlayers;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this, clientHandler);
    }
}