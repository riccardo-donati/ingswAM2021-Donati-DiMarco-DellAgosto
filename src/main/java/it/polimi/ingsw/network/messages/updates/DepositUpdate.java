package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.Deposit;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientVisitor;
import it.polimi.ingsw.network.messages.ClientMessage;

public class DepositUpdate implements ClientMessage {
    private int idDeposit;
    private Resource res;

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
        return res+" deposited";
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }
}
