package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposits;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientModel.Shelf;
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
                } else if (sp instanceof ExtraDeposit) {
                    ClientDeposits clientDeposits = clientModel.getCurrentBoard().getDeposits();
                    clientDeposits.addShelf(new Shelf(2, clientDeposits.getShelves().size() + 1));
                } else if (sp instanceof ExtraProduction) {
                    clientModel.getCurrentBoard().addExtraProd(sp.getResourceType());
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
