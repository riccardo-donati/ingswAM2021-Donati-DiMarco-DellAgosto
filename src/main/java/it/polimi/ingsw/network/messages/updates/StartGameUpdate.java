package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientBoard;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitable;
import it.polimi.ingsw.network.client.ClientVisitor;
import java.util.*;

public class StartGameUpdate implements Update, ClientVisitable {
    private final List<String> playerOrder;
    private final List<String> cards;
    private final Map<String,Integer> faithPaths;
    private final List<ResourceType> marbles;
    private final Stack<String>[][] cardMatrix;

    public StartGameUpdate(List<String> playerOrder, List<String> cards, Map<String,Integer> faithPaths, List<ResourceType> marbles,Stack<String>[][] cardMatrix){
        this.playerOrder=playerOrder;
        this.cards=cards;
        this.faithPaths=faithPaths;
        this.marbles=marbles;
        this.cardMatrix=cardMatrix;
    }


    public List<String> getCards() {
        return cards;
    }

    public List<String> getPlayerOrder() {
        return playerOrder;
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
        for(int i = 0; i < cards.size(); i++)
            clientModel.putIdNameLeadersMap(i + 1, cards.get(i));

        clientModel.setPlayersOrder(playerOrder);
        clientModel.setCurrentNickname(playerOrder.get(0));

        for(String player : playerOrder)
            clientModel.putBoard(player,new ClientBoard());
        if (playerOrder.size() == 1)
            clientModel.setUpSinglePlayer();
        for (String player : playerOrder)
            clientModel.getBoards().get(player).getFaithPath().setPosition(faithPaths.get(player));

        clientModel.getMarket().setMarbles(marbles);
        //clientModel.getCardMatrix().setCards(cardMatrix);
        clientModel.loadCardMatrixFromNames(cardMatrix);

        clientModel.setTurnPhase(TurnPhase.STARTSETUPTURN);
    }
}
