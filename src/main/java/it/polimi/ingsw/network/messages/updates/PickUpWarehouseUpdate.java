package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class PickUpWarehouseUpdate implements Update {
    Integer idDeposit;

    public PickUpWarehouseUpdate(Integer idDeposit) {
        this.idDeposit = idDeposit;
    }

    public Integer getIdDeposit() {
        return idDeposit;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }

    @Override
    public String getMessage() {
        return "picked up resource from deposit" + idDeposit;
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.getBoards().get(clientModel.getCurrentNickname()).getDeposits().remove(idDeposit);
    }
}
