package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;

public class ToggleProductionUpdate implements Update{
    private final List<Production> activeProd;

    public ToggleProductionUpdate(List<Production> activeProd){
        this.activeProd = activeProd;
    }

    public List<Production> getActiveProd() {
        return activeProd;
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.getCurrentBoard().setActiveProductions(activeProd);
        if (clientModel.getCurrentBoard().getActiveProductions().size()>0)
            clientModel.setTurnPhase(TurnPhase.PICKUPPHASE);
        else clientModel.setTurnPhase(TurnPhase.STARTTURN);
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }
}
