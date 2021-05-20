package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientVisitor;

public class ErrorMessage implements ClientMessage{
    String message;
    public ErrorMessage(String message){
        this.message=message;
    }
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void accept(ClientVisitor visitor, CLI client) {
        visitor.visit(this,client);
    }
}
