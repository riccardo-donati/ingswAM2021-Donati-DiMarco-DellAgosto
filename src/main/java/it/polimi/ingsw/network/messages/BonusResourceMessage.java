package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientVisitor;

public class BonusResourceMessage implements ClientMessage{
    int bonusRes;
    public BonusResourceMessage(int order){
        if(order == 1)
            this.bonusRes=0;
        else if(order>1 && order<4)
            this.bonusRes=1;
        else if(order == 4)
            this.bonusRes=2;
    }
    public String getMessage(){
        return "Bonus resources: "+bonusRes;
    }

    @Override
    public void accept(ClientVisitor visitor, CLI client) {
        visitor.visit(this,client);
    }
}
