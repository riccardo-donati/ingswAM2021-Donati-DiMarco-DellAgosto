package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class DiscardLeaderUpdate implements Update {
    private int index;

    public DiscardLeaderUpdate(int index){
        this.index=index;
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.getCurrentBoard().getFaithPath().addToFaithPath(1);
        LeaderCard ld=clientModel.getCurrentBoard().getLeadersInHand().get(index);
        if(ld!=null) clientModel.getCurrentBoard().getLeadersInHand().remove(index);
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
