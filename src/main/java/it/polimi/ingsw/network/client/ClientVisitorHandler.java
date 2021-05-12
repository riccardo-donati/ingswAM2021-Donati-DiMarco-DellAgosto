package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientBoard;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.NewTurnMessage;
import it.polimi.ingsw.network.messages.updates.DepositUpdate;

import java.io.IOException;
import java.util.List;

public class ClientVisitorHandler implements ClientVisitor{
    @Override
    public void visit(DisconnectionMessage message, Client client) {
        System.out.println(message.getMessage());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.getIn().close();
        client.getOut().close();
        try {
            client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(GenericMessage message, Client client) {
        System.out.println(message.getMessage());
    }

    @Override
    public void visit(LobbyInfoMessage message, Client client) {
        System.out.println(message.getMessage());
    }

    @Override
    public void visit(PingRequest message, Client client) {
        client.getOut().println(client.getGson().toJson(new PingResponse(), Message.class));
    }

    @Override
    public void visit(PlayerNumberRequest message, Client client) {
        System.out.println(message.getMessage());
        client.setCurrCommand("numberofplayers ");
    }

    @Override
    public void visit(RegisterRequest message, Client client) {
        System.out.println(message.getMessage());
        client.setCurrCommand("register ");
    }

    @Override
    public void visit(StartGameMessage message, Client client) {
        System.out.println(message.getMessage());
        List<String> cards=message.getCards();
        for(int i=0;i<cards.size();i++) {
            client.getClientModel().putIdNameLeadersMap(i+1,cards.get(i));
        }
        client.setCurrCommand("");
        client.getClientModel().setPlayersOrder(message.getPlayerOrder());
        client.getClientModel().setCurrentNickname(message.getPlayerOrder().get(0));
        for(String player : message.getPlayerOrder()){
            client.getClientModel().putBoard(player,new ClientBoard());
        }
    }

    @Override
    public void visit(NewTurnMessage message, Client client) {
        System.out.println(message.getMessage());
    }

    @Override
    public void visit(BonusResourceMessage message, Client client) {
        System.out.println(message.getMessage());
    }

    @Override
    public void visit(PendingResourcesMessage message, Client client) {
        System.out.println(message.getMessage());
    }

    @Override
    public void visit(ReconnectMessage message, Client client) {
        System.out.println(message.getMessage());
        if(client.getClientModel().getNickname().equals(message.getReconnectedNickname())){
            client.setCurrCommand("");
            //import the local Model
        }
    }

    @Override
    public void visit(DepositUpdate update, Client client) {
        Resource r=update.getRes();
        Integer idDeposit= update.getIdDeposit();
        client.getClientModel().handleUpdate(update);
    }
}

