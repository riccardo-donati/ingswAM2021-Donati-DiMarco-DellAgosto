package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.messages.ClientMessage;

public interface Update extends ClientMessage {
    void update(ClientModel clientModel);
}
