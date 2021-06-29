package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class DiscardLeaderUpdate implements Update {
    private final int index;

    public DiscardLeaderUpdate(int index){
        this.index=index;
    }

    /**
     * updates the given client model following the discard of a leader card:
     * adds 1 to current player's faith path and 
     * @param clientModel update target
     */
    @Override
    public void update(ClientModel clientModel) {
        clientModel.getCurrentBoard().getFaithPath().addToFaithPath(1);
        if(clientModel.getNickname().equals(clientModel.getCurrentNickname())) {
            LeaderCard ld = clientModel.getCurrentBoard().getLeadersInHand().get(index);
            if (ld != null) {
                clientModel.getCurrentBoard().getLeadersInHand().remove(index);
                clientModel.getCurrentBoard().putDiscardedCard(index,ld.getName());
            }
        }else{
            //i cant see the discarded card of the other player
            clientModel.getCurrentBoard().putDiscardedCard(index,"");
        }

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
