package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;
import it.polimi.ingsw.network.messages.updates.Update;

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
        StringBuilder mex = new StringBuilder();
        if(pending.size() > 0) {
            mex.append("Deposit this pending resources:\n[");
            for (ResourceType resourceType : pending) {
                Resource resource = Resource.valueOf(resourceType.label.toUpperCase());
                mex.append(resourceType.label).append("(").append(resource.label).append("),");
                //mex.append(ResourceType.valueOfLabel(res.toString())+"("+Resource.valueOf()+")");
            }
            if (pending.size() > 0) mex.deleteCharAt(mex.toString().length() - 1);
            mex.append("]");
        }
        return mex.toString();
    }

    @Override
    public void update(ClientModel clientModel) {
        if(pending.size() > 0) clientModel.setTurnPhase(TurnPhase.DEPOSITPHASE);
        else
            clientModel.setTurnPhase(TurnPhase.ENDTURN);
    }
}
