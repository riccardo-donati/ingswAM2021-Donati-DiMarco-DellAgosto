package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;
import it.polimi.ingsw.network.messages.updates.Update;

public class ReconnectMessage implements Update {
    private final String reconnectedNickname;

    public ReconnectMessage(String nickname){
        this.reconnectedNickname = nickname;
    }
    @Override
    public String getMessage() {
        return reconnectedNickname + " reconnected!";
    }

    public String getReconnectedNickname() {
        return reconnectedNickname;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.removeDisconnected(reconnectedNickname);
    }
}
