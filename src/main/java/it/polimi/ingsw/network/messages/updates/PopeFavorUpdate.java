package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.CLI.ClientPopeFavorState;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.Map;

public class PopeFavorUpdate implements Update {
    private final Map<String,Integer> positions;
    private final Map<String, Map<Integer, ClientPopeFavorState>> popeFavors;
    private final Integer lorenzoPos;

    public PopeFavorUpdate(Map<String,Integer> positions, Map<String, Map<Integer, ClientPopeFavorState>> popeFavors, Integer lorenzoPos){
        this.positions = positions;
        this.popeFavors = popeFavors;
        this.lorenzoPos = lorenzoPos;
    }
    @Override
    public void update(ClientModel clientModel) {
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            clientModel.getBoards().get(entry.getKey()).getFaithPath().setPosition(entry.getValue());
        }
        for (Map.Entry<String, Map<Integer, ClientPopeFavorState>> entry : popeFavors.entrySet()) {
            clientModel.getBoards().get(entry.getKey()).getFaithPath().setPopeFavor(entry.getValue());
        }
        if (lorenzoPos!=null) clientModel.getCurrentBoard().getFaithPath().setLorenzoPosition(lorenzoPos);
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
