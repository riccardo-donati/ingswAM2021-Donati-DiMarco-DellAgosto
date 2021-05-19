package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientVisitor;

public class EndGameMessage implements ClientMessage {
    @Override
    public String getMessage() {
        return "END GAME TRIGGERED ";
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }
}
