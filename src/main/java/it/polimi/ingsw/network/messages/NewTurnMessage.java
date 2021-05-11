package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientVisitor;
import it.polimi.ingsw.network.messages.ClientMessage;

public class NewTurnMessage implements ClientMessage {
    private String currNickname;

    public NewTurnMessage(String currNickname){
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
}
