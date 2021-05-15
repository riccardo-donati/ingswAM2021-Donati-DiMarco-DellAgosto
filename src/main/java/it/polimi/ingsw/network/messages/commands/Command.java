package it.polimi.ingsw.network.messages.commands;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.server.Controller;

public interface Command extends ServerMessage {
    void doAction(Controller c, String nickname) throws ResourcesNotAvailableException, TooManyResourcesException, IllegalActionException, NotYourTurnException, UnknownFoundException, IllegalResourceException, IllegalSlotException, IllegalCommandException, UnknownNotFoundException, FullSpaceException, NonEmptyException, IllegalLeaderCardsException, CardNotAvailableException, DepositableResourceException, RequirementNotMetException, DiscountNotFoundException, NoWhiteResourceException, DepositNotExistingException;
    boolean check();
}
