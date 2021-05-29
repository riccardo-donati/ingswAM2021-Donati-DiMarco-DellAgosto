package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class DepositUpdate implements Update {
    private final int idDeposit;
    private final Resource res;

    public DepositUpdate(int idDeposit,Resource res){
        this.idDeposit=idDeposit;
        this.res=res;
    }

    public int getIdDeposit() {
        return idDeposit;
    }

    public Resource getRes() {
        return res;
    }

    @Override
    public String getMessage() {
        return "deposited " + res.label + " in " + idDeposit;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.getBoards().get(clientModel.getCurrentNickname()).getDeposits().deposit(res, idDeposit);
    }
}
