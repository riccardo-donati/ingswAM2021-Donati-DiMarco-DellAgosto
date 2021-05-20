package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;

public class PendingResourcesMessage implements ClientMessage{
    List<ResourceType> pending;

    public PendingResourcesMessage(List<ResourceType> pending) {
        this.pending = pending;
    }

    public List<ResourceType> getPending() {
        return pending;
    }

    @Override
    public void accept(ClientVisitor visitor, CLI client) {
        visitor.visit(this, client);
    }

    @Override
    public String getMessage() {
        StringBuilder mex= new StringBuilder();
        mex.append("Deposit this pending resources:\n[");
        for(ResourceType res : pending){
            Resource r=Resource.valueOf(res.label.toUpperCase());
            mex.append(res.label+"("+r.label+"),");
            //mex.append(ResourceType.valueOfLabel(res.toString())+"("+Resource.valueOf()+")");
        }
        if(pending.size()>0) mex.deleteCharAt(mex.toString().length()-1);
        mex.append("]");
        return mex.toString();
    }


}
