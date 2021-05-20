package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientVisitor;

public class PlayerNumberRequest implements ClientMessage {
    @Override
    public String getMessage() {
        return "Please insert the number of players:";
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }
}

