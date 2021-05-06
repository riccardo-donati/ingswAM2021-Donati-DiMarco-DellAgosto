package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.commands.ChooseLeadersCommand;

public interface ServerVisitor {
    void visit(RegisterResponse message, ClientHandler clientHandler);
    void visit(PlayerNumberResponse message, ClientHandler clientHandler);
    void visit(PingResponse message, ClientHandler clientHandler);

    void visit(ChooseLeadersCommand command, ClientHandler clientHandler);
}
