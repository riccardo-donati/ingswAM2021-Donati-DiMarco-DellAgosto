package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientVisitor;

public class EndGameMessage implements ClientMessage {
    @Override
    public String getMessage() {
        return "END GAME TRIGGERED ";
    }

    @Override
    public void accept(ClientVisitor visitor, CLI client) {
        visitor.visit(this,client);
    }
}
