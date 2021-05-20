package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientVisitor;

public class PingRequest implements ClientMessage {
    @Override
    public String getMessage() {
        return "Ping";
    }

    @Override
    public void accept(ClientVisitor visitor, CLI client) {
        visitor.visit(this, client);
    }
}
