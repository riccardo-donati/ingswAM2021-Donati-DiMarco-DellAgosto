package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.CLI.ClientPopeFavorState;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientBoard;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ReconnectUpdate implements Update{
    private Map<String,Integer> positions;
    private Map<String, Map<Integer, ClientPopeFavorState>> popeFavors;
    private Integer lorenzoPos;
    private Map<String,Map<Resource,Integer>> strongboxes;
    private Map<String, List<ClientDeposit>> warehouses;
    private List<ResourceType> marbles;
    private Stack<String>[][] cardMatrix;
    private List<String> playerOrder;
    private String currentNickname;
    private Map<String,Map<Integer, Stack<String>>> slots;
    private Map<String,List<String>> allLeadersInBoard;
    private List<String> myLeadersInHand;

    public ReconnectUpdate(Map<String, Integer> positions, Map<String, Map<Integer, ClientPopeFavorState>> popeFavors, Integer lorenzoPos, Map<String, Map<Resource, Integer>> strongboxes, Map<String, List<ClientDeposit>> warehouses, List<ResourceType> marbles, Stack<String>[][] cardMatrix, List<String> playerOrder, String currentNickname, Map<String, Map<Integer, Stack<String>>> slots, Map<String, List<String>> allLeadersInBoard, List<String> myLeadersInHand) {
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
    }

    @Override
    public void update(ClientModel clientModel) {
        //update the players order and create the boards
        clientModel.setCurrentNickname(currentNickname);
        clientModel.setPlayersOrder(playerOrder);
        for(String nickname : playerOrder){
            if(clientModel.getBoards().get(nickname)==null){
                clientModel.putBoard(nickname,new ClientBoard());
            }
        }
        //update the faith paths/pope favors
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            clientModel.getBoards().get(entry.getKey()).getFaithPath().setPosition(entry.getValue());
        }
        for (Map.Entry<String, Map<Integer, ClientPopeFavorState>> entry : popeFavors.entrySet()) {
            clientModel.getBoards().get(entry.getKey()).getFaithPath().setPopeFavor(entry.getValue());
        }
        if(lorenzoPos!=null) clientModel.getCurrentBoard().getFaithPath().setLorenzoPosition(lorenzoPos);

        //update the strongboxes
        for (Map.Entry<String,Map<Resource,Integer>> entry : strongboxes.entrySet()) {
            clientModel.getBoards().get(entry.getKey()).getDeposits().setStrongbox(entry.getValue());
        }
        //update the warehouses
        for (Map.Entry<String, List<ClientDeposit>> entry : warehouses.entrySet()) {
            for(ClientDeposit cd : entry.getValue()) {
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
                for(int i=0;i<entry2.getValue().size();i++)
                    cb.push(entry2.getKey(),clientModel.getDevelopmentCard(entry2.getValue().get(i)));
            }
        }
        //update leaders in board
        for (Map.Entry<String,List<String>> entry : allLeadersInBoard.entrySet()) {
            for(String leaderName : entry.getValue()) {
                LeaderCard ld=clientModel.getLeaderCard(leaderName);
                if(ld!=null) {
                    for(SpecialAbility sa : ld.getSpecialAbilities()){
                        if(sa instanceof Discount) clientModel.getBoards().get(entry.getKey()).addDiscount(sa.getResourceType());
                        if(sa instanceof ExtraProduction) clientModel.getBoards().get(entry.getKey()).addExtraProd(sa.getResourceType());
                    }
                        clientModel.getBoards().get(entry.getKey()).getLeadersInBoard().add(ld);
                }
            }
        }
        //update leaders in hand
        for(String leaderName : myLeadersInHand) {
            LeaderCard ld=clientModel.getLeaderCard(leaderName);
            if(ld!=null) clientModel.getBoards().get(clientModel.getNickname()).getLeadersInHand().add(ld);

        }
/*        private Map<String,List<String>> allLeadersInBoard;
        private List<String> myLeadersInHand;

         */

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
