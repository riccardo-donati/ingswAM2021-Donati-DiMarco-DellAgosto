package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientVisitor;

public class ReconnectMessage implements ClientMessage {
    private String reconnectedNickname;

    public ReconnectMessage(String nickname){
        this.reconnectedNickname=nickname;
    }
    @Override
    public String getMessage() {
        return reconnectedNickname+" reconnected!";
    }

    public String getReconnectedNickname() {
        return reconnectedNickname;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }
}
