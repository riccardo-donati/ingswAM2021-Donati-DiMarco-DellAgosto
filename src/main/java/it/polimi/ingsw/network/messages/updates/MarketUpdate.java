package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;

public class MarketUpdate implements Update {
    private List<ResourceType> marbles;
    public MarketUpdate (List<ResourceType> marbles){
        this.marbles=marbles;
    }
    @Override
    public void update(ClientModel clientModel) {
        clientModel.getMarket().setMarbles(marbles);
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
