package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class ToggleDiscountUpdate implements Update {
    private ResourceType res;

    public ToggleDiscountUpdate(ResourceType res){
        this.res=res;
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.getCurrentBoard().toggleDiscount(res);
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
