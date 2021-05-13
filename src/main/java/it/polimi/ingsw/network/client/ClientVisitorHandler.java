package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientBoard;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.*;

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
    public void visit(StartGameUpdate message, Client client) {
        message.update(client.getClientModel());
        client.setCurrCommand("");
        client.getClientModel().setGamePhase(GamePhase.SETUP);
        //if cli mode
        System.out.println(client.getClientModel());
        System.out.println(Color.ANSI_GREEN.escape()+"SETUP PHASE BEGIN"+Color.RESET);
        System.out.println(message.getMessage());

    }

    @Override
    public void visit(NewTurnUpdate message, Client client) {
        GamePhase phasePrePass=client.getClientModel().getGamePhase();
        message.update(client.getClientModel());
        //if CLI
        if(client.getClientModel().getGamePhase()==GamePhase.ONGOING  && client.getClientModel().getNickname().equals(client.getClientModel().getCurrentNickname()))
            System.out.println(client.getClientModel());
        System.out.println(message.getMessage());
        if(phasePrePass==GamePhase.SETUP && client.getClientModel().getGamePhase()==GamePhase.ONGOING){
            System.out.println(Color.ANSI_GREEN.escape()+"NORMAL GAME PHASE BEGIN"+ Color.RESET);
        }
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
    public void visit(DepositUpdate message, Client client) {
        message.update(client.getClientModel());
        //if CLI
        if(client.getClientModel().getCurrentNickname().equals(client.getClientModel().getNickname()))
            client.getClientModel().visualizeDeposits(client.getClientModel().getNickname());
        else System.out.println(client.getClientModel().getCurrentNickname()+" "+message.getMessage());
    }

    @Override
    public void visit(LorenzoUpdate message, Client client) {
        message.update(client.getClientModel());
        //if CLI
        System.out.println(client.getClientModel());
    }

    @Override
    public void visit(FaithPathUpdate message, Client client) {
        message.update(client.getClientModel());
    }

    @Override
    public void visit(MoveResourceUpdate message, Client client) {
        message.update(client.getClientModel());

        //if CLI
        if(client.getClientModel().getNickname().equals(client.getClientModel().getCurrentNickname()))
            System.out.println(client.getClientModel().getBoards().get(client.getClientModel().getCurrentNickname()).getDeposits());
    }

    @Override
    public void visit(MarketUpdate message, Client client) {
        message.update(client.getClientModel());


    }
}

