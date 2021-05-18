package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TokenDiscard;
import it.polimi.ingsw.model.TokenPush;
import it.polimi.ingsw.model.TokenPushShuffle;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.interfaces.Token;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class LorenzoUpdate implements Update{
    private Integer lorenzoPosition;
    private Token lastUsedToken;
    private GamePhase gamePhase;

    public LorenzoUpdate(Integer lorenzoPosition, Token lastUsedToken, GamePhase gamePhase){
        this.lastUsedToken=lastUsedToken;
        this.lorenzoPosition=lorenzoPosition;
        this.gamePhase=gamePhase;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public Token getLastUsedToken() {
        return lastUsedToken;
    }

    @Override
    public void update(ClientModel clientModel) {
        if(lastUsedToken instanceof TokenPush || lastUsedToken instanceof TokenPushShuffle){
            clientModel.getBoards().get(clientModel.getCurrentNickname()).getFaithPath().setLorenzoPosition(lorenzoPosition);
        }
        if(lastUsedToken instanceof TokenDiscard){
            Integer quantity= ((TokenDiscard) lastUsedToken).getQuantity();
            Color color=((TokenDiscard) lastUsedToken).getColor();
            for(int i=0;i<quantity;i++){
                clientModel.getCardMatrix().discard(color.ordinal());
            }
        }
        clientModel.setGamePhase(gamePhase);
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
