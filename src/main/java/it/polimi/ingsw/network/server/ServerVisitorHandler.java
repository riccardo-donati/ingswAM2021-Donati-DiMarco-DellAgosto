package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.exceptions.ReconnectionException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.commands.*;
import it.polimi.ingsw.network.messages.updates.*;

import java.util.List;

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
                clientHandler.send(new GenericMessage("Illegal nickname"));
                clientHandler.startTimer(50000);
                clientHandler.send(new RegisterRequest());
                return;
            }
            VirtualClient virtualClient = new VirtualClient(nickname, clientHandler);
            System.out.println("Created virtual client for " + nickname);
            this.nickname=nickname;
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
    @Override
    public void visit(ChooseLeadersCommand command, ClientHandler clientHandler) {
        try {
            command.doAction(clientHandler.getLobby(),nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalActionException | NonEmptyException | IllegalLeaderCardsException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(ChooseBonusResourceCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalActionException | FullSpaceException | UnknownNotFoundException | IllegalResourceException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(PassCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | IllegalActionException | NotYourTurnException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(ToggleExtraProductionCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalActionException | UnknownFoundException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(ToggleProductionCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | UnknownFoundException | IllegalActionException | NotYourTurnException | IllegalSlotException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(ActivateProductionsCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (ResourcesNotAvailableException | IllegalResourceException | TooManyResourcesException | IllegalActionException | NotYourTurnException | UnknownFoundException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(StrongboxPickUpCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | ResourcesNotAvailableException | NotYourTurnException | IllegalActionException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(WarehousePickUpCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalActionException | ResourcesNotAvailableException | NonEmptyException | DepositNotExistingException e) {
            e.printStackTrace();
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(DepositResourceCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NotYourTurnException | IllegalCommandException | IllegalResourceException | IllegalActionException | FullSpaceException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(MoveResourceCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | IllegalResourceException | NotYourTurnException | IllegalActionException | NonEmptyException | FullSpaceException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(DiscardResourceCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NotYourTurnException | IllegalResourceException | DepositableResourceException | IllegalActionException | IllegalCommandException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(RevertPickUpCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NotYourTurnException | IllegalResourceException | IllegalActionException | FullSpaceException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(BuyCardCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NotYourTurnException | IllegalActionException | ResourcesNotAvailableException | TooManyResourcesException | IllegalSlotException | IllegalCommandException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(TransformWhiteCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (NoWhiteResourceException | NotYourTurnException | IllegalResourceException | IllegalActionException | IllegalCommandException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(BuyFromMarketCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalActionException | NotYourTurnException | IllegalCommandException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    public void visit(ProductionUnknownCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalResourceException | UnknownNotFoundException | IllegalActionException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(ExtraProductionUnknownCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | UnknownNotFoundException | IllegalResourceException | IllegalActionException | NotYourTurnException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(PlayLeaderCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | NotYourTurnException | IllegalResourceException | RequirementNotMetException | IllegalActionException | CardNotAvailableException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(DiscardLeaderCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException | CardNotAvailableException | NotYourTurnException | IllegalActionException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }

    @Override
    public void visit(ToggleDiscountCommand command, ClientHandler clientHandler) {
        Controller l=clientHandler.getLobby();
        try {
            command.doAction(l,nickname);
        } catch (IllegalCommandException  | NotYourTurnException | IllegalActionException | DiscountNotFoundException e) {
            clientHandler.send(new ErrorMessage(e.getMessage()));
            return;
        }
        //update
        clientHandler.send(new GenericMessage("Done"));
    }
}
