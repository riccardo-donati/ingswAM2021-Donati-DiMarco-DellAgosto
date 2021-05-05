package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.*;

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
            client.getEchoSocket().close();
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
        for(String nickname : message.getNickList()){
            System.out.println("LobbyPlayer: " + nickname);
        }
    }

    @Override
    public void visit(PingRequest message, Client client) {
        //System.out.println(message.getMessage());
        client.getOut().println(client.getGson().toJson(new PingResponse(), Message.class));
    }

    @Override
    public void visit(PlayerNumberRequest message, Client client) {
        System.out.println(message.getMessage());
        int players = 0;
        try {
            players = Integer.parseInt(client.getStdIn().readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.getOut().println(client.getGson().toJson(new PlayerNumberResponse(players), Message.class));
    }

    @Override
    public void visit(RegisterRequest message, Client client) {
        System.out.println(message.getMessage());
        String userInput = null;
        try {
            userInput = client.getStdIn().readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.getOut().println(client.getGson().toJson(new RegisterResponse(userInput), Message.class));
    }

    @Override
    public void visit(StartGameMessage message, Client client) {
        System.out.println(message.getMessage());
        System.out.println("-------------\n Game order:");
        List<String> nick=message.getPlayerOrder();
        for(String n : nick){
            System.out.println(n);
        }
        System.out.println("-------------\n LeaderCards to choose:");
        List<String> leaders=message.getCards();
        for(String l : leaders){
            System.out.println(l);
        }

    }
}

