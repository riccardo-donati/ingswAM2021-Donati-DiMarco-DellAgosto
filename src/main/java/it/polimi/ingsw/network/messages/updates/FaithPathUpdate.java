package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;
import it.polimi.ingsw.network.messages.ClientMessage;

public class FaithPathUpdate implements Update {
    private Integer position;
    public  FaithPathUpdate(Integer position){
        this.position=position;
    }
    @Override
    public void update(ClientModel clientModel) {
        clientModel.getBoards().get(clientModel.getCurrentNickname()).getFaithPath().setPosition(position);
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }
}
