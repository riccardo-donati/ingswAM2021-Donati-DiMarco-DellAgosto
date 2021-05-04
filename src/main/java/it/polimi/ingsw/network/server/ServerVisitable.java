package it.polimi.ingsw.network.server;

public interface ServerVisitable {
    void accept(ServerVisitor visitor, ClientHandler clientHandler);
}
