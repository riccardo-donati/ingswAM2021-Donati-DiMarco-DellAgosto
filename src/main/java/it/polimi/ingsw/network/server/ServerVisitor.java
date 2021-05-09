package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.commands.ChooseBonusResourceCommand;
import it.polimi.ingsw.network.messages.commands.ChooseLeadersCommand;
import it.polimi.ingsw.network.messages.commands.PassCommand;

public interface ServerVisitor {
    void visit(RegisterResponse message, ClientHandler clientHandler);
    void visit(PlayerNumberResponse message, ClientHandler clientHandler);
    void visit(PingResponse message, ClientHandler clientHandler);

    void visit(ChooseLeadersCommand command, ClientHandler clientHandler);
    void visit(ChooseBonusResourceCommand command, ClientHandler clientHandler);
    void visit(PassCommand command, ClientHandler clientHandler);
}
