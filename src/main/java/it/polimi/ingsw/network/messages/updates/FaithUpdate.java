package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.FaithPath;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class FaithUpdate implements Update {
    private Integer newPosition;

    public FaithUpdate(Integer newPosition){
        this.newPosition=newPosition;
    }
    @Override
    public void update(ClientModel clientModel) {
        clientModel.getCurrentBoard().getFaithPath().setPosition(newPosition);
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
