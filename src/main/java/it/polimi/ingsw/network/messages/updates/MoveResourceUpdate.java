package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.ArrayList;
import java.util.List;

public class MoveResourceUpdate implements Update{
    private final ResourceType[] d1;
    private final ResourceType[] d2;
    private final Integer id1;
    private final Integer id2;

    public MoveResourceUpdate(ResourceType[] d1, ResourceType[] d2, Integer id1, Integer id2) {
        this.d1 = d1;
        this.d2 = d2;
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public void update(ClientModel clientModel) {
        List<Resource> res1 = new ArrayList<>();
        for (ResourceType resourceType : d1) {
            if (resourceType != ResourceType.EMPTY) {
                res1.add(Resource.valueOf(resourceType.label.toUpperCase()));
            }
        }
        List<Resource> res2 = new ArrayList<>();
        for (ResourceType resourceType : d2) {
            if (resourceType != ResourceType.EMPTY) {
                res2.add(Resource.valueOf(resourceType.label.toUpperCase()));
            }
        }
        clientModel.getBoards().get(clientModel.getCurrentNickname()).getDeposits().deposit(res1, id1);
        clientModel.getBoards().get(clientModel.getCurrentNickname()).getDeposits().deposit(res2, id2);

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
