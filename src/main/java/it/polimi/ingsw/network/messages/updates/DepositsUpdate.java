package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;
import java.util.Map;

public class DepositsUpdate implements Update{
    private final List<ClientDeposit> deposits;
    private final Map<Resource,Integer> strongbox;
    private final TurnPhase turnPhase;

    public DepositsUpdate(List<ClientDeposit> deposits, Map<Resource,Integer> strongbox,TurnPhase turnPhase) {
        this.deposits=deposits;
        this.strongbox=strongbox;
        this.turnPhase=turnPhase;
    }

    @Override
    public void update(ClientModel clientModel) {
        for(ClientDeposit cd : deposits) {
            clientModel.getCurrentBoard().getDeposits().deposit(cd.getResources(), cd.getId());
        }
        clientModel.getCurrentBoard().getDeposits().setStrongbox(strongbox);
        clientModel.getCurrentBoard().getDeposits().clearResourcesInHand();

        clientModel.setTurnPhase(turnPhase);
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
