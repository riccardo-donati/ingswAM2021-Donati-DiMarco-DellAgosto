package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;

public interface ServerVisitor {
    void visit(RegisterResponse message, ClientHandler clientHandler);
    void visit(PlayerNumberResponse message, ClientHandler clientHandler);
    void visit(PingResponse message, ClientHandler clientHandler);
}
