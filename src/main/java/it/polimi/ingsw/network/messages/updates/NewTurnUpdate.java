package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class NewTurnUpdate implements Update {
    private String currNickname;
    private GamePhase gamePhase;

    public NewTurnUpdate(String currNickname,GamePhase gamePhase){
        this.currNickname=currNickname;
        this.gamePhase=gamePhase;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    @Override
    public void accept(ClientVisitor visitor, CLI client) {
        visitor.visit(this,client);
    }

    @Override
    public String getMessage() {
        return "New turn -> "+currNickname;
    }

    @Override
    public void update(ClientModel clientModel) {
        clientModel.setCurrentNickname(currNickname);
        clientModel.setGamePhase(gamePhase);
        clientModel.getCurrentBoard().resetProduction();
    }
}
