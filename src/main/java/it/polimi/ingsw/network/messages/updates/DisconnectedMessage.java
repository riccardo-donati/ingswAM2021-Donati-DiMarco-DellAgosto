package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;
import it.polimi.ingsw.network.messages.updates.Update;

public class DisconnectedMessage implements Update {
    private final String nickname;

    public DisconnectedMessage(String nickname){
        this.nickname=nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String getMessage() {
        return nickname+" disconnected!";
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.addDisconnected(nickname);
    }
}
