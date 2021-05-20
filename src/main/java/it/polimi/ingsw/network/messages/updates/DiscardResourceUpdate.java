package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientBoard;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.Map;

public class DiscardResourceUpdate implements Update{
    @Override
    public void update(ClientModel clientModel) {
        for (Map.Entry<String, ClientBoard> entry : clientModel.getBoards().entrySet()) {
            ClientBoard board = entry.getValue();
            if(!entry.getKey().equals(clientModel.getCurrentNickname())) {
                board.getFaithPath().addToFaithPath(1);
            }
            if(board.getFaithPath().getLorenzoPosition() != null) board.getFaithPath().addLorenzoPosition(1);
        }
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
