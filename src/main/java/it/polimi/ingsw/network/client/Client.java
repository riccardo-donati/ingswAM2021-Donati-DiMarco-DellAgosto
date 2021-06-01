package it.polimi.ingsw.network.client;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Result;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.*;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public interface Client {
     Socket getSocket();
     ClientModel getClientModel();
     Gson getGson();
     Scanner getIn();
     PrintWriter getOut();

    // Updates:
    void visualizeDisconnectionMessage(DisconnectionMessage message);
    void visualizeGeneralMessage(ClientMessage message);
    void visualizeLobbyInfoMessage(LobbyInfoMessage message);
    void visualizeSlotUpdate();
    void visualizeStartGameUpdate();
    void visualizeNewTurnUpdate(GamePhase previousGamePhase);
    void visualizeBonusResourceMessage(BonusResourceMessage message);
    void visualizePendingResourceUpdate();
    void visualizeDepositUpdate(DepositUpdate message);
    void visualizeLorenzoUpdate(LorenzoUpdate message, GamePhase previousGamePhase);
    void visualizePopeFavorUpdate();
    void visualizeMoveResourceUpdate();
    void visualizeErrorMessage(ErrorMessage message);
    void visualizeLeadersInHandUpdate();
    void visualizeDepositsUpdate();
    void visualizeToggleProductionUpdate();
    void visualizeUnknownProductionUpdate();
    void visualizePickUpWarehouseUpdate();
    void visualizePickUpStrongboxUpdate();
    void visualizePlayLeaderUpdate();
    void visualizeToggleDiscountUpdate();
    void visualizeEndGameMessage();
    void visualizeEndGameResultUpdate(Result gameResult);
    void visualizeNumberOfPlayer(PlayerNumberRequest message);
    void visualizeWait();
    void visualizeRegisterRequest();
    void visualizeReconnection(ReconnectUpdate message);
    void visualizeDiscardLeaderUpdate();
    void visualizeFaithUpdate();
    void visualizeMarketUpdate();
    void visualizeDisconnectedMessage();
    void visualizeReconnectMessage(ReconnectMessage message);

}
