package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.*;

public interface ClientVisitor {
    void visit(DisconnectionMessage message, Client client);
    void visit(GenericMessage message, Client client);
    void visit(LobbyInfoMessage message, Client client);
    void visit(PingRequest message, Client client);
    void visit(PlayerNumberRequest message, Client client);
    void visit(RegisterRequest message, Client client);
    void visit(StartGameUpdate message, Client client);
    void visit(NewTurnUpdate message, Client client);
    void visit(BonusResourceMessage message, Client client);
    void visit(ReconnectMessage message, Client client);
    void visit(DepositUpdate message, Client client);
    void visit(LorenzoUpdate message, Client client);
    void visit(PopeFavorUpdate message, Client client);
    void visit(MoveResourceUpdate message, Client client);
    void visit(MarketUpdate message, Client client);
    void visit(ErrorMessage message, Client client);
    void visit(LeadersInHandUpdate message, Client client);
    void visit(SlotUpdate message, Client client);
    void visit(DepositsUpdate message, Client client);
    void visit(ToggleProductionUpdate message, Client client);
    void visit(UnknownProductionUpdate message, Client client);
    void visit(PickUpWarehouseUpdate message, Client client);
    void visit(PickUpStrongboxUpdate message, Client client);
    void visit(PlayLeaderUpdate message, Client client);
    void visit(ToggleDiscountUpdate message, Client client);
    void visit(DiscardResourceUpdate message, Client client);
    void visit(DiscardLeaderUpdate message, Client client);
    void visit(FaithUpdate message, Client client);
    void visit(EndGameMessage message, Client client);
    void visit(EndGameResultUpdate message, Client client);
    void visit(ReconnectUpdate message, Client client);
    void visit(PendingResourcesUpdate message, Client client);
}
