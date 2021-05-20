package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.ArrayList;
import java.util.List;

public class LeadersInHandUpdate implements Update {
    private final List<String> leadersInHand;

    public LeadersInHandUpdate(List<String> leadersInHand){
        this.leadersInHand = leadersInHand;
    }

    @Override
    public void update(ClientModel clientModel) {
        List<LeaderCard> list = new ArrayList<>();
        for(String leader : leadersInHand) {
            LeaderCard leaderCard = clientModel.getLeaderCard(leader);
            if(leaderCard != null) list.add(leaderCard);
        }
        clientModel.getBoards().get(clientModel.getCurrentNickname()).setLeadersInHand(list);
        clientModel.setTurnPhase(TurnPhase.ENDSETUPTURN);
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }
}
