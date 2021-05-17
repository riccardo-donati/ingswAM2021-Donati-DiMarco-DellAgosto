package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
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

        StringBuilder sb=new StringBuilder();
        sb.append("------------------------------------------------------------------------------------\n");
        sb.append("Player Order:\n");
        List<String> playerOrder=message.getPlayerOrder();
        for(int i=0;i<playerOrder.size()-1;i++){
            sb.append(Color.ANSI_GREEN.escape()).append(playerOrder.get(i)).append(Color.RESET);
            sb.append(" -> ");
        }
        sb.append(Color.ANSI_GREEN.escape()).append(playerOrder.get(playerOrder.size() - 1)).append(Color.RESET+"\n");

        sb.append("Choose 2 leader cards: \n");
        List<String> leaderCards=message.getCards();
        for(int i=0;i<leaderCards.size();i++){
            sb.append(Color.ANSI_RED.escape()).append(i+1).append(Color.RESET).append(": ").append(client.getClientModel().getLeaderCardStringified(leaderCards.get(i))).append("\n");
        }
        sb.append("------------------------------------------------------------------------------------");
        System.out.println(sb.toString());
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

    @Override
    public void visit(ErrorMessage message, Client client) {
        //if cli
        System.out.println(Color.ANSI_RED.escape()+message.getMessage()+Color.RESET);
    }

    @Override
    public void visit(LeadersInHandUpdate message, Client client) {
        message.update(client.getClientModel());

        //if cli
        System.out.println(client.getClientModel().getBoards().get(client.getClientModel().getCurrentNickname()).stringifyLeaders());
    }

    @Override
    public void visit(SlotUpdate message, Client client) {
        message.update(client.getClientModel());

        //if cli
        if(client.getClientModel().getCurrentNickname().equals(client.getClientModel().getNickname())){
            System.out.println(client.getClientModel().getCurrentBoard().stringifySlots());
        }
    }

    @Override
    public void visit(WarehouseUpdate message, Client client) {
        message.update(client.getClientModel());

        //if cli
        if(client.getClientModel().getCurrentNickname().equals(client.getClientModel().getNickname())){
            System.out.println(client.getClientModel().getCurrentBoard().getDeposits());
        }
    }

    @Override
    public void visit(ToggleProductionUpdate message, Client client) {
        message.update(client.getClientModel());

        //if cli
        System.out.print("Active productions: ");
        if(client.getClientModel().getCurrentNickname().equals(client.getClientModel().getNickname())){
            for(Production p : client.getClientModel().getCurrentBoard().getActiveProductions()){
                System.out.print("["+ Utilities.stringify(p)+"]");
            }
        }
        System.out.print("\n");
    }

    @Override
    public void visit(UnknownProductionUpdate message, Client client) {
        message.update(client.getClientModel());

        //if CLI
        System.out.println(client.getClientModel().getCurrentBoard().stringifyProductions());
    }

    @Override
    public void visit(PickUpWarehouseUpdate message, Client client) {
        message.update(client.getClientModel());

        //if cli
        if(client.getClientModel().getCurrentNickname().equals(client.getClientModel().getNickname())){
            System.out.println(client.getClientModel().getCurrentBoard().getDeposits());
        }
    }

    @Override
    public void visit(PickUpStrongboxUpdate message, Client client) {
        message.update(client.getClientModel());

        //if cli
        if(client.getClientModel().getCurrentNickname().equals(client.getClientModel().getNickname())){
            System.out.println(client.getClientModel().getCurrentBoard().getDeposits());
        }
    }

    @Override
    public void visit(PlayLeaderUpdate message, Client client) {
        message.update(client.getClientModel());

        //if CLI
        if(client.getClientModel().getCurrentNickname().equals(client.getClientModel().getNickname())){
            System.out.println(client.getClientModel().getCurrentBoard().stringifyLeaders());
        }
    }

    @Override
    public void visit(HandResourcesUpdate message, Client client) {
        message.update(client.getClientModel());

        //if CLI
        if(client.getClientModel().getCurrentNickname().equals(client.getClientModel().getNickname())){
            System.out.println(client.getClientModel().getCurrentBoard().getDeposits().stringifyHandResources());
        }
    }

}

