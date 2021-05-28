package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class PickUpStrongboxUpdate implements Update {
    private final Resource resource;

    public PickUpStrongboxUpdate(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }

    @Override
    public String getMessage() {
        return "picked up " + resource + " from the strongbox";
    }

    @Override
    public void update(ClientModel clientModel) {
        Resource res=clientModel.getCurrentBoard().getDeposits().removeResourceFromStrongbox(resource);
        clientModel.getCurrentBoard().getDeposits().putResourceInHand(res);

        clientModel.setTurnPhase(TurnPhase.PICKUPPHASE);
    }
}
