package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.exceptions.ReconnectionException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.commands.*;
import it.polimi.ingsw.network.messages.updates.DepositUpdate;

import java.util.Map;

public class ServerVisitorHandler implements ServerVisitor {
    private Server server;
    private Map<Integer,String> chNick;
    private Map<String,Integer> nickLobby;

    public  ServerVisitorHandler(Server server){
        this.server=server;
        chNick=server.getClientHandlerNickMap();
        nickLobby=server.getNickLobbyMap();
    }
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
            String nickname = response.getNickname();
            if(nickname == null || nickname.equals("")) {
                clientHandler.send(new GenericMessage("Illegal nickname"));
                clientHandler.startTimer(50000);
                clientHandler.send(new RegisterRequest());
                return;
            }
            VirtualClient virtualClient = new VirtualClient(nickname, clientHandler);
            System.out.println("Created virtual client for " + nickname);
            try {
                clientHandler.getServer().addVirtualClient(virtualClient);
            } catch (IllegalArgumentException e){
                return;
            } catch (ReconnectionException e) {
                clientHandler.getPinger().start();
                return;
            }
            synchronized (clientHandler.getServer()) {
                if (clientHandler.getServer().getNickLobbyMap().get(virtualClient.getNickname()) == null) {
                    clientHandler.send(new PlayerNumberRequest());
                    clientHandler.startTimer(50000);
                    String jsonString = clientHandler.getIn().nextLine();
                    ServerMessage message = clientHandler.getGson().fromJson(jsonString, ServerMessage.class);
                    message.accept(clientHandler.getServerVisitorHandler(),clientHandler);
                    System.out.println(nickname + " created a new lobby for " + ((PlayerNumberResponse) message).getNPlayers() + " players");
                } else {
                   clientHandler.getPinger().start();
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
            clientHandler.getPinger().start();
        }
    }

    @Override
    public void visit(PingResponse response, ClientHandler clientHandler) {
        //System.out.println(response.getMessage() + " " + clientHandler.getId());
        clientHandler.setPing(true);
    }

    private boolean executeCommand(ClientHandler ch, Command cmd){
        int idCH=ch.getId();
        Controller c=server.searchLobby(nickLobby.get(chNick.get(idCH))).getGameController();
        return cmd.doAction(c,chNick.get(idCH));
    }

    @Override
    public void visit(ChooseLeadersCommand command, ClientHandler clientHandler) {
        boolean response=executeCommand(clientHandler,command);
        if(response){
            //update
            int nBonus=server.searchLobby(nickLobby.get(chNick.get(clientHandler.getId()))).getGameController().getNickOrderMap().get(chNick.get(clientHandler.getId()));
            clientHandler.send(new BonusResourceMessage(nBonus));
        }else{
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(ChooseBonusResourceCommand command, ClientHandler clientHandler) {
        boolean response=executeCommand(clientHandler,command);
        if(response){
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        }else{
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(PassCommand command, ClientHandler clientHandler) {
        boolean response=executeCommand(clientHandler,command);
        if(response){
            //update
            Lobby l=server.searchLobby(nickLobby.get(chNick.get(clientHandler.getId())));
            l.notifyLobby(new NewTurnMessage(l.getGameController().getGame().getCurrentNickname()));
        }else{
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(ToggleExtraProductionCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(ToggleProductionCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(ActivateProductionsCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(StrongboxPickUpCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(WarehousePickUpCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(DepositResourceCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            Lobby l=server.searchLobby(nickLobby.get(chNick.get(clientHandler.getId())));
            l.notifyLobby(new DepositUpdate(command.getId(), Resource.valueOf(command.getResourceType().label.toUpperCase())));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(MoveResourceCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(DiscardResourceCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(RevertPickUpCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(BuyCardCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(TransformWhiteCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(BuyFromMarketCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update

            int idCH=clientHandler.getId();
            Controller c=server.searchLobby(nickLobby.get(chNick.get(idCH))).getGameController();

            clientHandler.send(new PendingResourcesMessage(c.getGame().getCurrentPlayerPending()));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(ProductionUnknownCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(ExtraProductionUnknownCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(PlayLeaderCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(DiscardLeaderCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }

    @Override
    public void visit(ToggleDiscountCommand command, ClientHandler clientHandler) {
        boolean response = executeCommand(clientHandler,command);
        if (response) {
            //update
            clientHandler.send(new GenericMessage("DONE!"));
        } else {
            //illegal action
            clientHandler.send(new GenericMessage("Illegal ACTION"));
        }
    }
}
