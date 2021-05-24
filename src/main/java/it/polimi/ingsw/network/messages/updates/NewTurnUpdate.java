package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class NewTurnUpdate implements Update {
    private final String currNickname;
    private final GamePhase gamePhase;

    public NewTurnUpdate(String currNickname,GamePhase gamePhase) {
        this.currNickname = currNickname;
        this.gamePhase = gamePhase;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.setCurrentNickname(currNickname);
        clientModel.setGamePhase(gamePhase);
        clientModel.getCurrentBoard().resetProduction();

        if(gamePhase == GamePhase.ONGOING) clientModel.setTurnPhase(TurnPhase.STARTTURN);
        else if(gamePhase == GamePhase.SETUP) clientModel.setTurnPhase(TurnPhase.STARTSETUPTURN);
    }
}
