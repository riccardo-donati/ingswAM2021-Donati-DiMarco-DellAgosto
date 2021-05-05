package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientVisitable;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.ArrayList;
import java.util.List;

public class StartGameMessage implements ClientMessage, ClientVisitable {
    private List<String> playerOrder=new ArrayList<>();
    private List<String> cards=new ArrayList<>();

    public StartGameMessage(List<String> playerOrder,List<String> cards){
        this.playerOrder=playerOrder;
        this.cards=cards;
    }

    public List<String> getCards() {
        return cards;
    }

    public List<String> getPlayerOrder() {
        return playerOrder;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }

    @Override
    public String getMessage() {
        return "Game started!";
    }
}
