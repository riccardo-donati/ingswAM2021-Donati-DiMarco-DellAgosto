package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.*;

public interface ClientVisitor {
    void visit(DisconnectionMessage message, Client client);
    void visit(GenericMessage message, Client client);
    void visit(LobbyInfoMessage message, Client client);
    void visit(PingRequest pingRequest, Client client);
    void visit(PlayerNumberRequest playerNumberRequest, Client client);
    void visit(RegisterRequest registerRequest, Client client);
}
