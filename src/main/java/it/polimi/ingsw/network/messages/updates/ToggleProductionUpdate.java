package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;

public class ToggleProductionUpdate implements Update{
    List<Production> activeProd;

    public ToggleProductionUpdate(List<Production> activeProd){
        this.activeProd=activeProd;
    }

    public List<Production> getActiveProd() {
        return activeProd;
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.getCurrentBoard().setActiveProductions(activeProd);
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void accept(ClientVisitor visitor, CLI client) {
        visitor.visit(this,client);
    }
}
