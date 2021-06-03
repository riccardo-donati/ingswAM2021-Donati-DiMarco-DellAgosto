package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.exceptions.UnknownFoundException;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class ResetProductionsUpdate implements Update{

    @Override
    public void update(ClientModel clientModel) {
        clientModel.getMyBoard().getBaseProduction().resetProduction();
        if(clientModel.getMyBoard().getBaseProduction().checkSelected()) {
            try {
                clientModel.getMyBoard().getBaseProduction().toggleSelected();
            } catch (UnknownFoundException e) {
                e.printStackTrace();
            }
        }
        for(Production p : clientModel.getMyBoard().getActiveProductions()){
            if(p.checkSelected()) {
                try {
                    p.toggleSelected();
                } catch (UnknownFoundException e) {
                    e.printStackTrace();
                }
            }
            p.resetProduction();
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
