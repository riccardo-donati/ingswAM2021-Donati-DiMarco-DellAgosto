package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.updates.*;

import java.io.IOException;
import java.util.List;

public class ClientVisitorHandler implements ClientVisitor{
    @Override
    public void visit(DisconnectionMessage message, Client client) {
        client.visualizeDisconnectionMessage(message);

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
        client.visualizeGeneralMessage(message);
    }

    @Override
    public void visit(LobbyInfoMessage message, Client client) {
        client.visualizeLobbyInfoMessage(message);
    }

    @Override
    public void visit(PingRequest message, Client client) {
        client.getOut().println(client.getGson().toJson(new PingResponse(), Message.class));
    }

    @Override
    public void visit(PlayerNumberRequest message, Client client) {
        System.out.println(message.getMessage());
        if (client instanceof CLI)
            ((CLI) client).setCurrCommand("numberofplayers ");
    }

    @Override
    public void visit(RegisterRequest message, Client client) {
        System.out.println(message.getMessage());
        if (client instanceof CLI)
            ((CLI) client).setCurrCommand("register ");
    }

    @Override
    public void visit(StartGameUpdate message, Client client) {
        message.update(client.getClientModel());
        if (client instanceof CLI)
            ((CLI) client).setCurrCommand("");
        client.getClientModel().setGamePhase(GamePhase.SETUP);

        client.visualizeStartGameUpdate();
    }

    @Override
    public void visit(NewTurnUpdate message, Client client) {
        GamePhase gamePhase = client.getClientModel().getGamePhase();
        message.update(client.getClientModel());

        client.visualizeNewTurnUpdate(gamePhase);
    }

    @Override
    public void visit(BonusResourceMessage message, Client client) {
        client.visualizeBonusResourceMessage(message);
    }

    @Override
    public void visit(PendingResourcesUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizePendingResourceUpdate(message);
    }

    @Override
    public void visit(ReconnectMessage message, Client client) {
        System.out.println(message.getMessage());
        if(client.getClientModel().getNickname().equals(message.getReconnectedNickname())){
        if (client instanceof CLI)
            ((CLI) client).setCurrCommand("");
            //import the local Model
        }
    }

    @Override
    public void visit(DepositUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizeDepositUpdate(message);
    }

    @Override
    public void visit(LorenzoUpdate message, Client client) {
        GamePhase gamePhase = client.getClientModel().getGamePhase();
        message.update(client.getClientModel());

        client.visualizeLorenzoUpdate(message, gamePhase);
    }

    @Override
    public void visit(PopeFavorUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizePopeFavorUpdate();
    }

    @Override
    public void visit(MoveResourceUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizeMoveResourceUpdate();
    }

    @Override
    public void visit(MarketUpdate message, Client client) {
        message.update(client.getClientModel());
    }

    @Override
    public void visit(ErrorMessage message, Client client) {
        client.visualizeErrorMessage(message);
    }

    @Override
    public void visit(LeadersInHandUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizeLeadersInHandUpdate();
    }

    @Override
    public void visit(SlotUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizeSlotUpdate();
    }

    @Override
    public void visit(DepositsUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizeDepositsUpdate();
    }

    @Override
    public void visit(ToggleProductionUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizeToggleProductionUpdate();
    }

    @Override
    public void visit(UnknownProductionUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizeUnknownProductionUpdate();
    }

    @Override
    public void visit(PickUpWarehouseUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizePickUpWarehouseUpdate();
    }

    @Override
    public void visit(PickUpStrongboxUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizePickUpStrongboxUpdate();
    }

    @Override
    public void visit(PlayLeaderUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizePlayLeaderUpdate();
    }

    @Override
    public void visit(ToggleDiscountUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizeToggleDiscountUpdate();
    }

    @Override
    public void visit(DiscardResourceUpdate message, Client client) {
        message.update(client.getClientModel());
    }

    @Override
    public void visit(DiscardLeaderUpdate message, Client client) {
        message.update(client.getClientModel());
    }

    @Override
    public void visit(FaithUpdate message, Client client) {
        message.update(client.getClientModel());
    }

    @Override
    public void visit(EndGameMessage message, Client client) {
        client.visualizeEndGameMessage();
    }

    @Override
    public void visit(EndGameResultUpdate message, Client client) {
        message.update(client.getClientModel());

        client.visualizeEndGameResultUpdate(message.getResult());
    }

    @Override
    public void visit(ReconnectUpdate message, Client client) {
        message.update(client.getClientModel());

        System.out.println(client.getClientModel());
        System.out.println("-----------------------------");
        System.out.println("You just reconnected!");
        System.out.println("Phase: "+client.getClientModel().getGamePhase());
        System.out.println("Turn: "+client.getClientModel().getCurrentNickname());
        StringBuilder sb=new StringBuilder();
        if(client.getClientModel().getGamePhase()==GamePhase.SETUP && client.getClientModel().getCurrentNickname().equals(client.getClientModel().getNickname()) && client.getClientModel().getCurrentBoard().getLeadersInHand().size()==0){
            //if you haven't chosen leadercards in singleplayer
            sb.append("Choose 2 leader cards: \n");
            List<String> leaderCards=message.getFourLeaderCards();
            for(int i=0;i<leaderCards.size();i++){
                sb.append(Color.ANSI_RED.escape()).append(i+1).append(Color.RESET).append(": ").append(client.getClientModel().stringifyLeaderCardFromName(leaderCards.get(i))).append("\n");
            }
        }
        if(message.getPendingResources().size()>0){
            if(message.getPendingResources().size() > 0) {
                sb.append("Deposit this pending resources:\n[");
                for (ResourceType resourceType : message.getPendingResources()) {
                    Resource resource = Resource.valueOf(resourceType.label.toUpperCase());
                    sb.append(resourceType.label).append("(").append(resource.label).append("),");
                    //mex.append(ResourceType.valueOfLabel(res.toString())+"("+Resource.valueOf()+")");
                }
                if (message.getPendingResources().size() > 0) sb.deleteCharAt(sb.toString().length() - 1);
                sb.append("]");
            }
        }
        System.out.println("-----------------------------");
        System.out.println(sb.toString());

    }
}

