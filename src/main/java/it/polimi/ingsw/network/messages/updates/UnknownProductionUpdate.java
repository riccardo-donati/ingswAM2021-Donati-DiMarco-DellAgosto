package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.UnknownNotFoundException;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class UnknownProductionUpdate implements Update {
    private final Integer index;            //-1 for baseProd
    private final ResourceType res;
    private final char io;                  //i for input, o for output

    public UnknownProductionUpdate(Integer index, ResourceType res,char io){
        this.index = index;
        this.res = res;
        this.io = io;
    }

    @Override
    public void update(ClientModel clientModel) {
        if (io == 'i') {
            if (index == -1) {
                try {
                    clientModel.getCurrentBoard().getBaseProduction().replaceUnknownInput(res);
                } catch (UnknownNotFoundException | IllegalResourceException e) {
                    e.printStackTrace();
                }
            }
            else if(index >= 0) {
                Production p = clientModel.getCurrentBoard().getExtraProductions().get(0);
                if(p != null) {
                    try {
                        p.replaceUnknownInput(res);
                    } catch (UnknownNotFoundException | IllegalResourceException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (io == 'o') {
            if (index == -1) {
                try {
                    clientModel.getCurrentBoard().getBaseProduction().replaceUnknownOutput(res);
                } catch (UnknownNotFoundException | IllegalResourceException e) {
                    e.printStackTrace();
                }
            }
            else if (index >= 0) {
                Production p = clientModel.getCurrentBoard().getExtraProductions().get(index);
                if (p != null) {
                    try {
                        p.replaceUnknownOutput(res);
                    } catch (UnknownNotFoundException | IllegalResourceException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Integer getIndex() {
        return index;
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
