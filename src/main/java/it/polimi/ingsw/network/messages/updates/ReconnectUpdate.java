package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.CLI.enums.ClientPopeFavorState;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientBoard;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientModel.Shelf;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ReconnectUpdate implements Update{
    private final Map<String,Integer> positions;
    private final Map<String, Map<Integer, ClientPopeFavorState>> popeFavors;
    private final Integer lorenzoPos;
    private final Map<String,Map<Resource,Integer>> strongboxes;
    private final Map<String, List<ClientDeposit>> warehouses;
    private final List<ResourceType> marbles;
    private final Stack<String>[][] cardMatrix;
    private final List<String> playerOrder;
    private final String currentNickname;
    private final Map<String,Map<Integer, Stack<String>>> slots;
    private final Map<String,List<String>> allLeadersInBoard;
    private final List<String> myLeadersInHand;
    private final GamePhase gamePhase;
    private final TurnPhase turnPhase;
    private final List<String> fourLeaderCards;
    private final List<ResourceType> pendingResources;
    private final List<String> activePlayers;
    private final Map<String,Map<Integer,String>> allPlayedCards;
    private final Map<String,Map<Integer,String>> allDiscardedCards;
    private final Map<Integer,Production> unknownProductions;
    private final Map<Resource,Integer> handResource;
    private final List<ResourceDiscount> discounts;
    private final List<Production> activeProd;


    public ReconnectUpdate(Map<String, Integer> positions, Map<String, Map<Integer, ClientPopeFavorState>> popeFavors, Integer lorenzoPos, Map<String, Map<Resource, Integer>> strongboxes, Map<String, List<ClientDeposit>> warehouses, List<ResourceType> marbles, Stack<String>[][] cardMatrix, List<String> playerOrder, String currentNickname, Map<String, Map<Integer, Stack<String>>> slots, Map<String, List<String>> allLeadersInBoard, List<String> myLeadersInHand,GamePhase gamePhase,List<String> fourLeaderCards,List<ResourceType> pendingResources,List<String> activePlayers,Map<String,Map<Integer,String>> allPlayedCards,Map<String,Map<Integer,String>> allDiscardedCards, Map<Integer,Production> unknownProductions,TurnPhase turnPhase,Map<Resource,Integer> handResource,List<ResourceDiscount> discounts,List<Production> activeProd) {
        this.positions = positions;
        this.popeFavors = popeFavors;
        this.lorenzoPos = lorenzoPos;
        this.strongboxes = strongboxes;
        this.warehouses = warehouses;
        this.marbles = marbles;
        this.cardMatrix = cardMatrix;
        this.playerOrder = playerOrder;
        this.currentNickname = currentNickname;
        this.slots = slots;
        this.allLeadersInBoard = allLeadersInBoard;
        this.myLeadersInHand = myLeadersInHand;
        this.gamePhase=gamePhase;
        this.fourLeaderCards=fourLeaderCards;
        this.pendingResources=pendingResources;
        this.activePlayers=activePlayers;
        this.allPlayedCards=allPlayedCards;
        this.allDiscardedCards=allDiscardedCards;
        this.unknownProductions=unknownProductions;
        this.turnPhase=turnPhase;
        this.handResource=handResource;
        this.discounts=discounts;
        this.activeProd=activeProd;
    }

    @Override
    public void update(ClientModel clientModel) {
        //update the players order and create the boards
        clientModel.setCurrentNickname(currentNickname);
        clientModel.setPlayersOrder(playerOrder);
        for (String nickname : playerOrder) {
            if (clientModel.getBoards().get(nickname)==null) {
                clientModel.putBoard(nickname, new ClientBoard());
            }
        }
        //update the faith paths/pope favors
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            clientModel.getBoards().get(entry.getKey()).getFaithPath().setPosition(entry.getValue());
        }
        for (Map.Entry<String, Map<Integer, ClientPopeFavorState>> entry : popeFavors.entrySet()) {
            clientModel.getBoards().get(entry.getKey()).getFaithPath().setPopeFavor(entry.getValue());
        }
        if (lorenzoPos!=null) clientModel.getCurrentBoard().getFaithPath().setLorenzoPosition(lorenzoPos);

        //update the strongboxes
        for (Map.Entry<String,Map<Resource,Integer>> entry : strongboxes.entrySet()) {
            clientModel.getBoards().get(entry.getKey()).getDeposits().setStrongbox(entry.getValue());
        }
        //update the warehouses
        for (Map.Entry<String, List<ClientDeposit>> entry : warehouses.entrySet()) {
            for(ClientDeposit cd : entry.getValue()) {
                if(cd.getId()>3)
                    clientModel.getBoards().get(entry.getKey()).getDeposits().addShelf(new Shelf(2,cd.getId()));
                clientModel.getBoards().get(entry.getKey()).getDeposits().deposit(cd.getResources(), cd.getId());
            }
        }
        //update the marbles and cardmatrix
        clientModel.getMarket().setMarbles(marbles);
        clientModel.loadCardMatrixFromNames(cardMatrix);

        //update slots
        for (Map.Entry<String,Map<Integer,Stack<String>>> entry : slots.entrySet()) {
            ClientBoard cb=clientModel.getBoards().get(entry.getKey());
            for (Map.Entry<Integer,Stack<String>>entry2 : entry.getValue().entrySet()) {
                for(int i = 0; i < entry2.getValue().size(); i++)
                    cb.push(entry2.getKey(),clientModel.getDevelopmentCard(entry2.getValue().get(i)));
            }
        }
        //update leaders in board
        for (Map.Entry<String,List<String>> entry : allLeadersInBoard.entrySet()) {
            for(String leaderName : entry.getValue()) {
                LeaderCard ld=clientModel.getLeaderCard(leaderName);
                if(ld != null) {
                    for(SpecialAbility sa : ld.getSpecialAbilities()){
                        if(sa instanceof Discount) clientModel.getBoards().get(entry.getKey()).addDiscount(sa.getResourceType());
                        if(sa instanceof ExtraProduction) clientModel.getBoards().get(entry.getKey()).addExtraProd(sa.getResourceType());
                    }
                        clientModel.getBoards().get(entry.getKey()).getLeadersInBoard().add(ld);
                }
            }
        }
        //update leaders in hand
        for (String leaderName : myLeadersInHand) {
            LeaderCard ld=clientModel.getLeaderCard(leaderName);
            if(ld != null) clientModel.getBoards().get(clientModel.getNickname()).getLeadersInHand().add(ld);

        }
        //update gamePhase and turnPhase
        clientModel.setGamePhase(gamePhase);
        clientModel.setTurnPhase(turnPhase);
        //update 4 leaders
        for (String leaderCard : fourLeaderCards)
            clientModel.addSetupPhaseLeaderCard(leaderCard);

        //update disconnected players
        for(String nick : playerOrder) {
            if (!activePlayers.contains(nick))
                clientModel.addDisconnected(nick);
        }

        //update played and discarded cards
        for (Map.Entry<String, ClientBoard> entry : clientModel.getBoards().entrySet()) {
            Map<Integer,String> played=allPlayedCards.get(entry.getKey());
            Map<Integer,String> discarded=allDiscardedCards.get(entry.getKey());
            entry.getValue().setDiscardedCards(discarded);
            entry.getValue().setPlayedCards(played);
        }

        //update productions with unknown
        /*if(clientModel.getNickname().equals(clientModel.getCurrentNickname())){
            clientModel.getCurrentBoard().setUnknownProductions(unknownProductions);
        }*/
        clientModel.getMyBoard().setUnknownProductions(unknownProductions);

        //update pending resources
        List<Resource> clientPending = clientModel.getCurrentBoard().getPendingResources();
        clientPending.clear();
        for(ResourceType res : pendingResources){
            clientPending.add(Utilities.resourceTypeToResource(res));
        }

        //update hand resources
        if(clientModel.getNickname().equals(clientModel.getCurrentNickname())){
            clientModel.getCurrentBoard().getDeposits().setHandResources(handResource);
        }

        //discounts
        if(clientModel.getNickname().equals(clientModel.getCurrentNickname())){
            clientModel.getCurrentBoard().setDiscounts(discounts);
        }

        //active prod
        clientModel.getMyBoard().setActiveProductions(activeProd);
        /*if(clientModel.getNickname().equals(clientModel.getCurrentNickname())){
            clientModel.getCurrentBoard().setActiveProductions(activeProd);
        }*/
    }

    public List<ResourceType> getPendingResources() {
        return pendingResources;
    }

    public List<String> getFourLeaderCards() {
        return fourLeaderCards;
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
