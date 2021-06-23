package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.GUI.Controllers.WaitingLobby;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.exceptions.ReconnectionException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.commands.*;
import it.polimi.ingsw.network.messages.updates.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

public class ServerVisitorHandler implements ServerVisitor {
    private String nickname;

    public  ServerVisitorHandler(Server server){
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
                clientHandler.send(new ErrorMessage("Illegal nickname"));
                clientHandler.startTimer(50000);
                clientHandler.send(new RegisterRequest());
                return;
            }
            VirtualClient virtualClient = new VirtualClient(nickname, clientHandler);
            this.nickname=nickname;
            clientHandler.send(new WaitMessage());
            synchronized (clientHandler.getServer()) {
                try {
                    clientHandler.getServer().addVirtualClient(virtualClient);
                } catch (IllegalArgumentException e) {
                    return;
                } catch (ReconnectionException e) {
                    //clientHandler.getPinger().start();
                    clientHandler.startPinger();
                    return;
                }
                System.out.println("Created virtual client for " + nickname);

                //synchronized (clientHandler.getServer()) {
                if (clientHandler.getServer().getNickLobbyMap().get(virtualClient.getNickname()) == null) {
                    clientHandler.send(new PlayerNumberRequest());
                    clientHandler.startTimer(50000);
                    ServerMessage message;
                    try {
                        String jsonString = clientHandler.getIn().nextLine();
                        message = clientHandler.getGson().fromJson(jsonString, ServerMessage.class);
                        message.accept(clientHandler.getServerVisitorHandler(), clientHandler);
                        System.out.println(nickname + " created a new lobby for " + ((PlayerNumberResponse) message).getNPlayers() + " players");

                    } catch (NoSuchElementException ignored) {
                    }


                } else {
                    //clientHandler.getPinger().start();
                    clientHandler.startPinger();
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
            clientHandler.startPinger();
        }
    }

    @Override
    public void visit(PingResponse response, ClientHandler clientHandler) {
        //System.out.println(response.getMessage() + " " + clientHandler.getId());
        clientHandler.setPing(true);
    }
    @Override
    public void visit(ChooseLeadersCommand command, ClientHandler clientHandler) {
        try {
            command.doAction(clientHandler.getLobby(),nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalActionException | NonEmptyException | IllegalLeaderCardsException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(ChooseBonusResourceCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalActionException | FullSpaceException | UnknownNotFoundException | IllegalResourceException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(PassCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | IllegalActionException | NotYourTurnException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(ToggleExtraProductionCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalActionException | UnknownFoundException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(ToggleProductionCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | UnknownFoundException | IllegalActionException | NotYourTurnException | IllegalSlotException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(ActivateProductionsCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        List<Production> before= it.polimi.ingsw.model.Utilities.copyProductionsList(l.getCurrentActiveProductions());
        try {
            command.doAction(l,nickname);
        } catch (ResourcesNotAvailableException | IllegalResourceException | TooManyResourcesException | NotYourTurnException | UnknownFoundException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            clientHandler.send(new ResetProductionsUpdate(before));
            clientHandler.send(new RevertUpdate());
            clientHandler.send(new DepositsUpdate(l.getCurrentWarehouse(),l.getCurrentStrongbox(),l.getTurnPhase()));
        }catch (IllegalActionException e){
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(StrongboxPickUpCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | ResourcesNotAvailableException | NotYourTurnException | IllegalActionException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(WarehousePickUpCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalActionException | ResourcesNotAvailableException | NonEmptyException | DepositNotExistingException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(DepositResourceCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NotYourTurnException | IllegalCommandException | IllegalResourceException | IllegalActionException | FullSpaceException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(MoveResourceCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | IllegalResourceException | NotYourTurnException | IllegalActionException | NonEmptyException | FullSpaceException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(DiscardResourceCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NotYourTurnException | IllegalResourceException | DepositableResourceException | IllegalActionException | IllegalCommandException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(RevertPickUpCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NotYourTurnException | IllegalResourceException | IllegalActionException | FullSpaceException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(BuyCardCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NotYourTurnException | IllegalActionException | ResourcesNotAvailableException | TooManyResourcesException | IllegalSlotException | IllegalCommandException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(TransformWhiteCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NoWhiteResourceException | NotYourTurnException | IllegalResourceException | IllegalActionException | IllegalCommandException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(BuyFromMarketCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalActionException | NotYourTurnException | IllegalCommandException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(ProductionUnknownCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalResourceException | UnknownNotFoundException | IllegalActionException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(PlayLeaderCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalResourceException | RequirementNotMetException | IllegalActionException | CardNotAvailableException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        } catch (IndexOutOfBoundsException e){
            clientHandler.send(new ErrorMessage("Not in hand"));
        }
    }

    @Override
    public void visit(DiscardLeaderCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | CardNotAvailableException | NotYourTurnException | IllegalActionException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        } catch (IndexOutOfBoundsException e){
            clientHandler.send(new ErrorMessage("Not in hand"));
        }
    }

    @Override
    public void visit(ToggleDiscountCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalActionException | DiscountNotFoundException | WaitingReconnectionsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }
}
