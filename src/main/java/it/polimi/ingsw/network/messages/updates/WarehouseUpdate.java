package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.Deposit;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;

public class WarehouseUpdate implements Update{
    List<ClientDeposit> deposits;

    public WarehouseUpdate(List<ClientDeposit> deposits){
        this.deposits=deposits;
    }

    @Override
    public void update(ClientModel clientModel) {
        for(ClientDeposit cd : deposits)
            clientModel.getCurrentBoard().getDeposits().deposit(cd.getResources(),cd.getId());
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
