package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;

public class PendingResourcesMessage implements ClientMessage{
    List<ResourceType> pending;

    public PendingResourcesMessage(List<ResourceType> pending) {
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
        return pending.toString();
    }
}
