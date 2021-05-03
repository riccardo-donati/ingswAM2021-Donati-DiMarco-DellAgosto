package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientVisitor;

public class DisconnectionMessage implements ClientMessage{
    @Override
    public String getMessage() {
        return "You will be disconnected in 3 seconds";
    }

    @Override
    public void accept(ClientVisitor visitor, Client cl) {
        visitor.visit(this,cl);
    }
}
