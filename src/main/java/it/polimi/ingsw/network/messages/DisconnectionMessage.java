package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientVisitor;

public class DisconnectionMessage implements ClientMessage{
    @Override
    public String getMessage() {
        return "You will be disconnected";
    }

    @Override
    public void accept(ClientVisitor visitor, CLI cl) {
        visitor.visit(this,cl);
    }
}
