package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class HandResourcesUpdate implements Update{
    private Resource resource;

    public HandResourcesUpdate(ResourceType res){
        this.resource= Utilities.resourceTypeToResource(res);
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.getCurrentBoard().getDeposits().putResourceInHand(resource);
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
