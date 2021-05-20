package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.ArrayList;
import java.util.List;

public class LeadersInHandUpdate implements Update {
    private List<String> leadersInHand;

    public LeadersInHandUpdate(List<String> leadersInHand){
        this.leadersInHand=leadersInHand;
    }
    @Override
    public void update(ClientModel clientModel) {
        List<LeaderCard> list=new ArrayList<>();
        for(String l : leadersInHand) {
            LeaderCard ld=clientModel.getLeaderCard(l);
            if(ld!=null) list.add(ld);
        }
        clientModel.getBoards().get(clientModel.getCurrentNickname()).setLeadersInHand(list);
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void accept(ClientVisitor visitor, CLI client) {
        visitor.visit(this,client);
    }
}
