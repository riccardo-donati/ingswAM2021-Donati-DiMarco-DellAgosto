package it.polimi.ingsw.network.client;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Result;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.DepositUpdate;
import it.polimi.ingsw.network.messages.updates.LorenzoUpdate;
import it.polimi.ingsw.network.messages.updates.PendingResourcesUpdate;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public interface Client {
    public Socket getSocket();
    public ClientModel getClientModel();
    public Gson getGson();
    public Scanner getIn();
    public PrintWriter getOut();

    // Updates:
    void visualizeDisconnectionMessage(DisconnectionMessage message);
    void visualizeGeneralMessage(ClientMessage message);
    void visualizeLobbyInfoMessage(LobbyInfoMessage message);
    void visualizeSlotUpdate();
    void visualizeStartGameUpdate();
    void visualizeNewTurnUpdate(GamePhase previousGamePhase);
    void visualizeBonusResourceMessage(BonusResourceMessage message);
    void visualizePendingResourceUpdate(PendingResourcesUpdate message);
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
}
