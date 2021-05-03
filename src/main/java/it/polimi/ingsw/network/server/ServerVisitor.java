package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;

public interface ServerVisitor {
    void visit(RegisterResponse res, ClientHandler ch);
    void visit(PlayerNumberResponse pnr,ClientHandler ch);
    void visit(PingResponse pr,ClientHandler ch);
}
