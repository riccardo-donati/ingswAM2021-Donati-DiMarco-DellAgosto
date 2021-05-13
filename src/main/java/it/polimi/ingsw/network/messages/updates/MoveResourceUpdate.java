package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.ArrayList;
import java.util.List;

public class MoveResourceUpdate implements Update{
    ResourceType[] d1;
    ResourceType[] d2;
    Integer id1;
    Integer id2;

    public MoveResourceUpdate(ResourceType[] d1, ResourceType[] d2, Integer id1, Integer id2) {
        this.d1 = d1;
        this.d2 = d2;
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public void update(ClientModel clientModel) {
        List<Resource> res1=new ArrayList<>();
        for(int i=0;i<d1.length;i++){
            if(d1[i]!=ResourceType.EMPTY){
                res1.add(Resource.valueOf(d1[i].label.toUpperCase()));
            }
        }
        List<Resource> res2=new ArrayList<>();
        for(int i=0;i<d2.length;i++){
            if(d2[i]!=ResourceType.EMPTY){
                res2.add(Resource.valueOf(d2[i].label.toUpperCase()));
            }
        }
        clientModel.getBoards().get(clientModel.getCurrentNickname()).getDeposits().deposit(res1,id1);
        clientModel.getBoards().get(clientModel.getCurrentNickname()).getDeposits().deposit(res2,id2);

    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }
}
