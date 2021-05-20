package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientVisitor;

public class BonusResourceMessage implements ClientMessage{
    private int bonusRes;

    public BonusResourceMessage(int order){
        if(order == 1)
            this.bonusRes = 0;
        else if(order>1 && order<4)
            this.bonusRes = 1;
        else if(order == 4)
            this.bonusRes = 2;
    }

    public String getMessage(){
        return "Bonus resources: " + bonusRes;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }
}
