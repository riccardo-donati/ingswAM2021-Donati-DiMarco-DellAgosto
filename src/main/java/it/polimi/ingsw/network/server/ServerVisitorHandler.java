package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.network.exceptions.ReconnectionException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.commands.ChooseLeadersCommand;

import java.util.ArrayList;
import java.util.Map;

public class ServerVisitorHandler implements ServerVisitor {
    @Override
    public void visit(RegisterResponse response, ClientHandler clientHandler) {
        clientHandler.stopTimer();

        if(clientHandler.isTimeout()) {
            try {
                clientHandler.closeConnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            String nickname = response.getMessage();
            if(nickname == null || nickname.equals("")) {
                clientHandler.send(new GenericMessage("Illegal nickname"));
                clientHandler.send(new RegisterRequest());
                return;
            }
            VirtualClient virtualClient = new VirtualClient(nickname, clientHandler);
            try {
                clientHandler.getServer().addVirtualClient(virtualClient);
            }catch (IllegalArgumentException e){
                return;
            } catch (ReconnectionException e) {
                //clientHandler.getPinger().start();
                return;
            }
            synchronized (clientHandler.getServer()) {
                if (clientHandler.getServer().getNickLobbyMap().get(virtualClient.getNickname()) == null) {
                    ClientMessage req = new PlayerNumberRequest();
                    clientHandler.getOut().println(clientHandler.getGson().toJson(req, ClientMessage.class));
                    clientHandler.startTimer(50000);
                    String l = clientHandler.getIn().nextLine();
                    clientHandler.handleMessage(clientHandler.getGson().fromJson(l, Message.class));
                } else {//clientHandler.getPinger().start();
                     }
            }

        }
    }

    @Override
    public void visit(PlayerNumberResponse response, ClientHandler clientHandler) {
        clientHandler.stopTimer();

        if(clientHandler.isTimeout()) {
            try {
                clientHandler.closeConnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            clientHandler.getServer().createNewLobby(response.getNPlayers(), clientHandler);
            //clientHandler.getPinger().start();
        }
    }

    @Override
    public void visit(PingResponse response, ClientHandler clientHandler) {
        System.out.println(response.getMessage() + " " + clientHandler.getId());
        clientHandler.setPing(true);
    }

    @Override
    public void visit(ChooseLeadersCommand command, ClientHandler clientHandler) {
        int idCH=clientHandler.getId();
        Server server=clientHandler.getServer();
        Map<String,Integer> nickLobby=server.getNickLobbyMap();
        Map<Integer,String> chNick=server.getClientHandlerNickMap();
        Controller c=server.searchLobby(nickLobby.get(chNick.get(idCH))).getGameController();
        boolean response=command.doAction(c.getGame(),chNick.get(idCH));
        if(response){
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        }else{
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
            Message m=new StartGameMessage(new ArrayList<>(),new ArrayList<>());
            clientHandler.send(m);
        }
    }
}
