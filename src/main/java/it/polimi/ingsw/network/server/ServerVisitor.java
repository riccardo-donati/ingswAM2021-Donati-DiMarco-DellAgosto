package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.commands.*;

public interface ServerVisitor {
    void visit(RegisterResponse message, ClientHandler clientHandler);
    void visit(PlayerNumberResponse message, ClientHandler clientHandler);
    void visit(PingResponse message, ClientHandler clientHandler);

    void visit(ChooseLeadersCommand command, ClientHandler clientHandler);
    void visit(ChooseBonusResourceCommand command, ClientHandler clientHandler);
    void visit(PassCommand command, ClientHandler clientHandler);

    void visit(ToggleExtraProductionCommand command, ClientHandler clientHandler);
    void visit(ToggleProductionCommand command, ClientHandler clientHandler);
    void visit(ActivateProductionsCommand command, ClientHandler clientHandler);

    void visit(StrongboxPickUpCommand command, ClientHandler clientHandler);
    void visit(WarehousePickUpCommand command, ClientHandler clientHandler);
    void visit(DepositResourceCommand command, ClientHandler clientHandler);
    void visit(MoveResourceCommand command, ClientHandler clientHandler);
    void visit(DiscardResourceCommand command, ClientHandler clientHandler);
    void visit(RevertPickUpCommand command, ClientHandler clientHandler);

    void visit(BuyCardCommand command, ClientHandler clientHandler);

    void visit(TransformWhiteCommand command, ClientHandler clientHandler);
    void visit(BuyFromMarketCommand command, ClientHandler clientHandler);
    void visit(ToggleDiscountCommand command, ClientHandler clientHandler);

    void visit(ProductionUnknownCommand command, ClientHandler clientHandler);
    void visit(ExtraProductionUnknownCommand command, ClientHandler clientHandler);
}
