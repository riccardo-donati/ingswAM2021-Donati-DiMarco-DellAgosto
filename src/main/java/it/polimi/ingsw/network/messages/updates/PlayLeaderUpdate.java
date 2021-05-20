package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.Discount;
import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.SpecialAbility;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class PlayLeaderUpdate implements Update {
    private final int index;

    public PlayLeaderUpdate(int index){
        this.index = index;
    }

    @Override
    public void update(ClientModel clientModel) {
        LeaderCard leaderCard = clientModel.getCurrentBoard().getLeadersInHand().remove(index);
        if (leaderCard != null) {
            clientModel.getCurrentBoard().getLeadersInBoard().add(leaderCard);
            for (SpecialAbility sp : leaderCard.getSpecialAbilities()) {
                if (sp instanceof Discount) {
                    clientModel.getCurrentBoard().addDiscount(sp.getResourceType());
                }
            }
        }
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit( this, client);
    }
}
