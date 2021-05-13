package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class LorenzoUpdate implements Update{
    private Integer lorenzoPosition;
    //private matrice di carte/carte scartate

    public LorenzoUpdate(Integer lorenzoPosition/*,Matrice mat*/){
        this.lorenzoPosition=lorenzoPosition;
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.getBoards().get(clientModel.getCurrentNickname()).getFaithPath().setLorenzoPosition(lorenzoPosition);
        //clientModel.getBoards().get(clientModel.getCurrentNickname()).getMATRICE(. . .) to do
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
