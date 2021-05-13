package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;
import it.polimi.ingsw.network.messages.ClientMessage;

public class NewTurnUpdate implements Update {
    private String currNickname;

    public NewTurnUpdate(String currNickname){
        this.currNickname=currNickname;
    }


    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }

    @Override
    public String getMessage() {
        return "New turn -> "+currNickname;
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.setCurrentNickname(currNickname);
    }
}
