package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.exceptions.UnknownFoundException;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.ArrayList;
import java.util.List;

public class ResetProductionsUpdate implements Update{
    List<Production> before;

    public ResetProductionsUpdate(List<Production> before){
        this.before=before;
    }
    @Override
    public void update(ClientModel clientModel) {
        if(before.contains(clientModel.getMyBoard().getBaseProduction())) {
            clientModel.getMyBoard().getBaseProduction().resetProduction();
            if (clientModel.getMyBoard().getBaseProduction().checkSelected()) {
                try {
                    clientModel.getMyBoard().getBaseProduction().toggleSelected();
                } catch (UnknownFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        for(Production p : clientModel.getMyBoard().getExtraProductions()){
            if(before.contains(p)) {
                if (p.checkSelected()) {
                    try {
                        p.toggleSelected();
                    } catch (UnknownFoundException e) {
                        e.printStackTrace();
                    }
                }
                p.resetProduction();
            }
        }
        for(Production p : clientModel.getMyBoard().getActiveProductions()){
            if(before.contains(p)) {
                if (p.checkSelected()) {
                    try {
                        p.toggleSelected();
                    } catch (UnknownFoundException e) {
                        e.printStackTrace();
                    }
                }
                p.resetProduction();
            }
        }
        clientModel.getMyBoard().setActiveProductions(new ArrayList<>());
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
