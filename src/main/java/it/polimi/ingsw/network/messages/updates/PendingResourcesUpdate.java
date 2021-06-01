package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;

public class PendingResourcesUpdate implements Update {
    private final List<ResourceType> pending;

    public PendingResourcesUpdate(List<ResourceType> pending) {
        this.pending = pending;
    }

    public List<ResourceType> getPending() {
        return pending;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void update(ClientModel clientModel) {
        List<Resource> clientPending = clientModel.getCurrentBoard().getPendingResources();
        clientPending.clear();
        for(ResourceType res : pending){
            clientPending.add(Utilities.resourceTypeToResource(res));
        }
        if(pending.size() > 0) clientModel.setTurnPhase(TurnPhase.DEPOSITPHASE);
        else
            clientModel.setTurnPhase(TurnPhase.ENDTURN);
    }
}
